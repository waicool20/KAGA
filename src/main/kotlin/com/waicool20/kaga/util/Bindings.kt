package com.waicool20.kaga.util

import javafx.beans.property.DoubleProperty
import javafx.beans.property.FloatProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner

private object Bindings {
    var objectBindings = mutableMapOf<ObjectProperty<*>, MutableList<ObjectProperty<*>>>()
}

fun Spinner<Int>.bind(integerProperty: IntegerProperty, readOnly: Boolean = false) =
        bind(this.valueFactory.valueProperty(), integerProperty.asObject(), readOnly)

fun Spinner<Float>.bind(floatProperty: FloatProperty, readOnly: Boolean = false) =
        bind(this.valueFactory.valueProperty(), floatProperty.asObject(), readOnly)

fun Spinner<Double>.bind(doubleProperty: DoubleProperty, readOnly: Boolean = false) =
        bind(this.valueFactory.valueProperty(), doubleProperty.asObject(), readOnly)

fun ComboBox<Int>.bind(integerProperty: IntegerProperty, readOnly: Boolean = false) =
        bind(this.valueProperty(), integerProperty.asObject(), readOnly)

fun ComboBox<Float>.bind(floatProperty: FloatProperty, readOnly: Boolean = false) =
        bind(this.valueProperty(), floatProperty.asObject(), readOnly)

fun ComboBox<Double>.bind(doubleProperty: DoubleProperty, readOnly: Boolean = false) =
        bind(this.valueProperty(), doubleProperty.asObject(), readOnly)

private fun <T> bind(objectProperty: ObjectProperty<T>, objectProperty1: ObjectProperty<T>, readOnly: Boolean = false) {
    if (readOnly) objectProperty.bind(objectProperty1) else objectProperty.bindBidirectional(objectProperty1)
    Bindings.objectBindings.getOrPut(objectProperty, { mutableListOf(objectProperty1) })
}




