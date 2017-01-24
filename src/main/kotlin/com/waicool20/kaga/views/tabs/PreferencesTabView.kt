package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KagaConfig
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Label

class PreferencesTabView {
    @FXML private lateinit var preventLockCheckBox: CheckBox
    @FXML private lateinit var clearConsoleCheckbox: CheckBox
    @FXML private lateinit var restartSessionCheckBox: CheckBox
    @FXML private lateinit var debugModeEnableCheckBox: CheckBox
    @FXML private lateinit var sikulixJarPathLabel: Label
    @FXML private lateinit var kancolleAutoRootPathLabel: Label

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    fun setValues() {
        with(Kaga.CONFIG) {
            sikulixJarPathLabel.text = sikulixJarPath.toString()
            kancolleAutoRootPathLabel.text = kancolleAutoRootDirPath.toString()
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
