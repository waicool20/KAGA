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

import javafx.fxml.FXML
import javafx.scene.control.Button
import org.controlsfx.control.CheckComboBox

class ShipSwitcherTabView {
    @FXML private lateinit var enableButton: Button
    @FXML private lateinit var slot1CriteriaComboBox: CheckComboBox<String>
    @FXML private lateinit var slot2CriteriaComboBox: CheckComboBox<String>
    @FXML private lateinit var slot3CriteriaComboBox: CheckComboBox<String>
    @FXML private lateinit var slot4CriteriaComboBox: CheckComboBox<String>
    @FXML private lateinit var slot5CriteriaComboBox: CheckComboBox<String>
    @FXML private lateinit var slot6CriteriaComboBox: CheckComboBox<String>

    @FXML private lateinit var slot1ShipsButton: Button
    @FXML private lateinit var slot2ShipsButton: Button
    @FXML private lateinit var slot3ShipsButton: Button
    @FXML private lateinit var slot4ShipsButton: Button
    @FXML private lateinit var slot5ShipsButton: Button
    @FXML private lateinit var slot6ShipsButton: Button

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {

    }

    private fun createBindings() {

    }
}


