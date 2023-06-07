package edu.uniandes.annotations.handler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import edu.uniandes.annotations.core.Data;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"edu.uniandes.annotations.core.Data", "edu.uniandes.annotations.core.PK"})
@SupportedSourceVersion(SourceVersion.RELEASE_18)
public class EntityManager extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    private final List<JavaFile> classes = new LinkedList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = this.processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Data.class).stream()
                .filter(element -> element.getKind().isClass())
                .map(TypeElement.class::cast)
                .forEach(this::generate);

        classesBuild();
        return true;
    }

    private void classesBuild() {
        for (JavaFile file : classes) try {
            file.writeTo(filer);
            messager.printMessage(Diagnostic.Kind.NOTE, file.packageName + "." + file.typeSpec.name + " escrita");
        } catch (IOException e) {
            if (!e.getMessage().contains("Attempt to recreate a file for type"))
                messager.printMessage(Diagnostic.Kind.ERROR, "Class\n" + e.getMessage());
        }
    }

    private void generate(TypeElement typeElement) {
        List<VariableElement> fields = typeElement.getEnclosedElements().stream()
                                                  .filter(EntityManager::check)
                                                  .map(VariableElement.class::cast)
                                                  .toList();

        String tableName = typeElement.getAnnotation(Data.class).value();
        try {
            classes.addAll(DataHandler.dataSection(typeElement, fields, tableName));
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static boolean check(Element e) {
        return e.getKind() == ElementKind.FIELD && !e.getModifiers().contains(Modifier.STATIC)
               && !e.getModifiers().contains(Modifier.TRANSIENT);
    }
}
