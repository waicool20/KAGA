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

import com.waicool20.kaga.config.ShipSpecification
import com.waicool20.kaga.handlers.MouseIncrementHandler
import com.waicool20.util.javafx.EnumCapitalizedNameConverter
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import org.controlsfx.control.SegmentedButton
import tornadofx.*

abstract class SlotShipsEditByShipClassView<T> : View() {
    override val root: VBox by fxml("/views/tabs/slotships/editor-ship-class.fxml")
    protected val commonLabel: Label by fxid()
    protected val commonComboBox: ComboBox<T> by fxid()
    protected val levelFilterButton: SegmentedButton by fxid()
    protected val levelSpinner: Spinner<Int> by fxid()
    protected val lockComboBox: ComboBox<ShipSpecification.LockCriteria> by fxid()
    protected val ringComboBox: ComboBox<ShipSpecification.RingCriteria> by fxid()
    protected open val spec: ShipSpecification by param()
    protected val editModel: SlotShipEditModel by inject()

    protected val toggleMap = mapOf(
            ShipSpecification.LevelCriteria.NONE to ToggleButton("X"),
            ShipSpecification.LevelCriteria.GREATHER_THAN to ToggleButton(">"),
            ShipSpecification.LevelCriteria.LESS_THAN to ToggleButton("<")
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
                levelFilterButton.toggleGroup.selectedToggleProperty().isEqualTo(toggleMap[ShipSpecification.LevelCriteria.NONE])
        )
        lockComboBox.items.addAll(ShipSpecification.LockCriteria.values())
        ringComboBox.items.addAll(ShipSpecification.RingCriteria.values())
        lockComboBox.converter = EnumCapitalizedNameConverter()
        ringComboBox.converter = EnumCapitalizedNameConverter()
    }

    override fun onDock() {
        super.onDock()
        val handler = MouseIncrementHandler(1000L, 40)
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, handler)
        root.addEventFilter(MouseEvent.MOUSE_RELEASED, handler)
    }
}
