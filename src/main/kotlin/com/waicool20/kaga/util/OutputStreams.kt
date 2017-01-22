package com.waicool20.kaga.util

import javafx.application.Platform
import javafx.scene.control.TextArea
import java.io.OutputStream

abstract class LineBufferedOutputStream : OutputStream() {
    var buffer = mutableListOf<Char>()

    override fun write(byte: Int) {
        val char = byte.toChar()
        buffer.add(char)
        if (char == '\n') {
            flush()
        }
    }

    override fun flush() {
        writeLine(buffer.joinToString(separator = ""))
        buffer.clear()
    }

    abstract fun writeLine(line: String)
}

class TextAreaOutputStream(private val console: TextArea, private val maxLines: Int) : LineBufferedOutputStream() {
    init {
        console.textProperty().addListener { obs, oldVal, newVal -> run {
            console.scrollTop = Double.MAX_VALUE
        }}
    }
    override fun writeLine(line: String) {
        Platform.runLater {
            if (line.contains("\u001b[2J\u001b[H")) {
                console.clear()
                return@runLater
            }
            var current = console.text
            if (current.split("\n").size > maxLines) {
                current = current.replaceFirst(".+?\n".toRegex(), "")
            }
            current += line
            console.text = current.replace("\\u001b\\[.+?m".toRegex(), "")
            console.appendText("")
        }
    }
}

class LineListenerOutputStream : LineBufferedOutputStream() {
    override fun writeLine(line: String) {
        // TODO implement line listener
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
