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
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_RELEASED
import javafx.stage.Modality
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import tornadofx.FX
import tornadofx.find
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths

class Kaga : Application() {

    val logger = LoggerFactory.getLogger(javaClass)

    private object Holder {
        val INSTANCE = Kaga()
    }

    companion object {
        val INSTANCE: Kaga by lazy { Holder.INSTANCE }

        val JAR_DIR: Path = Paths.get(Kaga::class.java.protectionDomain.codeSource.location.toURI()).parent
        val CONFIG_DIR: Path = Paths.get(JAR_DIR.toString(), "kaga")

        lateinit var ROOT_STAGE: Stage
        lateinit var CONSOLE_STAGE: Stage
        lateinit var STATS_STAGE: Stage

        lateinit var CONFIG: KagaConfig
        var PROFILE: KancolleAutoProfile? = null

        var LOG = ""
        val KANCOLLE_AUTO = KancolleAuto()

        fun setLogLevel(level: Level) {
            (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = level
        }
    }

    override fun start(stage: Stage) {
        val logLevel = parameters.named.getOrElse("log", { "" })
        if (logLevel != "") {
            val level = Level.toLevel(logLevel)
            logger.info("Logging level was passed as argument, setting logging level to ${level.levelStr}")
            setLogLevel(level)
        }
        logger.info("Starting KAGA")
        FX.registerApplication(application = this, primaryStage = stage)
        ROOT_STAGE = stage
        stage.setOnHidden { Platform.exit() }
        CONFIG = KagaConfig.load()
        if (CONFIG.isValid()) {
            if (logLevel == "") {
                logger.info("No logging level was found in the arguments...using the config level of ${CONFIG.logLevel()}")
                setLogLevel(Level.toLevel(CONFIG.logLevel()))
            }
            logger.info("KAGA config is valid, starting main application")
            startMainApplication()
        } else {
            logger.info("KAGA config isn't valid, starting path chooser")
            startPathChooser()
        }
    }

    fun startMainApplication() {
        Kaga.PROFILE = KancolleAutoProfile
                .load(Paths.get(Kaga.CONFIG_DIR.toString(), "${Kaga.CONFIG.currentProfile}-config.ini")) ?:
                KancolleAutoProfile.load()
        if (Kaga.PROFILE != null) {
            Kaga.CONFIG.currentProfile = Kaga.PROFILE!!.name
            Kaga.CONFIG.save()
            val root: Parent = FXMLLoader.load(Kaga::class.java.classLoader.getResource("views/kaga.fxml"))
            val scene = Scene(root)
            with(ROOT_STAGE) {
                this.scene = scene
                title = "KAGA - Kancolle Auto GUI App"
                show()
                isResizable = false
                addEventFilter(KeyEvent.ANY, ToolTipHandler(this))
                addEventFilter(KEY_PRESSED, KeyboardIncrementHandler())
                val handler = MouseIncrementHandler(1000L, 40)
                addEventFilter(MOUSE_PRESSED, handler)
                addEventFilter(MOUSE_RELEASED, handler)
            }
            startConsole()
            startKCAutoListener()
            startStats()
        }
    }

    fun startPathChooser() {
        val stage = Stage()
        with(stage) {
            title = "KAGA - Configure KAGA paths..."
            isResizable = false
            scene = Scene(find(PathChooserView::class).root)
            show()
        }
    }

    fun startConsole() {
        CONSOLE_STAGE = Stage()
        with(CONSOLE_STAGE) {
            initOwner(ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            title = "KAGA - Debug"
            minHeight = 300.0
            minWidth = 600.0
            scene = Scene(find(ConsoleView::class).root)
            if (CONFIG.showDebugOnStart) show()
        }
    }

    fun startStats() {
        STATS_STAGE = Stage()
        with(STATS_STAGE) {
            initOwner(ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            title = "KAGA - Session Stats"
            scene = Scene(find(StatsView::class).root)
            isResizable = false
        }
    }

    private fun startKCAutoListener() {
        val lineListener = LineListenerOutputStream()
        System.setOut(PrintStream(TeeOutputStream(System.out, lineListener)))
        System.setErr(PrintStream(TeeOutputStream(System.err, lineListener)))
    }
}
