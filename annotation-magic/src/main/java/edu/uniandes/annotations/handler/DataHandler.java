package edu.uniandes.annotations.handler;

import com.squareup.javapoet.*;
import edu.uniandes.annotations.core.Queries;
import edu.uniandes.annotations.core.Query;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Consumer;

final class DataHandler {
    static List<JavaFile> dataSection(TypeElement typeElement,
                                      List<? extends VariableElement> fields,
                                      String tableName)
            throws Exception {
        String pojoName = typeElement.getSimpleName().toString();
        String voName = "VO" + pojoName;
        String namePackage = packageName(typeElement);

        Map<String, String> queryMap = loadQuery(typeElement);

        TypeSpec[] init = init(pojoName, voName, namePackage, fields, tableName, queryMap);
        return List.of(JavaFile.builder(namePackage, init[0]).build(), JavaFile.builder(namePackage, init[1]).build());
    }

    private static TypeSpec[] init(String pojoName,
                                   String voName,
                                   String namePackage,
                                   List<? extends VariableElement> fields,
                                   String tableName,
                                   Map<String, String> queryMap) {
        TypeSpec.Builder voBuilder = TypeSpec.interfaceBuilder(voName)
                                             .addModifiers(Modifier.PUBLIC)
                                             .addSuperinterface(ClassName.get("edu.uniandes.util", "Tabulable"));

        TypeSpec.Builder pojoBuilder = TypeSpec.classBuilder(pojoName)
                                               .addModifiers(Modifier.PUBLIC)
                                               .addSuperinterface(ClassName.get(namePackage, voName));

        KeyHandler.crudQueries(tableName, fields, queryMap).forEach(pojoBuilder::addAnnotation);

        MethodSpec.Builder noArgs = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder allArgs = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder toString = MethodSpec.methodBuilder("toString")
                                                .addAnnotation(Override.class)
                                                .returns(String.class)
                                                .addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder toTable = MethodSpec.methodBuilder("toTable")
                                               .addAnnotation(Override.class)
                                               .returns(Object[][].class)
                                               .addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder headers = MethodSpec.methodBuilder("getHeaders")
                                               .returns(String[].class)
                                               .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        StringJoiner sj = new StringJoiner(" + ");
        CodeBlock.Builder header = CodeBlock.builder();
        CodeBlock.Builder body = CodeBlock.builder();

        for (VariableElement field : fields) {
            String fName = field.getSimpleName().toString();
            TypeMirror mirror = field.asType();
            TypeName fType = TypeName.get(mirror);
            String capName = capitalize(fName);

            //noinspection UnusedLabel
            toString_toTable:
            {
                header.add("$S, ", fName);
                body.add("$L, ", fName);
                sj.add("\"" + fName + "=\" + this." + fName);
            }

            //noinspection UnusedLabel
            all_args:
            {
                allArgs.addParameter(fType, fName);
                allArgs.addStatement("this.$N = $N", fName, fName);
                noArgs.addStatement("this.$N = $L", fName, generateDefaultValue(mirror));
            }

            //noinspection UnusedLabel
            getter_setter:
            {
                MethodSpec voGetter = MethodSpec.methodBuilder("get" + capName)
                                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                                .returns(fType)
                                                .build();
                voBuilder.addMethod(voGetter);

                FieldSpec pojoField = FieldSpec.builder(fType, fName, Modifier.PRIVATE).build();
                pojoBuilder.addField(pojoField);

                MethodSpec pojoGetter = MethodSpec.methodBuilder("get" + capName)
                                                  .addAnnotation(Override.class)
                                                  .addModifiers(Modifier.PUBLIC)
                                                  .returns(fType)
                                                  .addStatement("return $N", fName)
                                                  .build();
                MethodSpec pojoSetter = MethodSpec.methodBuilder("set" + capName)
                                                  .addModifiers(Modifier.PUBLIC)
                                                  .returns(TypeName.VOID)
                                                  .addParameter(fType, fName)
                                                  .addStatement("this.$N = $N", fName, fName)
                                                  .build();
                pojoBuilder.addMethod(pojoGetter);
                pojoBuilder.addMethod(pojoSetter);
            }
        }
        CodeBlock headerCodeBlock = trailingForTable(header);

        toString.addStatement("return $S + '[' + $L + ']'", pojoName, sj);
        toTable.addStatement("return new $T {new $T $L, new $T $L}",
                             Object[][].class, Object[].class, headerCodeBlock, Object[].class, trailingForTable(body));

        headers.addStatement("return new $T $L", String[].class, headerCodeBlock);

        pojoBuilder.addMethod(noArgs.build());
        pojoBuilder.addMethod(allArgs.build());

        pojoBuilder.addMethod(toString.build());
        pojoBuilder.addMethod(toTable.build());
        pojoBuilder.addMethod(headers.build());

        voBuilder.addMethod(MethodSpec.methodBuilder("toString")
                                      .addAnnotation(Override.class)
                                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                      .returns(String.class)
                                      .build());
        voBuilder.addMethod(MethodSpec.methodBuilder("toTable")
                                      .addAnnotation(Override.class)
                                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                      .returns(Object[][].class)
                                      .build());

        return new TypeSpec[]{voBuilder.build(), pojoBuilder.build()};
    }

    private static CodeBlock trailingForTable(CodeBlock.Builder member) {
        String s = member.build().toString();
        return CodeBlock.builder().add("{$L}", s.substring(0, s.length() - 2)).build();
    }

    private static String packageName(TypeElement typeElement)
            throws Exception {
        Element enclosingElement = typeElement.getEnclosingElement();
        if (Objects.requireNonNull(enclosingElement.getKind()) == ElementKind.PACKAGE)
            return ((PackageElement) enclosingElement).getQualifiedName().toString().replace("___", "");
        throw new Exception("Hay clases dentro de clases con la anotacion");
    }

    // generate a default value for a given type
    private static CodeBlock generateDefaultValue(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return switch (type.getKind()) {
                case BOOLEAN -> CodeBlock.of("false");
                case BYTE -> CodeBlock.of("(byte) 0");
                case SHORT -> CodeBlock.of("(short) 0");
                case INT -> CodeBlock.of("0");
                case LONG -> CodeBlock.of("0L");
                case CHAR -> CodeBlock.of("'\\u0000'");
                case FLOAT -> CodeBlock.of("0.0f");
                case DOUBLE -> CodeBlock.of("0.0d");
                default -> throw new AssertionError();
            };
        } else {
            return CodeBlock.of("null");
        }
    }

    private static Map<String, String> loadQuery(TypeElement element) {
        Map<String, String> result = new HashMap<>();
        Consumer<Query> consumer = q -> result.put(q.k(), q.v());
        Arrays.stream(element.getAnnotationsByType(Queries.class))
              .map(Queries::value)
              .forEach(q -> Arrays.stream(q).forEach(consumer));
        Arrays.stream(element.getAnnotationsByType(Query.class)).forEach(consumer);
        return result;
    }

    private static String capitalize(String str) {return str.substring(0, 1).toUpperCase() + str.substring(1);}
}
