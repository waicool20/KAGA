package com.waicool20.kaga.util

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

fun String.toObject(clazz: Class<*>): Any {
    when (clazz) {
        Boolean::class.javaObjectType -> return toBoolean()
        Byte::class.javaObjectType -> return toByte()
        Short::class.javaObjectType -> return toShort()
        Int::class.javaObjectType -> return toInt()
        Long::class.javaObjectType -> return toLong()
        Float::class.javaObjectType -> return toFloat()
        Double::class.javaObjectType -> return toDouble()
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
