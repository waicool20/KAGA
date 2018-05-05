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

import javafx.application.Platform.runLater
import javafx.scene.control.TextArea
import java.io.OutputStream

abstract class LineBufferedOutputStream : OutputStream() {
    private val buffer = StringBuffer()

    override fun write(byte: Int) {
        val char = byte.toChar()
        buffer.append(char)
        if (char == '\n') {
            flush()
        }
    }

    override fun flush() {
        writeLine(buffer.toString())
        buffer.setLength(0)
    }

    abstract fun writeLine(line: String)
}

class TextAreaOutputStream(private val console: TextArea, private val maxLines: Int = 1000) : LineBufferedOutputStream() {
    override fun writeLine(line: String) {
        runLater {
            if (line.contains("\u001b[2J\u001b[H")) {
                console.clear()
                return@runLater
            }
            if (console.text.count { it == '\n' } >= maxLines) {
                console.deleteText(0, console.text.indexOf('\n') + 1)
            }
            console.appendText(line.replace(Regex("\\u001b\\[.+?m"), ""))
        }
    }
}

class LineListenerOutputStream : LineBufferedOutputStream() {
    override fun writeLine(line: String) {
        LoggingEventBus.publish(line.trim())
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
