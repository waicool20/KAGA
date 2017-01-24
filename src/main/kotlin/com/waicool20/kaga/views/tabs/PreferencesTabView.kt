package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.bind
import javafx.fxml.FXML
import javafx.scene.control.*
import tornadofx.bind

class PreferencesTabView {
    @FXML private lateinit var preventLockCheckBox: CheckBox
    @FXML private lateinit var sikulixJarPathLabel: Label
    @FXML private lateinit var kancolleAutoRootPathLabel: Label

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    fun setValues() {
        sikulixJarPathLabel.text = Kaga.CONFIG.sikulixJarPath.toString()
        kancolleAutoRootPathLabel.text = Kaga.CONFIG.kancolleAutoRootDirPath.toString()
        preventLockCheckBox.selectedProperty().set(Kaga.CONFIG.preventLock)
    }

    fun createBindings() {
        preventLockCheckBox.selectedProperty().addListener { obsVal, oldVal, newVal ->
            Kaga.CONFIG.preventLock = newVal
        }
    }
}
