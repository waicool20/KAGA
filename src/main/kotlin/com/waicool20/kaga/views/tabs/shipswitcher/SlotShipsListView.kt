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

import com.waicool20.kaga.config.Kanmusu
import com.waicool20.kaga.config.ShipSpecification
import com.waicool20.kaga.config.ShipSpecification.Companion.parse
import com.waicool20.kaga.config.ShipSpecificationByPosition
import com.waicool20.kaga.util.AlertFactory
import javafx.beans.property.SimpleListProperty
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.util.StringConverter
import org.controlsfx.glyphfont.Glyph
import tornadofx.*

class SlotShipsListView : Fragment() {
    override val root: HBox by fxml("/views/tabs/slotships/list.fxml")
    override val scope = super.scope as ShipSwitcherTabView.SlotShipsEditScope

    private val slotShipsListView: ListView<ShipSpecification> by fxid()

    init {
        val converter = object : StringConverter<ShipSpecification>() {
            override fun toString(specification: ShipSpecification) = specification.asConfigString()
            override fun fromString(string: String) = ShipSpecification.parse(string)
        }
        with(slotShipsListView) {
            selectionModel.selectionMode = SelectionMode.MULTIPLE
            slotShipsListView.setCellFactory {
                TextFieldListCell<ShipSpecification>().apply { setConverter(converter) }
            }
            slotShipsListView.onUserSelect { spec ->
                scope.set(SlotShipEditModel(slotShipsListView, slotShipsListView.items.indexOf(spec)))
                workspace.dock<SlotShipsEditorView>()
            }
            setOnKeyPressed {
                if (it.code == KeyCode.DELETE) items.removeAll(selectionModel.selectedItems)
            }
        }
        onRefresh()

        val listener = ChangeListener<Number> { _, _, newVal ->
            if (newVal.toInt() % 4 == 0) workspace.currentStage?.sizeToScene()
        }

        workspace.currentStage?.heightProperty()?.addListener(listener)
        workspace.currentStage?.widthProperty()?.addListener(listener)
    }

    override fun onDock() {
        super.onDock()
        workspace.currentStage?.sizeToScene()
        workspace.button("KC3 Import") {
            graphic = Glyph("FontAwesome", "UPLOAD")
            action {
                FileChooser().apply {
                    title = "Import KC3 Ship List..."
                    extensionFilters += ExtensionFilter("KC3 Ship List (*.html)", "*.html")
                    extensionFilters += ExtensionFilter("All Files (*.*)", "*.*")
                }.showOpenDialog(null)?.also {
                    val ships = Kanmusu.parseFromKc3ShipList(it.readText())
                    if (ships.isNotEmpty()) {
                        workspace.dock<SlotShipsImporterView>(Scope(workspace),
                                "ships" to ships, "listView" to slotShipsListView)
                    } else {
                        AlertFactory.error(
                                content = "Not a valid KC3 Ship List!"
                        ).showAndWait()
                    }
                }
            }
            visibleWhen {
                slotShipsListView.items.sizeProperty.booleanBinding(workspace.dockedComponentProperty) {
                    val list = slotShipsListView.items
                    val docked = workspace.dockedComponent == this@SlotShipsListView
                    val listValid = list != null && (list.isEmpty() || list.firstOrNull() is ShipSpecificationByPosition)
                    docked && listValid
                }
            }
        }
    }

    override fun onCreate() {
        scope.set(SlotShipEditModel(slotShipsListView))
        workspace.dock<SlotShipsEditorView>()
    }

    override fun onRefresh() {
        slotShipsListView.items = SimpleListProperty(scope.slot.map(::parse).observable())
    }

    override fun onDelete() {
        slotShipsListView.items.removeAll(slotShipsListView.selectionModel.selectedItems)
    }

    override fun onSave() {
        scope.slot.setAll(slotShipsListView.items.map { it.asConfigString() })
        close()
    }
}
