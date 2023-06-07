package edu.uniandes.___data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public final class DataUtils {
    private DataUtils() {}

    public static <T> T fromTable(Class<T> clazz, List<Object[]> data)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T obj = clazz.getConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < data.get(0).length; i++) {
            String fieldName = data.get(0)[i].toString();
            String fieldValue = data.get(1)[i].toString();
            Arrays.stream(fields).filter(field -> field.getName().equals(fieldName)).findFirst().ifPresent(field -> {
                field.setAccessible(true);
                try {field.set(obj, fieldValue);}
                catch (IllegalAccessException e) {e.printStackTrace();}
            });
        }
        return obj;
    }
}
