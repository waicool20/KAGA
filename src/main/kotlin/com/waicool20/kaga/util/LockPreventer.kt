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
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class LockPreventer {
    private val scheduler by lazy { ScheduledThreadPoolExecutor(1) }
    private val robot by lazy { Robot() }

    private var isRunning = false
    private var task: ScheduledFuture<*>? = null

    fun start() {
        if (!isRunning) {
            isRunning = true
            task = scheduler.scheduleAtFixedRate({
                robot.keyPress(KeyEvent.VK_SHIFT)
                TimeUnit.MILLISECONDS.sleep(10)
                robot.keyRelease(KeyEvent.VK_SHIFT)
            },0, 1, TimeUnit.MINUTES)
        }
    }

    fun stop() {
        if (isRunning) {
            isRunning = false
            task?.cancel(false)
            scheduler.purge()
        }
    }
}
