package com.waicool20.kaga.util

fun Collection<String>.getIgnoreCase(string: String): String? {
    forEach { item ->
        if (item.equals(string, true)) return item
    }
    return null
}

fun Collection<String>.containsIgnoreCase(string: String): Boolean {
    forEach { item ->
        if (item.equals(string, true)) return true
    }
    return false
}


