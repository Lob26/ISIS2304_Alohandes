package edu.uniandes.annotations.handler;

import com.squareup.javapoet.AnnotationSpec;
import edu.uniandes.annotations.core.PK;
import edu.uniandes.annotations.core.Query;

import javax.lang.model.element.VariableElement;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class KeyHandler {
    private static final String[] RESERVED = {"start", "end", "size"};
    static List<AnnotationSpec> crudQueries(String tableName, List<? extends VariableElement> fields, Map<String, String> queryMap) {

        Supplier<Stream<String>> fieldNames = () -> fields.stream()
                                                          .map(VariableElement::getSimpleName)
                                                          .map(KeyHandler::clean);

        String pkWhere = fields.stream().filter(KeyHandler::isAnnotated)
                               .map(VariableElement::getSimpleName)
                               .map(KeyHandler::clean)
                               .map(name -> name + "=?")
                               .collect(Collectors.joining(" AND "));

        String updateValues = fieldNames.get().skip(1).map(name -> name + "=?").collect(Collectors.joining(","));
        String values = fieldNames.get().collect(Collectors.joining(","));
        String questions = String.join(",", Collections.nCopies(fields.size(), "?"));

        List<AnnotationSpec> list = new ArrayList<>(){{
            add(AnnotationSpec.builder(Query.class)
                              .addMember("k", "$S", "INSERT")
                              .addMember("v", "$S",
                                         "INSERT INTO " + tableName + " (" + values + ") VALUES (" + questions + ")")
                              .build());

            add(AnnotationSpec.builder(Query.class)
                          .addMember("k", "$S", "SELECT_ONE")
                          .addMember("v", "$S", "SELECT * FROM " + tableName + " WHERE " + pkWhere)
                          .build());

            add(AnnotationSpec.builder(Query.class)
                          .addMember("k", "$S", "SELECT_ALL")
                          .addMember("v", "$S", "SELECT * FROM " + tableName)
                          .build());

            add(AnnotationSpec.builder(Query.class)
                          .addMember("k", "$S", "UPDATE")
                          .addMember("v", "$S",
                                     "UPDATE " + tableName + " SET " + updateValues + " WHERE " + pkWhere)
                          .build());

            add(AnnotationSpec.builder(Query.class)
                          .addMember("k", "$S", "DELETE")
                          .addMember("v", "$S",
                                     "DELETE FROM " + tableName + " WHERE " + pkWhere)
                          .build());
            queryMap.entrySet().stream().map(e-> AnnotationSpec.builder(Query.class)
                                                               .addMember("k", "$S", e.getKey())
                                                               .addMember("v", "$S", e.getValue())
                                                               .build()).forEach(this::add);
        }};
        return list;
    }

    private static String clean(Object o) {
        String str = Objects.toString(o);
        StringBuilder result = new StringBuilder();
        boolean quotes = Arrays.asList(RESERVED).contains(str);
        if (quotes) result.append("\"");
        int bound = str.length();
        for (int i = 0; i < bound; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0 && !Character.isUpperCase(str.charAt(i - 1))) result.append('_');
                result.append(Character.toLowerCase(c));
            } else result.append(c);
        }
        if (quotes) result.append("\"");
        return result.toString();
    }

    private static boolean isAnnotated(VariableElement field) {
        return field.getAnnotation(PK.class) != null;
    }

}
