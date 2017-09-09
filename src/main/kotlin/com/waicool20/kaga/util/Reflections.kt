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

fun Field.getGenericClass(level: Int): Class<*> {
    var paramType = genericType as ParameterizedType
    var objType = paramType.actualTypeArguments[0]
    for (i in level downTo 1) {
        if (objType is ParameterizedType) {
            paramType = objType
            objType = paramType.actualTypeArguments[0]
        }
    }
    return objType as Class<*>
}
