package com.waicool20.kaga.util

import java.io.BufferedReader
import java.io.InputStreamReader


class StreamGobbler(val process: Process?) {
    fun run() {
        val readOut = Thread { BufferedReader(InputStreamReader(process?.inputStream)).forEachLine(::println) }
        val readErr = Thread { BufferedReader(InputStreamReader(process?.errorStream)).forEachLine(::println) }
        readOut.start()
        readErr.start()
    }
}
