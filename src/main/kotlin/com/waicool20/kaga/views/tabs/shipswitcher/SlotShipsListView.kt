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
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.scene.control.ListView
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.layout.HBox
import javafx.util.StringConverter
import tornadofx.*

class SlotShipsListView : Fragment() {
    override val root: HBox by fxml("/views/slotships/list.fxml")
    override val scope = super.scope as ShipSwitcherTabView.SlotShipsEditScope

    private val slotShipsListView: ListView<ShipSpecification> by fxid()

    init {
        val converter = object : StringConverter<ShipSpecification>() {
            override fun toString(specification: ShipSpecification) = specification.asConfigString()
            override fun fromString(string: String) = ShipSpecification.parse(string)
        }
        slotShipsListView.setCellFactory {
            TextFieldListCell<ShipSpecification>().apply { setConverter(converter) }
        }
        slotShipsListView.onUserSelect { spec ->
            scope.set(SlotShipEditModel(slotShipsListView, slotShipsListView.items.indexOf(spec)))
            workspace.dock<SlotShipsEditorView>()
        }
        onRefresh()
    }

    override fun onCreate() {
        super.onCreate()
        scope.set(SlotShipEditModel(slotShipsListView))
        workspace.dock<SlotShipsEditorView>()
    }

    override fun onRefresh() {
        super.onRefresh()
        slotShipsListView.items = SimpleListProperty(FXCollections.observableList(scope.slot.get().map { ShipSpecification.parse(it) }))
    }

    override fun onDelete() {
        super.onDelete()
        slotShipsListView.items.remove(slotShipsListView.selectedItem)
    }

    override fun onSave() {
        super.onSave()
        scope.slot.get().setAll(slotShipsListView.items.map { it.asConfigString() })
        close()
    }
}
