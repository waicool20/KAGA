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
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.waicool20.kaga.config.KagaConfig
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.handlers.GlobalShortcutHandler
import com.waicool20.kaga.handlers.KeyboardIncrementHandler
import com.waicool20.kaga.handlers.MouseIncrementHandler
import com.waicool20.kaga.handlers.ToolTipHandler
import com.waicool20.kaga.kcauto.KancolleAutoKai
import com.waicool20.kaga.kcauto.YuuBot
import com.waicool20.kaga.util.AlertFactory
import com.waicool20.kaga.util.LineListenerOutputStream
import com.waicool20.kaga.util.TeeOutputStream
import com.waicool20.kaga.views.ConsoleView
import com.waicool20.kaga.views.PathChooserView
import com.waicool20.kaga.views.StatsView
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_RELEASED
import javafx.scene.layout.FlowPane
import javafx.stage.Modality
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import tornadofx.*
import java.awt.Desktop
import java.io.PrintStream
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.concurrent.thread
import kotlin.math.abs

class KagaApp : Application() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun start(stage: Stage) {
        if (parameters.unnamed.contains("--use-local-server")) {
            logger.info("Using local server for API endpoint!")
            YuuBot.useLocalServer = true
        }
        if (parameters.unnamed.contains("--log-keys")) {
            logger.info("Logging key presses to console")
            GlobalShortcutHandler.logKeys = true
        }
        logger.info("Starting KAGA")
        FX.registerApplication(application = this, primaryStage = stage)
        Kaga.ROOT_STAGE = stage
        stage.setOnHidden { Kaga.exit() }

        if (Kaga.CONFIG.isValid()) {
            parameters.named["log"]?.let {
                val level = Level.toLevel(it)
                logger.info("Logging level was passed as argument, setting logging level to ${level.levelStr}")
                Kaga.setLogLevel(level)
            } ?: run {
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
    data class VersionInfo(val version: String = "Unknown", val kcAutoCompatibility: String = "Unknown") : Comparable<VersionInfo> {
        override fun equals(other: Any?) = other != null && other is VersionInfo && compareTo(other) == 0
        override fun hashCode() = super.hashCode()

        override fun compareTo(other: VersionInfo): Int {
            var tokens1 = version.split("\\D".toRegex()).mapNotNull { it.toIntOrNull() }
            var tokens2 = other.version.split("\\D".toRegex()).mapNotNull { it.toIntOrNull() }
            val diff = abs(tokens1.size - tokens2.size)
            if (tokens1.size > tokens2.size) {
                tokens2 += List(diff) { 0 }
            } else {
                tokens1 += List(diff) { 0 }
            }
            tokens1.zip(tokens2).forEach { (first, second) ->
                when {
                    first > second -> return 1
                    first < second -> return -1
                }
            }
            return 0
        }
    }

    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()
    val JAR_DIR: Path = Paths.get(Kaga::class.java.protectionDomain.codeSource.location.toURI()).parent
    val CONFIG_DIR: Path = JAR_DIR.resolve("kaga")

    lateinit var ROOT_STAGE: Stage

    val VERSION_INFO = mapper.readValue<VersionInfo>(javaClass.classLoader.getResourceAsStream("version.txt"))

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
    val KCAUTO_KAI by lazy { KancolleAutoKai() }

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
        checkForUpdates()
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
        KCAUTO_KAI.stop()
        Platform.exit()
        System.exit(0)
    }

    fun checkForUpdates(showNoUpdatesDialog: Boolean = false) {
        logger.info("KAGA - ${VERSION_INFO.version}")
        logger.info("KCAuto-Kai Compatibility: v${VERSION_INFO.kcAutoCompatibility}")
        if (!CONFIG.checkForUpdates) {
            logger.info("Update checking disabled, skipping")
            return
        }
        logger.info("Checking for updates...")
        thread {
            try {
                val json = mapper.readTree(URL("https://api.github.com/repos/waicool20/Kaga/releases/latest"))
                val latestVersion = VersionInfo(json.at("/tag_name").asText())
                if (latestVersion > VERSION_INFO) {
                    Platform.runLater {
                        Alert(Alert.AlertType.INFORMATION, "KAGA - Update").apply {
                            headerText = null
                            val pane = FlowPane()
                            val label = Label("""
                                A new update for KAGA is available: ${latestVersion.version}
                                Current version: ${VERSION_INFO.version}

                                Get the update over here:
                                """.trimIndent())
                            val link = json.at("/html_url").asText()
                            val hyperlink = Hyperlink(link).apply {
                                setOnAction {
                                    if (Desktop.isDesktopSupported()) {
                                        thread { Desktop.getDesktop().browse(URI(link)) }
                                    }
                                }
                            }
                            pane.children.addAll(label, hyperlink)
                            dialogPane.contentProperty().set(pane)
                            showAndWait()
                        }
                    }
                } else {
                    if (showNoUpdatesDialog) {
                        Platform.runLater {
                            AlertFactory.info(
                                    content = """
                                    No updates so far...

                                    Current version: ${VERSION_INFO.version}
                                    """.trimIndent()
                            ).showAndWait()
                        }
                    }
                    logger.info("No updates so far....")
                }
            } catch (e: Exception) {
                logger.warn("Could not check for updates, maybe your internet is down?")
                logger.error("Update check failed, reason: $e")
            }
        }
    }

    private fun startKCAutoListener() = LineListenerOutputStream().let {
        System.setOut(PrintStream(TeeOutputStream(System.out, it)))
        System.setErr(PrintStream(TeeOutputStream(System.err, it)))
    }
}
