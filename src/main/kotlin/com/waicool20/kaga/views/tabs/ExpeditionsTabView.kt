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
import com.waicool20.waicoolutils.controlsfx.bind
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox
import tornadofx.*


class ExpeditionsTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var fleet2CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var fleet3CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var fleet4CheckComboBox: CheckComboBox<String>

    @FXML private lateinit var content: VBox

    private val supportExpeditions = mapOf(
            "S1" to "Pre-Boss Node Support",
            "S2" to "Boss Node Support"
    )

    private val expeditions =
            (1..8).map(Int::toString) +
                    listOf("A1", "A2", "A3", "A4") +
                    (9..16).map(Int::toString) +
                    listOf("B1", "B2") +
                    (17..42).map(Int::toString) +
                    supportExpeditions.keys

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        expeditions.also {
            fleet2CheckComboBox.items.setAll(it)
            fleet3CheckComboBox.items.setAll(it)
            fleet4CheckComboBox.items.setAll(it)
        }
        val converter = object : StringConverter<String>() {
            override fun toString(expedition: String) = supportExpeditions[expedition] ?: expedition
            override fun fromString(string: String?): String = ""
        }
        fleet2CheckComboBox.converter = converter
        fleet3CheckComboBox.converter = converter
        fleet4CheckComboBox.converter = converter
    }

    private fun createBindings() {
        with(Kaga.PROFILE.expeditions) {
            enableButton.bind(enabledProperty)
            fleet2CheckComboBox.bind(fleet2Property)
            fleet3CheckComboBox.bind(fleet3Property)
            fleet4CheckComboBox.bind(fleet4Property)
        }
        content.disableProperty().bind(enableButton.selectedProperty().not())
    }
}
