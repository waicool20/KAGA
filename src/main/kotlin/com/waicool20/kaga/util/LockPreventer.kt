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

import java.awt.Robot
import java.awt.event.KeyEvent
import java.util.*

class LockPreventer {
    private var timer: Timer? = null
    private val robot = Robot()
    private val timerTask = object : TimerTask() {
        override fun run() {
            robot.keyPress(KeyEvent.VK_SHIFT)
            Thread.sleep(10)
            robot.keyRelease(KeyEvent.VK_SHIFT)
        }
    }

    fun start() {
        timer = Timer()
        timer?.schedule(timerTask, 0L, 60 * 1000L)
    }

    fun stop() = timer?.cancel()
}
