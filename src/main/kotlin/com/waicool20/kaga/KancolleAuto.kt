package com.waicool20.kaga

import com.fasterxml.jackson.databind.ObjectMapper
import com.waicool20.kaga.util.LockPreventer
import com.waicool20.kaga.util.StreamGobbler
import org.slf4j.LoggerFactory
import java.nio.file.Paths


class KancolleAuto {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var kancolleAutoProcess: Process? = null
    private var streamGobbler: StreamGobbler? = null

    fun startAndWait() {
        Kaga.PROFILE!!.save(Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "config.ini"))
        val args = listOf(
                "java",
                "-jar",
                Kaga.CONFIG.sikuliScriptJarPath.toString(),
                "-r",
                Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "kancolle_auto.sikuli").toString()
        )
        val lockPreventer: LockPreventer? =
                if (Kaga.CONFIG.preventLock) LockPreventer() else null
        println("\u001b[2J\u001b[H") // Clear console
        logger.info("Starting new Kancolle Auto session")
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
            // TODO add config option
            logger.info("Kancolle Auto didn't terminate gracefully")
            startAndWait()
        }
    }

    fun stop() {
        logger.info("Terminating current Kancolle Auto session")
        kancolleAutoProcess?.destroy()
    }

    fun isRunning() = kancolleAutoProcess != null && kancolleAutoProcess!!.isAlive
}
