package com.waicool20.kaga.config;

import com.waicool20.kaga.util.ReflectionUtil;
import org.ini4j.Ini;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class IniUtils {

    public static String getString(Ini.Section section, String key, String defaultValue) {
        String value = section.get(key);
        return value == null ? defaultValue : value;
    }

    public static <T extends Object> T sectionToObject(Ini.Section section, Class<T> object) {
        try {
            List<Object> args = new ArrayList<>();
            List<Class> argClasses = new ArrayList<>();
            for (Field field : object.getDeclaredFields()) {
                if (field.isAnnotationPresent(IniConfig.class)) {
                    IniConfig config = field.getAnnotation(IniConfig.class);
                    if (!config.read()) {
                        continue;
                    }

                    Class fieldObject = ReflectionUtil
                        .getPrimitive(field.getType().getMethod("getValue").getReturnType());
                    if (ReflectionUtil.hasGenericType(field)) {
                        Class genericClass = ReflectionUtil.getGenericClass(field, 0);
                        boolean isList = List.class.isAssignableFrom(field.getType());
                        if (isList || Set.class.isAssignableFrom(field.getType())) {
                            Collection<Object> collection =
                                isList ? new ArrayList<>() : new LinkedHashSet<>();
                            for (String value : section.get(config.key()).replaceAll("-", "_")
                                .split("\\s?,\\s?")) {
                                if (!value.isEmpty()) {
                                    if (genericClass.isEnum()) {
                                        Object enumObject =
                                            Enum.valueOf((Class<? extends Enum>) genericClass,
                                                value.toUpperCase());
                                        collection.add(enumObject);
                                    } else {
                                        collection.add(
                                            ReflectionUtil.stringToObject(value, genericClass));
                                    }
                                }
                            }
                            argClasses.add(isList ? List.class : Set.class);
                            args.add(collection);
                        } else {
                            if (genericClass.isEnum()) {
                                String enumName =
                                    section.get(config.key()).replaceAll("-", "_").toUpperCase();
                                Object enumObject =
                                    Enum.valueOf((Class<? extends Enum>) genericClass, enumName);
                                argClasses.add(ReflectionUtil.getPrimitive(genericClass));
                                args.add(enumObject);
                            }
                        }
                    } else {
                        Object value = section.get(config.key(), fieldObject);
                        argClasses.add(fieldObject);
                        args.add(value);
                    }
                }
            }
            return object.getConstructor(argClasses.toArray(new Class[argClasses.size()]))
                .newInstance(args.toArray(new Object[args.size()]));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void objectToSection(Ini.Section section, Object object) {
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(IniConfig.class)) {
                    IniConfig config = field.getAnnotation(IniConfig.class);
                    field.setAccessible(true);
                    Object prop = field.get(object);
                    section.add(config.key(),
                        prop.getClass().getMethod("get").invoke(prop).toString()
                            .replaceAll("\\[|]", ""));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
