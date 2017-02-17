package com.waicool20.kaga

import com.fasterxml.jackson.databind.ObjectMapper
import com.waicool20.kaga.util.LockPreventer
import com.waicool20.kaga.util.StreamGobbler
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class KancolleAuto {
    val template = Kaga::class.java.classLoader.getResourceAsStream("crashlog_template.md").bufferedReader().readText()
    private val logger = LoggerFactory.getLogger(javaClass)
    private var kancolleAutoProcess: Process? = null
    private var streamGobbler: StreamGobbler? = null

    fun startAndWait() {
        Kaga.PROFILE!!.save(Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "config.ini"))
        val args = listOf(
                "java",
                "-jar",
                Kaga.CONFIG.sikulixJarPath.toString(),
                "-r",
                Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "kancolle_auto.sikuli").toString()
        )
        val lockPreventer: LockPreventer? =
                if (Kaga.CONFIG.preventLock) LockPreventer() else null
        if (Kaga.CONFIG.clearConsoleOnStart) println("\u001b[2J\u001b[H") // Clear console
        logger.info("Starting new Kancolle Auto session")
        logger.debug("Launching with command: ${args.joinToString(" ")}")
        logger.debug("Session profile: ${ObjectMapper().writeValueAsString(Kaga.PROFILE)}")
        kancolleAutoProcess = ProcessBuilder(args).start()
        streamGobbler = StreamGobbler(kancolleAutoProcess)
        streamGobbler?.run()
        lockPreventer?.start()
        val exitVal = kancolleAutoProcess?.waitFor()
        logger.info("Kancolle Auto session has terminated!")
        logger.debug("Exit Value was $exitVal")
        lockPreventer?.stop()
        if (!(exitVal == 0 || exitVal == 143)) {
            handleCrash()
        }
    }

    fun stop() {
        logger.info("Terminating current Kancolle Auto session")
        kancolleAutoProcess?.destroy()
    }

    fun isRunning() = kancolleAutoProcess != null && kancolleAutoProcess!!.isAlive

    private fun handleCrash() {
        logger.info("Kancolle Auto didn't terminate gracefully")
        saveCrashLog()
        if (Kaga.CONFIG.autoRestartOnKCAutoCrash) {
            logger.info("Auto Restart enabled...attempting restart")
            startAndWait()
        }
    }

    private fun saveCrashLog() {
        val crashTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss"))
        val logFile = Kaga.CONFIG.kancolleAutoRootDirPath.resolve("crashes/$crashTime.log")
        if (Files.notExists(logFile)) {
            Files.createDirectories(logFile.parent)
        }
        val versionLine = Files.readAllLines(Kaga.CONFIG.kancolleAutoRootDirPath.resolve("CHANGELOG.md"))[0]
        var kancolleAutoVersion = versionLine.replace("#{4} (\\d{4}-\\d{2}-\\d{2}).*?".toRegex(), { it.groupValues[1] })
        if (versionLine.contains("\\[.+?\\]".toRegex())) {
            kancolleAutoVersion += versionLine.replace(".*?(\\[.+?\\]).*?".toRegex(), { it.groupValues[1] })
        }
        val log = template.replace("<DateTime>", crashTime)
                .replace("<Version>", kancolleAutoVersion)
                .replace("<Viewer>", Kaga.PROFILE!!.general.program)
                .replace("<OS>", "${System.getProperty("os.name")} ${System.getProperty("os.version")} ${System.getProperty("os.arch")}")
                .replace("<Config>", Kaga.PROFILE!!.asIniString())
                .replace("<Log>", Kaga.LOG)
        Files.write(logFile, log.toByteArray(), StandardOpenOption.CREATE)
    }
}
