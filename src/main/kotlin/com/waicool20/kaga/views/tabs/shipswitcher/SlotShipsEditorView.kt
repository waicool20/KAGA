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
import com.waicool20.kaga.config.ShipSpecificationByAsset
import com.waicool20.kaga.config.ShipSpecificationByPosition
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.util.StringConverter
import tornadofx.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class SlotShipEditModel(listView: ListView<ShipSpecification>, val index: Int = -1) : ViewModel() {
    val items = bind { listView.itemsProperty() }
}

class SlotShipsEditorView : Fragment() {
    override val root: BorderPane by fxml("/views/tabs/slotships/editor.fxml")
    private val shipSpecificationComboBox: ComboBox<KClass<out ShipSpecification>> by fxid()
    private val content: HBox by fxid()
    private val editModel: SlotShipEditModel by inject()

    private var currentEditView: Parent = content

    init {
        val specification = editModel.items.value.getOrNull(editModel.index)
        val converter = object : StringConverter<KClass<out ShipSpecification>>() {
            override fun toString(spec: KClass<out ShipSpecification>) = ShipSpecification.values[spec]
            override fun fromString(string: String) = ShipSpecification::class
        }
        shipSpecificationComboBox.converter = converter
        shipSpecificationComboBox.selectionModel.selectedItemProperty().addListener { _, _, newVal ->
            val param = "spec" to (specification ?: newVal.createInstance())
            val view = when (param.second) {
                is ShipSpecificationByPosition -> find<SlotShipsEditByPositionView>(Scope(editModel), param)
                is ShipSpecificationByAsset -> find<SlotShipsEditByAssetView>(Scope(editModel), param)
            }
            currentEditView.replaceWith(view.root)
            currentEditView = view.root
            forwardWorkspaceActions(view)
        }

        specification?.let {
            shipSpecificationComboBox.items.addAll(it::class)
            shipSpecificationComboBox.selectionModel.select(it::class)
            shipSpecificationComboBox.isDisable = true
        } ?: editModel.items.value.firstOrNull()?.let {
            shipSpecificationComboBox.items.addAll(it::class)
            shipSpecificationComboBox.selectionModel.select(it::class)
            shipSpecificationComboBox.isDisable = true
        } ?: shipSpecificationComboBox.items.addAll(ShipSpecification.values.keys)
    }

    override fun onUndock() {
        super.onUndock()
        workspace.viewStack.remove(this)
    }

    override fun onSave() {
        super.onSave()
        workspace.navigateBack()
    }
}
