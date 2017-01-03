package com.waicool20.kaga.util

import javafx.application.Platform
import javafx.scene.control.TextArea
import java.io.OutputStream


class TextAreaOutputStream(private val console: TextArea, private val maxLines: Int) : OutputStream() {

    override fun write(b: Int) {
        val c = b.toChar()
        when (c) {
            '\u001B' -> appendText("<ESC>")
            else -> appendText(c.toString())
        }
    }

    private fun appendText(string: String) {
        Platform.runLater {
            var current = console.text
            if (current.split("\n").size > maxLines) {
                current = current.replaceFirst(".+?\n".toRegex(), "")
            }
            current += string
            console.text = current.replace("<ESC>\\[.+?m".toRegex(), "")
            console.scrollTop = Double.MAX_VALUE
        }
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
