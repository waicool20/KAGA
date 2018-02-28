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
import com.waicool20.kaga.config.ShipSpecificationByPosition
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import org.slf4j.LoggerFactory
import tornadofx.*

private typealias Entry = Pair<Kanmusu, SimpleBooleanProperty>

class SlotShipsImporterView : View() {
    override val root: VBox by fxml("/views/slotships/importer.fxml")
    private val kanmusuTable: TableView<Entry> by fxid()
    private val ships: List<Kanmusu> by param()
    private val listView: ListView<ShipSpecification> by param()
    private val shipEntries = ships.map { it to SimpleBooleanProperty(false) }
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onDock() {
        logger.info("Loaded ${ships.size} entries from KC3 Ship List")
        with(kanmusuTable) {
            selectionModel.selectionMode = SelectionMode.MULTIPLE
            items.setAll(shipEntries)
            column("Drop ID", Kanmusu::class) {
                value { it.value.first.dropId }
            }
            column("Name", Kanmusu::class) {
                value { it.value.first.name }
                remainingWidth()
            }
            column("Level", Kanmusu::class) {
                value { it.value.first.level }
            }
            column("Class", Kanmusu::class) {
                value { it.value.first.shipClass }
            }
            column("Fleet", Kanmusu::class) {
                value { it.value.first.fleet }
            }
            column<Entry, Boolean?>("Enable", "enabled") {
                value { it.value.second }
                useCheckbox()
            }
            columnResizePolicy = SmartResize.POLICY
            setOnKeyPressed {
                if (it.code == KeyCode.SPACE) {
                    selectionModel.selectedItems.forEach { it.second.set(!it.second.value) }
                }
            }
        }
        workspace.currentStage?.sizeToScene()
    }

    override fun onUndock() {
        workspace.viewStack.remove(this)
    }

    override fun onSave() {
        val specs = kanmusuTable.items.sortedBy { it.first.dropId }
                .mapIndexed { index, entry ->
                    ShipSpecificationByPosition(
                            order = ShipSpecificationByPosition.Order.END_OF_LIST,
                            offset = index + 1
                    ) to entry.second
                }.filter { it.second.value }
                .map { it.first }.sortedBy { it.offset }
        logger.info("Imported ${specs.size} entries!")
        listView.items.addAll(specs)
        workspace.navigateBack()
    }
}
