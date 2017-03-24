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

package com.waicool20.kaga.handlers

import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Spinner
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import java.util.concurrent.TimeUnit

class KeyboardIncrementHandler : EventHandler<KeyEvent> {
    override fun handle(keyEvent: KeyEvent) {
        val target = keyEvent.target
        if (target is Spinner<*>) {
            when (keyEvent.code) {
                KeyCode.UP -> target.increment()
                KeyCode.DOWN -> target.decrement()
                else -> Unit
            }
        }
    }
}

class MouseIncrementHandler(delayMillis: Long, incrementInterval: Long) : EventHandler<MouseEvent> {
    private var startTimestamp: Long = 0
    private var isIncrementing: Boolean = false
    private var spinner: Spinner<*>? = null

    private val delay = 1000000L * delayMillis
    private val timer = object : AnimationTimer() {
        override fun handle(now: Long) {
            if (now - startTimestamp >= delay) {
                if (isIncrementing) {
                    spinner?.increment()
                } else {
                    spinner?.decrement()
                }
                TimeUnit.MILLISECONDS.sleep(incrementInterval)
            }
        }
    }

    override fun handle(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            if (event.eventType == MouseEvent.MOUSE_PRESSED) {
                val node = event.target as Node
                if (node is StackPane && node.getParent() is Spinner<*>) {
                    val spinner = node.getParent() as Spinner<*>

                    var increment: Boolean? = null

                    if (node.styleClass.contains("increment-arrow-button")) {
                        increment = java.lang.Boolean.TRUE
                    } else if (node.styleClass.contains("decrement-arrow-button")) {
                        increment = java.lang.Boolean.FALSE
                    }

                    if (increment != null) {
                        event.consume()
                        this.spinner = spinner
                        this.isIncrementing = increment
                        startTimestamp = System.nanoTime()
                        timer.handle(startTimestamp + delay)
                        timer.start()
                    }
                }
            } else if (event.eventType == MouseEvent.MOUSE_RELEASED) {
                timer.stop()
                spinner = null
            }
        }
    }

}


