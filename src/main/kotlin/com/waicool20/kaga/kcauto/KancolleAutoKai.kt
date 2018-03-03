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

package com.waicool20.kaga.kcauto

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.LockPreventer
import com.waicool20.kaga.util.StreamGobbler
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class KancolleAutoKai {
    private val template by lazy { Kaga::class.java.classLoader.getResourceAsStream("crashlog_template.md").bufferedReader().readText() }
    private val logger = LoggerFactory.getLogger(javaClass)
    private var kancolleAutoProcess: Process? = null
    private var streamGobbler: StreamGobbler? = null
    private var shouldStop = false

    val statsTracker = KancolleAutoKaiStatsTracker

    val version by lazy {
        Files.readAllLines(Kaga.CONFIG.kcaKaiRootDirPath.resolve("CHANGELOG.md")).first().let {
            it.dropWhile { !it.isDigit() }
        }
    }

    fun startAndWait(saveConfig: Boolean = true) {
        if (saveConfig) Kaga.PROFILE.save()
        val args = listOf(
                "java", "-jar",
                "${Kaga.CONFIG.sikulixJarPath}", "-r",
                "${Kaga.CONFIG.kcaKaiRootDirPath.resolve("kcauto-kai.sikuli")}",
                "--", "cfg", "${Kaga.PROFILE.path()}"
        )
        val lockPreventer = if (Kaga.CONFIG.preventLock) LockPreventer() else null
        statsTracker.startNewSession()
        shouldStop = false
        KCAutoLoop@ while (true) {
            if (Kaga.CONFIG.clearConsoleOnStart) println("\u001b[2J\u001b[H") // Clear console
            logger.info("Starting new KCAuto-Kai session (Version: $version)")
            logger.debug("Launching with command: ${args.joinToString(" ")}")
            logger.debug("Session profile: ${jacksonObjectMapper().writeValueAsString(Kaga.PROFILE)}")
            kancolleAutoProcess = ProcessBuilder(args).start()
            YuuBot.reportStats()
            streamGobbler = StreamGobbler(kancolleAutoProcess).apply { run() }
            lockPreventer?.start()
            val exitVal = kancolleAutoProcess?.waitFor()
            logger.info("KCAuto-Kai session has terminated!")
            logger.debug("Exit Value was $exitVal")
            lockPreventer?.stop()
            YuuBot.reportStats()
            when (exitVal) {
                0, 143 -> break@KCAutoLoop
                else -> {
                    if (shouldStop) {
                        shouldStop = false
                        logger.info("User initiated termination, exiting kancolle-auto process loop regardless...")
                        break@KCAutoLoop
                    }
                    logger.info("KCAuto-Kai didn't terminate gracefully")
                    YuuBot.reportStats()
                    saveCrashLog()
                    if (Kaga.CONFIG.autoRestartOnKCAutoCrash) {
                        if (statsTracker.crashes < Kaga.CONFIG.autoRestartMaxRetries) {
                            logger.info("Auto Restart enabled...attempting restart")
                            statsTracker.trackNewChild()
                        } else {
                            logger.info("Auto restart retry limit reached, terminating current session.")
                            break@KCAutoLoop
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        logger.info("Terminating current KCAuto-Kai session")
        kancolleAutoProcess?.destroy()
        shouldStop = true
    }

    fun stopAtPort() {
        logger.info("Will wait for any ongoing battle to finish first before terminating current KCAuto-Kai session!")
        thread {
            while (!statsTracker.atPort) {
                TimeUnit.MILLISECONDS.sleep(10)
            }
            stop()
        }
    }

    fun isRunning() = kancolleAutoProcess != null && kancolleAutoProcess?.isAlive ?: false

    private fun saveCrashLog() {
        val crashTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss"))
        val logFile = Kaga.CONFIG.kcaKaiRootDirPath.resolve("crashes/$crashTime.log")
        if (Files.notExists(logFile)) Files.createDirectories(logFile.parent)
        val log = template.replace("<DateTime>", crashTime)
                .replace("<Version>", version)
                .replace("<Viewer>", Kaga.PROFILE.general.program)
                .replace("<OS>", "${System.getProperty("os.name")} ${System.getProperty("os.version")} ${System.getProperty("os.arch")}")
                .replace("<Config>", Kaga.PROFILE.asIniString())
                .replace("<Log>", Kaga.LOG)
        Files.write(logFile, log.toByteArray(), StandardOpenOption.CREATE)
        YuuBot.reportCrash(CrashInfoDto(Kaga.LOG))
    }
}
