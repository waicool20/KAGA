package com.waicool20.kaga.util

import javafx.application.Platform
import javafx.scene.control.TextArea
import java.io.OutputStream


class TextAreaOutputStream(private val console: TextArea, private val maxLines: Int) : OutputStream() {
    var buffer = mutableListOf<Char>()

    override fun write(byte: Int) {
        val char = byte.toChar()
        buffer.add(char)
        if (char == '\n') {
            flush()
        }
    }

    override fun flush() {
        val newLine = buffer.joinToString(separator = "")
        Platform.runLater {
            if (newLine.contains("\u001b[2J\u001b[H")) {
                console.clear()
                return@runLater
            }
            var current = console.text
            if (current.split("\n").size > maxLines) {
                current = current.replaceFirst(".+?\n".toRegex(), "")
            }
            current += newLine
            console.text = current.replace("\\u001b\\[.+?m".toRegex(), "")
            console.scrollTop = Double.MAX_VALUE
        }
        buffer.clear()
    }
}

class TeeOutputStream(val main: OutputStream, val branch: OutputStream) : OutputStream() {
    override fun write(int: Int) {
        main.write(int)
        branch.write(int)
    }

    override fun flush() {
        super.flush()
        main.flush()
        branch.flush()
    }

    override fun close() {
        super.close()
        main.close()
        branch.close()
    }
}
