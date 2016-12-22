package com.waicool20.kaga.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectBindings {
    public static Map<ObjectProperty<?>, List<ObjectProperty<?>>> objectBindings = new HashMap<>();

    public static void bindBidirectionally(ObjectProperty<Integer> objectProperty,
        IntegerProperty integerProperty) {
        ObjectProperty<Integer> integerObjectProperty = integerProperty.asObject();
        objectProperty.bindBidirectional(integerObjectProperty);
        if (objectBindings.containsKey(objectProperty)) {
            objectBindings.get(objectProperty).add(integerObjectProperty);
        } else {
            objectBindings.put(objectProperty, Arrays.asList(integerObjectProperty));
        }
    }

    public static void bindBidirectionally(ObjectProperty<Float> objectProperty,
        FloatProperty floatProperty) {
        ObjectProperty<Float> floatObjectProperty = floatProperty.asObject();
        objectProperty.bindBidirectional(floatObjectProperty);
        if (objectBindings.containsKey(objectProperty)) {
            objectBindings.get(objectProperty).add(floatObjectProperty);
        } else {
            objectBindings.put(objectProperty, Arrays.asList(floatObjectProperty));
        }
    }

    public static void bindBidirectionally(ObjectProperty<Double> objectProperty,
        DoubleProperty doubleProperty) {
        ObjectProperty<Double> doubleObjectProperty = doubleProperty.asObject();
        objectProperty.bindBidirectional(doubleObjectProperty);
        if (objectBindings.containsKey(objectProperty)) {
            objectBindings.get(objectProperty).add(doubleObjectProperty);
        } else {
            objectBindings.put(objectProperty, Arrays.asList(doubleObjectProperty));
        }
    }
}
