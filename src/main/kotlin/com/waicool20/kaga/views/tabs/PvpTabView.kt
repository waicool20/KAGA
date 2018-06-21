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
import com.waicool20.waicoolutils.javafx.bind
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.VBox
import tornadofx.*

class PvpTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var presetComboBox: ComboBox<String>
    @FXML private lateinit var content: VBox

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        presetComboBox.items.setAll(listOf("") + (1..12).map(Int::toString))
    }

    private fun createBindings() {
        with(Kaga.PROFILE.pvp) {
            enableButton.bind(enabledProperty)
            presetComboBox.bind(fleetProperty)
        }
        content.disableProperty().bind(enableButton.selectedProperty().not())
    }
}
