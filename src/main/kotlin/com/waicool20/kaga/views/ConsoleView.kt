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

package com.waicool20.kaga.views

import com.waicool20.kaga.util.TeeOutputStream
import com.waicool20.kaga.util.TextAreaOutputStream
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.Tooltip
import javafx.scene.input.Clipboard
import javafx.scene.layout.GridPane
import javafx.util.Duration
import tornadofx.*
import java.io.PrintStream
import java.util.*


class ConsoleView : View() {
    override val root: GridPane by fxml("/views/console.fxml", hasControllerAttribute = true)
    private val consoleTextArea: TextArea by fxid()
    private val copyButton: Button by fxid()
    private var outStream: PrintStream
    private var errStream: PrintStream

    init {
        val textArea = TextAreaOutputStream(consoleTextArea)
        outStream = PrintStream(TeeOutputStream(System.out, textArea))
        errStream = PrintStream(TeeOutputStream(System.err, textArea))
        System.setOut(outStream)
        System.setErr(errStream)
    }

    @FXML private fun onClear() = consoleTextArea.clear()

    @FXML private fun onCopyAll() {
        Clipboard.getSystemClipboard().putString(consoleTextArea.text)
        val tooltip = Tooltip("Copied everything!")
        with(copyButton) {
            val bounds = localToScene(boundsInLocal)
            tooltip.show(copyButton, bounds.maxX + scene.window.x, bounds.minY + scene.window.y)
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    val timeline = Timeline()
                    timeline.keyFrames.add(KeyFrame(Duration.millis(500.0), KeyValue(tooltip.opacityProperty(), 0)))
                    timeline.play()
                }
            }
        }, 500L)
    }

    @FXML private fun toTop() {
        consoleTextArea.scrollTop = 0.0
    }

    @FXML private fun toBottom() {
        consoleTextArea.scrollTop = Double.MAX_VALUE
    }
}
