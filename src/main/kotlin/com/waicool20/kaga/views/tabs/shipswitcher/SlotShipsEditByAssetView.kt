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

import com.waicool20.kaga.config.ShipSpecification.*
import com.waicool20.kaga.config.ShipSpecificationByAsset
import com.waicool20.kaga.config.ShipSpecificationByAsset.ShipClass
import com.waicool20.kaga.handlers.MouseIncrementHandler
import com.waicool20.waicoolutils.javafx.converters.EnumCapitalizedNameConverter
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import org.controlsfx.control.SegmentedButton
import tornadofx.*

class SlotShipsEditByAssetView : View() {
    override val root: VBox by fxml("/views/tabs/slotships/editor-ship-class.fxml")
    private val classComboBox: ComboBox<ShipClass> by fxid()
    private val assetTextField: TextField by fxid()
    private val levelFilterButton: SegmentedButton by fxid()
    private val levelSpinner: Spinner<Int> by fxid()
    private val lockComboBox: ComboBox<LockCriteria> by fxid()
    private val ringComboBox: ComboBox<RingCriteria> by fxid()
    private val spec: ShipSpecificationByAsset by param()
    private val editModel: SlotShipEditModel by inject()

    private val toggleMap = mapOf(
            LevelCriteria.NONE to ToggleButton("X"),
            LevelCriteria.GREATHER_THAN to ToggleButton(">"),
            LevelCriteria.LESS_THAN to ToggleButton("<")
    )

    init {
        disableRefresh()
        disableDelete()
        disableCreate()
        toggleMap.forEach { (criteria, toggle) ->
            toggle.userData = criteria
        }
        levelFilterButton.buttons.addAll(toggleMap.values)
        levelSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 165)
        levelSpinner.disableProperty().bind(
                levelFilterButton.toggleGroup.selectedToggleProperty().isEqualTo(toggleMap[LevelCriteria.NONE])
        )
        lockComboBox.items.addAll(LockCriteria.values())
        ringComboBox.items.addAll(RingCriteria.values())
        lockComboBox.converter = EnumCapitalizedNameConverter()
        ringComboBox.converter = EnumCapitalizedNameConverter()
    }

    init {
        classComboBox.items.addAll(ShipClass.values())

        ShipClass.values().find { it.name == spec.asset }?.let {
            classComboBox.selectionModel.select(it)
        } ?: run { assetTextField.text = spec.asset }

        spec.level?.let { levelSpinner.valueFactory.value = it }
        toggleMap[spec.levelCriteria]?.isSelected = true
        lockComboBox.selectionModel.select(spec.lockCriteria)
        ringComboBox.selectionModel.select(spec.ringCriteria)

        classComboBox.disableWhen { assetTextField.textProperty().isNotBlank() }
    }

    override fun onDock() {
        super.onDock()
        val handler = MouseIncrementHandler(1000L, 40)
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, handler)
        root.addEventFilter(MouseEvent.MOUSE_RELEASED, handler)
    }

    override fun onSave() {
        super.onSave()
        val levelCriteria = levelFilterButton.toggleGroup.selectedToggle.userData as LevelCriteria
        val s = ShipSpecificationByAsset(
                assetTextField.text.takeIf { it.isNotBlank() }
                        ?: classComboBox.selectedItem?.toString() ?: return,
                levelCriteria,
                if (levelCriteria == LevelCriteria.NONE) null else levelSpinner.value,
                lockComboBox.selectedItem!!,
                ringComboBox.selectedItem!!)
        if (editModel.index != -1) {
            editModel.items.value[editModel.index] = s
        } else {
            editModel.items.value.add(s)
        }
    }
}
