package com.waicool20.kaga

import com.waicool20.kaga.config.KagaConfig
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.handlers.KeyboardIncrementHandler
import com.waicool20.kaga.views.ConsoleView
import com.waicool20.kaga.views.PathChooserView
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.input.MouseEvent.MOUSE_RELEASED
import javafx.stage.Modality
import javafx.stage.Stage
import tornadofx.FX
import tornadofx.find
import java.nio.file.Path
import java.nio.file.Paths

class Kaga : Application() {

    private object Holder {
        val INSTANCE = Kaga()
    }

    companion object {
        val INSTANCE: Kaga by lazy { Holder.INSTANCE }

        @JvmStatic val JAR_DIR: Path = Paths.get(Kaga::class.java.protectionDomain.codeSource.location.toURI()).parent
        @JvmStatic val CONFIG_DIR: Path = Paths.get(JAR_DIR.toString(), "kaga")
        @JvmStatic val CONFIG_FILE: Path = Paths.get(CONFIG_DIR.toString(), "kaga.ini")

        @JvmStatic lateinit var ROOT_STAGE: Stage
        @JvmStatic lateinit var CONSOLE_STAGE: Stage

        @JvmStatic val CONFIG: KagaConfig = KagaConfig.load(CONFIG_FILE)
        @JvmStatic var PROFILE: KancolleAutoProfile? = null
    }

    override fun start(stage: Stage) {
        FX.registerApplication(application = this, primaryStage = stage)
        ROOT_STAGE = stage
        if (CONFIG.isValid()) {
            startMainApplication()
        } else {
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
                minHeight = height + 25
                minWidth = width + 25
            }
            with(scene) {
                addEventFilter(KEY_PRESSED, KeyboardIncrementHandler())
                val handler = com.waicool20.kaga.handlers.MouseIncrementHandler(1000L, 40)
                addEventFilter(MOUSE_PRESSED, handler)
                addEventFilter(MOUSE_RELEASED, handler)
            }
            startConsole()
        }
    }

    fun startPathChooser() {
        val stage = Stage()
        with(stage) {
            title = "Configure KAGA paths..."
            isResizable = false
            scene = Scene(find(PathChooserView::class).root)
            show()
        }
    }

    fun startConsole() {
        CONSOLE_STAGE = Stage()
        with(CONSOLE_STAGE) {
            initModality(Modality.WINDOW_MODAL)
            title = "KAGA Debug"
            minHeight = 300.0
            maxHeight = 600.0
            scene = Scene(find(ConsoleView::class).root)
        }
    }
}
