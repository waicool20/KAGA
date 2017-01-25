package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KagaConfig
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Path

class PreferencesTabView {
    @FXML private lateinit var preventLockCheckBox: CheckBox
    @FXML private lateinit var clearConsoleCheckbox: CheckBox
    @FXML private lateinit var restartSessionCheckBox: CheckBox
    @FXML private lateinit var debugModeEnableCheckBox: CheckBox
    @FXML private lateinit var sikulixJarPathLink: Hyperlink
    @FXML private lateinit var kancolleAutoRootPathLink: Hyperlink

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    fun setValues() {
        with(Kaga.CONFIG) {
            sikulixJarPathLink.setOnAction { openFile(sikulixJarPath.parent)}
            kancolleAutoRootPathLink.setOnAction { openFile(kancolleAutoRootDirPath) }
            sikulixJarPathLink.text = sikulixJarPath.toString()
            kancolleAutoRootPathLink.text = kancolleAutoRootDirPath.toString()
        }
    }

    private fun openFile(path: Path) {
        if (Desktop.isDesktopSupported()) {
            Thread({
                Desktop.getDesktop().open(path.toFile())
            }).start()
            Kaga.ROOT_STAGE.toBack()
        }
    }

    fun createBindings() {
        with(Kaga.CONFIG) {
            preventLockCheckBox.selectedProperty().bindBidirectional(preventLockProperty)
            clearConsoleCheckbox.selectedProperty().bindBidirectional(clearConsoleOnStartProperty)
            restartSessionCheckBox.selectedProperty().bindBidirectional(autoRestartOnKCAutoCrashProperty)
            debugModeEnableCheckBox.selectedProperty().bindBidirectional(debugModeEnabledProperty)
        }
    }

    @FXML private fun onSaveButton() {
        Kaga.CONFIG.save()
    }

    @FXML private fun onResetButton() {
        Kaga.CONFIG = KagaConfig.load()
        initialize()
    }
}