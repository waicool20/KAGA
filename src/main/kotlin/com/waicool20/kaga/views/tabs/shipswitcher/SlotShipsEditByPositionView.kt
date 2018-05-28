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

import com.waicool20.kaga.config.ShipSpecificationByPosition
import com.waicool20.kaga.handlers.MouseIncrementHandler
import com.waicool20.kaga.util.javafx.EnumCapitalizedNameConverter
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import tornadofx.*

class SlotShipsEditByPositionView : View() {
    override val root: VBox by fxml("/views/tabs/slotships/editor-position.fxml")
    private val sortByComboBox: ComboBox<ShipSpecificationByPosition.SortCriteria> by fxid()
    private val orderComboBox: ComboBox<ShipSpecificationByPosition.Order> by fxid()
    private val offsetSpinner: Spinner<Int> by fxid()
    private val spec: ShipSpecificationByPosition by param()
    private val editModel: SlotShipEditModel by inject()

    init {
        disableRefresh()
        disableDelete()
        disableCreate()
        sortByComboBox.converter = EnumCapitalizedNameConverter()
        orderComboBox.converter = EnumCapitalizedNameConverter()
        sortByComboBox.items.addAll(ShipSpecificationByPosition.SortCriteria.values())
        orderComboBox.items.addAll(ShipSpecificationByPosition.Order.values())
        offsetSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999)
        sortByComboBox.selectionModel.select(spec.sortBy)
        orderComboBox.selectionModel.select(spec.order)
        offsetSpinner.valueFactory.value = spec.offset
    }

    override fun onDock() {
        super.onDock()
        val handler = MouseIncrementHandler(1000L, 40)
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, handler)
        root.addEventFilter(MouseEvent.MOUSE_RELEASED, handler)
    }

    override fun onSave() {
        super.onSave()
        val s = ShipSpecificationByPosition(
                sortByComboBox.selectedItem!!,
                orderComboBox.selectedItem!!,
                offsetSpinner.value)
        if (editModel.index != -1) {
            editModel.items.value[editModel.index] = s
        } else {
            editModel.items.value.add(s)
        }
    }
}
