package edu.uniandes.___data;

import edu.uniandes.annotations.core.PK;

import java.util.Arrays;

public final class SeqUtils {
    private SeqUtils() {}

    public static String sequence(String pojo) {
        try {
            Class<?> entity = Class.forName("edu.uniandes.___data." + pojo);
            String[] strings = Arrays.stream(entity.getDeclaredFields())
                                     .filter(f -> f.isAnnotationPresent(PK.class))
                                     .map(f -> f.getAnnotation(PK.class))
                                     .map(PK::sequence)
                                     .toArray(String[]::new);
            return strings[0];
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
