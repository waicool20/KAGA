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

import com.waicool20.kaga.Kaga
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

class TextAreaOutputStream(private val console: TextArea, private val maxLines: Int = 1000) : LineBufferedOutputStream() {
    init {
        console.textProperty().addListener { obs, oldVal, newVal ->
            Platform.runLater {
                console.scrollTop = Double.MAX_VALUE
            }
        }
    }

    override fun writeLine(line: String) {
        Platform.runLater {
            if (line.contains("\u001b[2J\u001b[H")) {
                console.clear()
                return@runLater
            }
            console.text = appendLineWithLimit(console.text, line, maxLines)
                    .replace("\\u001b\\[.+?m".toRegex(), "")
            console.appendText("")
        }
    }
}

class LineListenerOutputStream : LineBufferedOutputStream() {
    override fun writeLine(line: String) {
        Kaga.LOG = if (line.contains("\u001b[2J\u001b[H")) "" else appendLineWithLimit(Kaga.LOG, line)
                .replace("\\u001b\\[.+?m".toRegex(), "")
        LoggingEventBus.publish(line.replaceFirst("\n", ""))
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

private fun appendLineWithLimit(target: String, line: String, maxLines: Int = 1000): String {
    var string = target
    if (string.count { it == '\n' } >= maxLines) {
        string = string.replaceFirst(".+?\n".toRegex(), "")
    }
    return string.plus(line)
}
