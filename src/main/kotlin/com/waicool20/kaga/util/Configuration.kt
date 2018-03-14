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

import javafx.beans.value.WritableListValue
import javafx.beans.value.WritableSetValue
import javafx.beans.value.WritableValue
import org.ini4j.Profile
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class IniConfig(val key: String, val shouldRead: Boolean = true)

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Profile.Section.toObject(): T {
    val targetObjectClass = T::class
    val targetObjectInstance = T::class.java.newInstance()
    targetObjectClass.declaredMembers.filter { it.returnType.isSubtypeOf(WritableValue::class.starProjectedType) }
            .mapNotNull { it as? KProperty1<T, WritableValue<*>> }
            .forEach { kProperty ->
                val property = kProperty.get(targetObjectInstance)
                val iniConfig = kProperty.findAnnotation<IniConfig>().takeIf { it != null && it.shouldRead }
                        ?: return@forEach
                if (kProperty.hasGenericType()) {
                    val isList = kProperty.returnType.isSubtypeOf(List::class.starProjectedType)
                    val isSet = kProperty.returnType.isSubtypeOf(Set::class.starProjectedType)

                    val genericKClass = kProperty.getGenericClass()

                    when {
                        isList || isSet -> {
                            val items = this[iniConfig.key]?.split(Regex("\\s?,\\s?"))
                                    ?.filter { it.isNotBlank() }
                                    ?.mapNotNull { value ->
                                        if (genericKClass.isEnum()) {
                                            genericKClass.enumValueOf(value)
                                        } else {
                                            value.toObject(genericKClass)
                                        }
                                    }?.toList() ?: mutableListOf()
                            (property as? WritableListValue<Any>)?.apply {
                                clear()
                                items.forEach { add(it) }
                            }
                            (property as? WritableSetValue<Any>)?.apply {
                                clear()
                                items.forEach { add(it) }
                            }
                        }
                        genericKClass.isEnum() -> {
                            val enumName = this[iniConfig.key]?.replace("-", "_")?.toUpperCase()
                                    ?: ""
                            genericKClass.enumValueOf(enumName)?.let { property.value = it }
                        }
                    }
                } else {
                    val value = try {
                        this.get(iniConfig.key, property::class.java.getMethod("getValue").returnType.toPrimitive())
                    } catch (e: Exception) {
                        when (e.cause) {
                            is NumberFormatException -> 0
                            else -> throw e
                        }
                    }
                    value?.let { property.value = it }
                }
            }
    return targetObjectInstance
}

@Suppress("UNCHECKED_CAST")
fun Profile.Section.fromObject(obj: Any) {
    obj::class.declaredMembers.filter { it.returnType.isSubtypeOf(WritableValue::class.starProjectedType) }
            .mapNotNull { it as? KProperty1<Any, WritableValue<*>> }
            .forEach { kProperty ->
                val property = kProperty.get(obj)
                val iniConfig = kProperty.findAnnotation<IniConfig>().takeIf { it != null } ?: return@forEach
                add(iniConfig.key, property.value.toString().replace("[\\[\\]]".toRegex(), ""))
            }
}

