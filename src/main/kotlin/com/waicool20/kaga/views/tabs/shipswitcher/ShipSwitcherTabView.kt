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
import com.waicool20.kaga.config.KancolleAutoProfile.ShipSwitcher
import com.waicool20.kaga.config.KancolleAutoProfile.SwitchCriteria
import com.waicool20.kaga.util.bind
import javafx.beans.property.SimpleListProperty
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox
import tornadofx.*
import kotlin.concurrent.thread
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

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

    @FXML private lateinit var slot1ShipsVBox: VBox
    @FXML private lateinit var slot2ShipsVBox: VBox
    @FXML private lateinit var slot3ShipsVBox: VBox
    @FXML private lateinit var slot4ShipsVBox: VBox
    @FXML private lateinit var slot5ShipsVBox: VBox
    @FXML private lateinit var slot6ShipsVBox: VBox

    @FXML private lateinit var content: GridPane
    private var currentProperty: Pair<Button, SimpleListProperty<String>>? = null
    data class SlotShipsEditScope(val slot: SimpleListProperty<String>) : Scope()

    private val buttonPropMap by lazy {
        mapOf(
                slot1ShipsButton to ShipSwitcher::slot1ShipsProperty,
                slot2ShipsButton to ShipSwitcher::slot2ShipsProperty,
                slot3ShipsButton to ShipSwitcher::slot3ShipsProperty,
                slot4ShipsButton to ShipSwitcher::slot4ShipsProperty,
                slot5ShipsButton to ShipSwitcher::slot5ShipsProperty,
                slot6ShipsButton to ShipSwitcher::slot6ShipsProperty
        )
    }

    @FXML
    fun initialize() {
        setValues()
        createBindings()
        setupButtons()
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
    }

    private fun createBindings() {
        content.disableProperty().bind(enableButton.selectedProperty().not())
        with(Kaga.PROFILE.shipSwitcher) {
            enableButton.bind(enabledProperty)
            slot1CriteriaComboBox.bind(slot1CriteriaProperty)
            slot2CriteriaComboBox.bind(slot2CriteriaProperty)
            slot3CriteriaComboBox.bind(slot3CriteriaProperty)
            slot4CriteriaComboBox.bind(slot4CriteriaProperty)
            slot5CriteriaComboBox.bind(slot5CriteriaProperty)
            slot6CriteriaComboBox.bind(slot6CriteriaProperty)
        }
        slot1ShipsVBox.disableProperty().bind(slot1CriteriaComboBox.checkModel.checkedIndices.sizeProperty.isEqualTo(0))
        slot2ShipsVBox.disableProperty().bind(slot2CriteriaComboBox.checkModel.checkedIndices.sizeProperty.isEqualTo(0))
        slot3ShipsVBox.disableProperty().bind(slot3CriteriaComboBox.checkModel.checkedIndices.sizeProperty.isEqualTo(0))
        slot4ShipsVBox.disableProperty().bind(slot4CriteriaComboBox.checkModel.checkedIndices.sizeProperty.isEqualTo(0))
        slot5ShipsVBox.disableProperty().bind(slot5CriteriaComboBox.checkModel.checkedIndices.sizeProperty.isEqualTo(0))
        slot6ShipsVBox.disableProperty().bind(slot6CriteriaComboBox.checkModel.checkedIndices.sizeProperty.isEqualTo(0))
    }

    private fun setupButtons() {
        val switcher = Kaga.PROFILE.shipSwitcher
        buttonPropMap.forEach { button, slotProp ->
            val slot = slotProp.get(Kaga.PROFILE.shipSwitcher)
            button.tooltip {
                textProperty().bind(slot.stringBinding { it?.joinToString("\n") })
            }
            button.setOnAction { configureSlotShips(slot) }
            button.setOnMouseClicked { e ->
                if (!e.isControlDown) return@setOnMouseClicked
                when (e.button) {
                    MouseButton.PRIMARY -> {
                        if (button.textFill == Color.GREEN) {
                            button.textFill = Color.BLACK
                            currentProperty = null
                        } else {
                            buttonPropMap.keys.filterNot { it == button }.forEach { it.textFill = Color.BLACK }
                            button.textFill = Color.GREEN
                            currentProperty = button to slot
                        }
                    }
                    MouseButton.SECONDARY -> {
                        if (button != currentProperty?.first) {
                            currentProperty?.second?.also { sourceProp ->
                                buttonPropMap[button]?.get(switcher)?.setAll(sourceProp)
                                thread {
                                    button.style = "-fx-background-color: red"
                                    Thread.sleep(200)
                                    button.style = ""
                                }
                            }
                        }
                    }
                    else -> Unit // Ignore
                }
            }
        }
    }

    private fun configureSlotShips(slot: SimpleListProperty<String>) =
            find<SlotShipsEditorWorkspace>(SlotShipsEditScope(slot)).openModal(resizable = false)
}


