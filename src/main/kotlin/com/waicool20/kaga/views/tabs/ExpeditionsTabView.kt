package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.GridPane
import javafx.util.StringConverter
import tornadofx.bind


class ExpeditionsTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var fleet2ComboBox: ComboBox<String>
    @FXML private lateinit var fleet3ComboBox: ComboBox<String>
    @FXML private lateinit var fleet4ComboBox: ComboBox<String>

    @FXML private lateinit var content: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        val special = mapOf(
                "" to "<Off-Duty>",
                "9998" to "Pre-Boss Node Support",
                "9999" to "Boss Node Support"
        )
        val expeditionOptions = special.keys.toMutableList()
        with(expeditionOptions) {
            addAll(1, (1..41).map(Int::toString))
            fleet2ComboBox.items.setAll(this)
            fleet3ComboBox.items.setAll(this)
            fleet4ComboBox.items.setAll(this)
        }
        val converter = object : StringConverter<String>() {
            override fun toString(string: String?): String {
                return special.getOrElse(string ?: "", { string ?: "" })
            }

            override fun fromString(string: String?): String = ""
        }
        fleet2ComboBox.converter = converter
        fleet3ComboBox.converter = converter
        fleet4ComboBox.converter = converter
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.expeditions) {
            enableButton.bind(enabledProperty)
            fleet2ComboBox.bind(fleet2Property)
            fleet3ComboBox.bind(fleet3Property)
            fleet4ComboBox.bind(fleet4Property)
        }
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }
}
