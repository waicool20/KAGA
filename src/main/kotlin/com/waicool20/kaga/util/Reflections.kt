package com.waicool20.kaga.util

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

fun String.toObject(clazz: Class<*>): Any {
    when (clazz) {
        Boolean::class.java -> return toBoolean()
        Byte::class.java -> return toByte()
        Short::class.java -> return toShort()
        Int::class.java -> return toInt()
        Long::class.java -> return toLong()
        Float::class.java -> return toFloat()
        Double::class.java -> return toDouble()
        else -> return this
    }
}

fun Class<*>.toPrimitive(): Class<*> {
    when (this) {
        Boolean::class.javaObjectType -> return Boolean::class.java
        Byte::class.javaObjectType -> return Byte::class.java
        Short::class.javaObjectType -> return Short::class.java
        Int::class.javaObjectType -> return Int::class.java
        Long::class.javaObjectType -> return Long::class.java
        Float::class.javaObjectType -> return Float::class.java
        Double::class.javaObjectType -> return Double::class.java
        else -> return this
    }
}

fun Field.hasGenericType() = this.genericType is ParameterizedType

fun Field.getGenericClass(level: Int): Class<*> {
    var paramType = this.genericType as ParameterizedType
    var objType = paramType.actualTypeArguments[0]
    for (i in level downTo 1) {
        if (objType is ParameterizedType) {
            paramType = objType
            objType = paramType.actualTypeArguments[0]
        }
    }
    return Class.forName(objType.typeName.replace("<.+>".toRegex(), ""))
}
