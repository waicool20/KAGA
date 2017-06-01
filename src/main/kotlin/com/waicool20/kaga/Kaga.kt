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

package com.waicool20.kaga

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.waicool20.kaga.config.KagaConfig
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.handlers.KeyboardIncrementHandler
import com.waicool20.kaga.handlers.MouseIncrementHandler
import com.waicool20.kaga.handlers.ToolTipHandler
import com.waicool20.kaga.kcauto.KancolleAuto
import com.waicool20.kaga.util.LineListenerOutputStream
import com.waicool20.kaga.util.TeeOutputStream
import com.waicool20.kaga.views.ConsoleView
import com.waicool20.kaga.views.PathChooserView
import com.waicool20.kaga.views.StatsView
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_RELEASED
import javafx.stage.Modality
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths

class KagaApp : Application() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun start(stage: Stage) {
        val logLevel = parameters.named.getOrElse("log", { "" })
        if (logLevel != "") {
            val level = Level.toLevel(logLevel)
            logger.info("Logging level was passed as argument, setting logging level to ${level.levelStr}")
            Kaga.setLogLevel(level)
        }
        logger.info("Starting KAGA")
        FX.registerApplication(application = this, primaryStage = stage)
        Kaga.ROOT_STAGE = stage
        stage.setOnHidden { Kaga.exit() }

        if (Kaga.CONFIG.isValid()) {
            if (logLevel.isEmpty()) {
                logger.info("No logging level was found in the arguments...using the config level of ${Kaga.CONFIG.logLevel()}")
                Kaga.setLogLevel(Level.toLevel(Kaga.CONFIG.logLevel()))
            }
            logger.info("KAGA config is valid, starting main application")
            Kaga.startMainApplication()
        } else {
            logger.info("KAGA config isn't valid, starting path chooser")
            Kaga.startPathChooser()
        }
    }
}

object Kaga {
    private val logger = LoggerFactory.getLogger(javaClass)
    val JAR_DIR: Path = Paths.get(Kaga::class.java.protectionDomain.codeSource.location.toURI()).parent
    val CONFIG_DIR: Path = JAR_DIR.resolve("kaga")

    lateinit var ROOT_STAGE: Stage

    val CONSOLE_STAGE by lazy {
        Stage().apply {
            initOwner(ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            title = "KAGA - Debug"
            minHeight = 300.0
            minWidth = 600.0
            scene = Scene(find(ConsoleView::class).root)
        }
    }

    val STATS_STAGE by lazy {
        Stage().apply {
            initOwner(ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            title = "KAGA - Session Stats"
            scene = Scene(find(StatsView::class).root)
            isResizable = false
        }
    }

    var CONFIG = KagaConfig.load()
    var PROFILE = try {
        KancolleAutoProfile.load(CONFIG_DIR.resolve("${CONFIG.currentProfile}-config.ini"))
    } catch (e: Exception) {
        logger.error("Failed to parse kancolle auto profile, reason: ${e.message}")
        logger.info("Using default profile!")
        KancolleAutoProfile()
    }

    var LOG = ""
    val KANCOLLE_AUTO by lazy { KancolleAuto() }

    fun startMainApplication() {
        CONFIG.currentProfile = PROFILE.name
        CONFIG.save()
        with(ROOT_STAGE) {
            scene = Scene(FXMLLoader.load(Kaga::class.java.classLoader.getResource("views/kaga.fxml")))
            title = "KAGA - Kancolle Auto GUI App"
            show()
            isResizable = false
            addEventFilter(KeyEvent.ANY, ToolTipHandler(this))
            addEventFilter(KEY_PRESSED, KeyboardIncrementHandler())
            val handler = MouseIncrementHandler(1000L, 40)
            addEventFilter(MOUSE_PRESSED, handler)
            addEventFilter(MOUSE_RELEASED, handler)
        }
        if (CONFIG.showDebugOnStart) CONSOLE_STAGE.show()
        startKCAutoListener()
        if (CONFIG.showStatsOnStart) STATS_STAGE.show()
    }

    fun startPathChooser() = with(Stage()) {
        title = "KAGA - Configure KAGA paths..."
        isResizable = false
        scene = Scene(find(PathChooserView::class).root)
        show()
    }

    fun setLogLevel(level: Level) {
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = level
    }

    fun exit() {
        KANCOLLE_AUTO.stop()
        Platform.exit()
        System.exit(0)
    }

    private fun startKCAutoListener() = LineListenerOutputStream().let {
        System.setOut(PrintStream(TeeOutputStream(System.out, it)))
        System.setErr(PrintStream(TeeOutputStream(System.err, it)))
    }
}
