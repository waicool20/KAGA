package com.waicool20.kaga.util

import org.ini4j.Profile
import java.util.*

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class IniConfig(val key: String, val read: Boolean = true)

fun <T : Any> Profile.Section.toObject(obj: Class<T>): T? {
    val args = mutableListOf<Any>()
    val argClasses = mutableListOf<Class<*>>()
    obj.declaredFields.forEach { field ->
        run {
            val config = field.annotations.find { it is IniConfig } as? IniConfig
            if (config != null && config.read) {
                val fieldObject = field.type.getMethod("getValue").returnType.toPrimitive()
                if (field.hasGenericType()) {
                    val genericClass = field.getGenericClass(0)
                    val isList = List::class.java.isAssignableFrom(field.type)
                    if (isList || Set::class.java.isAssignableFrom(field.type)) {
                        val collection: MutableCollection<Any> = if (isList) ArrayList() else LinkedHashSet()
                        this[config.key]?.replace("-", "_")?.split("\\s?,\\s?".toRegex())?.forEach { value ->
                            run {
                                if (value.isNotEmpty()) {
                                    if (genericClass.isEnum) {
                                        val enumObj = genericClass.enumConstants.find { it.toString().equals(value, true) }
                                        if (enumObj != null) collection.add(enumObj)
                                    } else {
                                        collection.add(value.toObject(genericClass))
                                    }
                                }
                            }
                        }
                        argClasses.add(if (isList) List::class.java else Set::class.java)
                        args.add(collection)
                    } else {
                        if (genericClass.isEnum) {
                            val enumName = this[config.key]?.replace("-", "_")?.toUpperCase() ?: ""
                            val enumObj = genericClass.enumConstants.find { it.toString().equals(enumName, true) }
                            if (enumObj != null) {
                                argClasses.add(genericClass)
                                args.add(enumObj)
                            }
                        }
                    }
                } else {
                    val value = this.get(config.key, fieldObject)
                    argClasses.add(fieldObject)
                    args.add(value)
                }
            }
        }
    }
    return obj.getConstructor(*argClasses.toTypedArray()).newInstance(*args.toTypedArray())
}

fun Profile.Section.fromObject(obj: Any) {
    obj.javaClass.declaredFields.forEach { field ->
        run {
            val config = field.annotations.find { it is IniConfig } as? IniConfig
            if (config != null) {
                field.isAccessible = true
                val prop = field.get(obj)
                this.add(config.key, prop.javaClass.getMethod("get").invoke(prop).toString().replace("\\[|]".toRegex(), ""))
            }
        }
    }
}
