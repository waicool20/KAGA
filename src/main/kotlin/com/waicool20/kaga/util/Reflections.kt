/*
 * GPLv3 License
 *
 *  Copyright (c) KAGA by waicool20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.waicool20.kaga.util

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

fun String.toObject(clazz: Class<*>): Any {
    return when (clazz) {
        Boolean::class.javaObjectType -> toBoolean()
        Byte::class.javaObjectType -> toByte()
        Short::class.javaObjectType -> toShort()
        Int::class.javaObjectType -> toInt()
        Long::class.javaObjectType -> toLong()
        Float::class.javaObjectType -> toFloat()
        Double::class.javaObjectType -> toDouble()
        else -> this
    }
}

fun String.toObject(clazz: KClass<*>) = toObject(clazz.java)

//<editor-fold desc="JVM Reflection">

fun Class<*>.toPrimitive(): Class<*> {
    return when (this) {
        Boolean::class.javaObjectType -> Boolean::class.java
        Byte::class.javaObjectType -> Byte::class.java
        Short::class.javaObjectType -> Short::class.java
        Int::class.javaObjectType -> Int::class.java
        Long::class.javaObjectType -> Long::class.java
        Float::class.javaObjectType -> Float::class.java
        Double::class.javaObjectType -> Double::class.java
        else -> this
    }
}

fun Field.hasGenericType() = genericType is ParameterizedType

fun Field.getGenericClass(level: Int = 0): Class<*> {
    var paramType = genericType as ParameterizedType
    var objType = paramType.actualTypeArguments[0]
    for (i in level downTo 0) {
        if (objType is ParameterizedType) {
            paramType = objType
            objType = paramType.actualTypeArguments[0]
        }
    }
    return objType as Class<*>
}

//</editor-fold>

//<editor-fold desc="Kotlin Reflection">
fun KProperty<*>.hasGenericType() = returnType.jvmErasure.typeParameters.isNotEmpty()

fun KProperty<*>.getGenericType(level: Int = 0): KType {
    var type = returnType
    for (i in level downTo 0) {
        type.arguments.firstOrNull()?.type?.let { type = it }
    }
    return type
}

fun KProperty<*>.getGenericClass(level: Int = 0) = getGenericType(level).jvmErasure

fun KClass<*>.isEnum() = starProjectedType.isEnum()
fun KType.isEnum() = isSubtypeOf(Enum::class.starProjectedType)

fun KClass<*>.enumValueOf(string: String): Any? = java.enumConstants.find {
    it.toString().equals(string, true) ||
            it.toString().equals(string.replace("_", "-"), true)
}
//</editor-fold>
