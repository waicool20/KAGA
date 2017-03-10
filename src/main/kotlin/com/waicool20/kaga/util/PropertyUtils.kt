package com.waicool20.kaga.util

import javafx.beans.property.DoubleProperty
import javafx.beans.property.FloatProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.LongProperty

fun IntegerProperty.inc() = set(value + 1)

fun IntegerProperty.dec() = set(value - 1)

fun FloatProperty.inc() = set(value + 1)

fun FloatProperty.dec() = set(value - 1)

fun DoubleProperty.inc() = set(value + 1)

fun DoubleProperty.dec() = set(value - 1)

fun LongProperty.inc() = set(value + 1)

fun LongProperty.dec() = set(value - 1)
