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

import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import org.slf4j.LoggerFactory
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

object GlobalShortcutHandler : NativeKeyListener {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val modifiersPressed = mutableMapOf(
            NativeKeyEvent.CTRL_MASK to false,
            NativeKeyEvent.SHIFT_MASK to false,
            NativeKeyEvent.ALT_MASK to false,
            NativeKeyEvent.META_MASK to false
    )

    private val modifiers = mapOf(
            "CTRL" to NativeKeyEvent.CTRL_MASK,
            "SHIFT" to NativeKeyEvent.SHIFT_MASK,
            "ALT" to NativeKeyEvent.ALT_MASK,
            "META" to NativeKeyEvent.META_MASK
    )
    private val shortcuts = mutableMapOf<String, Pair<List<String>, () -> Unit>>()
    var logKeys = false

    init {
        try {
            Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
                level = Level.WARNING
            }
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(this)
        } catch (e: Exception) {
            logger.info("There was a problem registering global shortcuts, they will not work!")
        }
    }

    fun registerShortcut(name: String = UUID.randomUUID().toString(), shortcut: String, action: () -> Unit) =
            shortcuts.put(name, shortcut.split("+") to action)

    fun deregisterShortcut(name: String) = shortcuts.remove(name)

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        modifiersPressed.forEach { key, _ ->
            modifiersPressed[key] = event.modifiers and key != 0
        }
        if (logKeys) {
            logger.debug("Rawcode: ${event.rawCode}, Keycode: ${event.keyCode}, Keytext: ${NativeKeyEvent.getKeyText(event.keyCode)}, Char: ${event.rawCode.toChar()}, Modifiers: ${event.modifiers}")
        }
        shortcuts.forEach { _, (keys, action) ->
            val pressed = keys.partition { modifiers.containsKey(it) }.let { (modifiers, key) ->
                modifiers.all { modifiersPressed[this.modifiers[it]] ?: false } &&
                        key.isNotEmpty() &&
                        key.all { NativeKeyEvent.getKeyText(event.keyCode).equals(it, true) }
            }
            if (pressed) action()
        }
    }

    override fun nativeKeyReleased(event: NativeKeyEvent) = Unit
    override fun nativeKeyTyped(event: NativeKeyEvent) = Unit
}
