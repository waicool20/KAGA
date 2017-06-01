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

import org.ini4j.Profile
import java.util.*

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class IniConfig(val key: String, val shouldRead: Boolean = true)

fun <T : Any> Profile.Section.toObject(obj: Class<T>): T? {
    val args = mutableListOf<Any>()
    val argClasses = mutableListOf<Class<*>>()
    obj.declaredFields.forEach { field ->
        val config = field.annotations.find { it is IniConfig } as? IniConfig
        if (config != null && config.shouldRead) {
            val fieldObject = field.type.getMethod("getValue").returnType.toPrimitive()
            if (field.hasGenericType()) {
                val genericClass = field.getGenericClass(0)
                val isList = List::class.java.isAssignableFrom(field.type)
                if (isList || Set::class.java.isAssignableFrom(field.type)) {
                    val collection: MutableCollection<Any> = if (isList) ArrayList() else LinkedHashSet()
                    this[config.key]?.split("\\s?,\\s?".toRegex())
                            ?.filter { it.isNotEmpty() }
                            ?.forEach { value ->
                                if (genericClass.isEnum) {
                                    genericClass.enumConstants.find {
                                        it.toString().equals(value, true) ||
                                                it.toString().equals(value.replace("_", "-"), true)
                                    }?.let { collection.add(it) }
                                } else {
                                    collection.add(value.toObject(genericClass))
                                }
                            }
                    argClasses.add(if (isList) List::class.java else Set::class.java)
                    args.add(collection)
                } else {
                    if (genericClass.isEnum) {
                        val enumName = this[config.key]?.replace("-", "_")?.toUpperCase() ?: ""
                        genericClass.enumConstants.find { it.toString().equals(enumName, true) }?.let {
                            argClasses.add(genericClass)
                            args.add(it)
                        }
                    }
                }
            } else {
                var value: Any
                try {
                    value = this.get(config.key, fieldObject)
                } catch (e: Exception) {
                    when (e.cause) {
                        is NumberFormatException -> value = 0
                        else -> throw e
                    }
                }
                argClasses.add(fieldObject)
                args.add(value)
            }
        }
    }
    return obj.getConstructor(*argClasses.toTypedArray()).newInstance(*args.toTypedArray())
}

fun Profile.Section.fromObject(obj: Any) = obj.javaClass.declaredFields.forEach { field ->
    (field.annotations.find { it is IniConfig } as? IniConfig)?.let {
        field.isAccessible = true
        val prop = field.get(obj)
        this.add(it.key, prop.javaClass.getMethod("get").invoke(prop).toString().replace("\\[|]".toRegex(), ""))
    }
}

