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
