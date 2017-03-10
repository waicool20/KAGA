package com.waicool20.kaga.util

object LoggingEventBus {
    private val listeners = mutableMapOf<Regex, (match: MatchResult) -> Unit>()

    fun subscribe(regex: Regex, listener: (match: MatchResult) -> Unit) =
            listeners.put(regex, listener)

    fun publish(string: String) {
        listeners.forEach {
            val matches = it.key.matchEntire(string)
            if (matches != null) it.value.invoke(matches)
        }
    }
}
