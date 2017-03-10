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

package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.GridPane
import javafx.util.StringConverter
import tornadofx.*


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
        with(special.keys.toMutableList()) {
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
        with(Kaga.PROFILE!!.expeditions) {
            fleet2ComboBox.value = fleet2
            fleet3ComboBox.value = fleet3
            fleet4ComboBox.value = fleet4
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.expeditions) {
            enableButton.bind(enabledProperty)
            fleet2ComboBox.valueProperty().addListener { obs, oldVal, newVal -> if (newVal != null) fleet2 = newVal }
            fleet3ComboBox.valueProperty().addListener { obs, oldVal, newVal -> if (newVal != null) fleet3 = newVal }
            fleet4ComboBox.valueProperty().addListener { obs, oldVal, newVal -> if (newVal != null) fleet4 = newVal }
        }
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }
}
