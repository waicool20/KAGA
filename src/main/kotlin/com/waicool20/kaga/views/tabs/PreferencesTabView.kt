package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KagaConfig
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.bind
import javafx.fxml.FXML
import javafx.scene.control.*
import tornadofx.bind

class PreferencesTabView {
    @FXML private lateinit var preventLockCheckBox: CheckBox
    @FXML private lateinit var clearConsoleCheckbox: CheckBox
    @FXML private lateinit var restartSessionCheckBox: CheckBox
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
            preventLockCheckBox.selectedProperty().set(preventLock)
        }
    }

    fun createBindings() {
        with(Kaga.CONFIG) {
            preventLockCheckBox.selectedProperty().bindBidirectional(preventLockProperty)
            clearConsoleCheckbox.selectedProperty().bindBidirectional(clearConsoleOnStartProperty)
            restartSessionCheckBox.selectedProperty().bindBidirectional(autoRestartOnKCAutoCrashProperty)
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
