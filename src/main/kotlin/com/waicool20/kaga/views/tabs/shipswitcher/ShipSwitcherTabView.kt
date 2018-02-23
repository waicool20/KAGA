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

package com.waicool20.kaga.views.tabs.shipswitcher

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile.SwitchCriteria
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox
import tornadofx.*

class ShipSwitcherTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var slot1CriteriaComboBox: CheckComboBox<SwitchCriteria>
    @FXML private lateinit var slot2CriteriaComboBox: CheckComboBox<SwitchCriteria>
    @FXML private lateinit var slot3CriteriaComboBox: CheckComboBox<SwitchCriteria>
    @FXML private lateinit var slot4CriteriaComboBox: CheckComboBox<SwitchCriteria>
    @FXML private lateinit var slot5CriteriaComboBox: CheckComboBox<SwitchCriteria>
    @FXML private lateinit var slot6CriteriaComboBox: CheckComboBox<SwitchCriteria>

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
        val switchCriteriaConverter = object : StringConverter<SwitchCriteria>() {
            override fun toString(criteria: SwitchCriteria) = criteria.prettyString
            override fun fromString(string: String): SwitchCriteria = SwitchCriteria.fromPrettyString(string)
        }
        slot1CriteriaComboBox.converter = switchCriteriaConverter
        slot2CriteriaComboBox.converter = switchCriteriaConverter
        slot3CriteriaComboBox.converter = switchCriteriaConverter
        slot4CriteriaComboBox.converter = switchCriteriaConverter
        slot5CriteriaComboBox.converter = switchCriteriaConverter
        slot6CriteriaComboBox.converter = switchCriteriaConverter
        SwitchCriteria.values().toList().also {
            slot1CriteriaComboBox.items.setAll(it)
            slot2CriteriaComboBox.items.setAll(it)
            slot3CriteriaComboBox.items.setAll(it)
            slot4CriteriaComboBox.items.setAll(it)
            slot5CriteriaComboBox.items.setAll(it)
            slot6CriteriaComboBox.items.setAll(it)
        }

        slot1ShipsButton.setOnAction { configureSlotShips(1) }
        slot2ShipsButton.setOnAction { configureSlotShips(2) }
        slot3ShipsButton.setOnAction { configureSlotShips(3) }
        slot4ShipsButton.setOnAction { configureSlotShips(4) }
        slot5ShipsButton.setOnAction { configureSlotShips(5) }
        slot6ShipsButton.setOnAction { configureSlotShips(6) }
    }

    private fun createBindings() {
        with(Kaga.PROFILE.shipSwitcher) {
            enableButton.bind(enabledProperty)
            slot1CriteriaProperty.bind<SwitchCriteria, SwitchCriteria>(slot1CriteriaComboBox.checkModel.checkedItems) { it }
            slot2CriteriaProperty.bind<SwitchCriteria, SwitchCriteria>(slot2CriteriaComboBox.checkModel.checkedItems) { it }
            slot3CriteriaProperty.bind<SwitchCriteria, SwitchCriteria>(slot3CriteriaComboBox.checkModel.checkedItems) { it }
            slot4CriteriaProperty.bind<SwitchCriteria, SwitchCriteria>(slot4CriteriaComboBox.checkModel.checkedItems) { it }
            slot5CriteriaProperty.bind<SwitchCriteria, SwitchCriteria>(slot5CriteriaComboBox.checkModel.checkedItems) { it }
            slot6CriteriaProperty.bind<SwitchCriteria, SwitchCriteria>(slot6CriteriaComboBox.checkModel.checkedItems) { it }
        }
    }

    private fun configureSlotShips(slot: Int) = find<SlotShipsConfigurationView>(params = mapOf("slot" to slot)).openModal()
}


