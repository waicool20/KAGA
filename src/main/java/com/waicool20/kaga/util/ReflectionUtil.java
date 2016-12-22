package com.waicool20.kaga.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {
    public static boolean hasGenericType(Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }

    public static Class getGenericClass(Field field, int level) throws ClassNotFoundException {
        ParameterizedType paramType = ((ParameterizedType) field.getGenericType());
        Type objType = paramType.getActualTypeArguments()[0];
        for (int i = level; i > 0; i--) {
            if (objType instanceof ParameterizedType) {
                paramType = (ParameterizedType) objType;
                objType = paramType.getActualTypeArguments()[0];
            }
        }
        return Class.forName(objType.getTypeName().replaceAll("<.+>", ""));
    }

    public static Object stringToObject(String value, Class clazz) {
        if (Boolean.class == clazz)
            return Boolean.parseBoolean(value);
        if (Byte.class == clazz)
            return Byte.parseByte(value);
        if (Short.class == clazz)
            return Short.parseShort(value);
        if (Integer.class == clazz)
            return Integer.parseInt(value);
        if (Long.class == clazz)
            return Long.parseLong(value);
        if (Float.class == clazz)
            return Float.parseFloat(value);
        if (Double.class == clazz)
            return Double.parseDouble(value);
        return value;
    }

    public static Class getPrimitive(Class clazz) {
        if (clazz == Integer.class) {
            return int.class;
        } else if (clazz == Boolean.class) {
            return boolean.class;
        } else if (clazz == Float.class) {
            return float.class;
        } else if (clazz == Double.class) {
            return double.class;
        } else if (clazz == Long.class) {
            return long.class;
        } else {
            return clazz;
        }
    }
}
