package com.waicool20.kaga.util

import java.io.BufferedReader
import java.io.InputStreamReader


class StreamGobbler(val process: Process?) {
    fun run() {
        val handler = Thread.UncaughtExceptionHandler { thread, throwable ->
            if (throwable.message != "Stream closed") throw throwable // Ignore stream closed errors
        }
        with(Thread { BufferedReader(InputStreamReader(process?.inputStream)).forEachLine(::println) }) {
            uncaughtExceptionHandler = handler
            start()
        }
        with(Thread { BufferedReader(InputStreamReader(process?.errorStream)).forEachLine(::println) }) {
            uncaughtExceptionHandler = handler
            start()
        }
    }
}
