package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.bind
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.GridPane
import tornadofx.bind

class PvpTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var fleetCompComboBox: ComboBox<Int>

    @FXML private lateinit var content: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        fleetCompComboBox.items.setAll((1..5).toList())
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.pvp) {
            enableButton.bind(enabledProperty)
            fleetCompComboBox.bind(fleetCompProperty)
        }
        content.visibleProperty().bind(enableButton.selectedProperty())
    }
}
