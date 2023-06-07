package edu.uniandes.annotations.handler;

import edu.uniandes.annotations.core.Query;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TableHandler {
    public static Map<String, String> getQueries(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotationsByType(Query.class))
                     .collect(Collectors.toMap(Query::k, Query::v, (a, b) -> b));
    }
}
