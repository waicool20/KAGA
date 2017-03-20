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

package com.waicool20.kaga.views.tabs.quests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.*
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.Tooltip
import javafx.scene.control.cell.CheckBoxTableCell
import tornadofx.*

data class Quest(val id: String, val description: String, val requirements: List<String>) {
    val enabledProperty = SimpleBooleanProperty(Kaga.PROFILE!!.quests.quests.containsIgnoreCase(id))
}

class QuestsChooserView : SingleListView<Quest>() {
    val questList: List<Quest> = run {
        val stream = Kaga::class.java.classLoader.getResourceAsStream("valid_quests.json")
        val mapper = jacksonObjectMapper()
        mapper.readValue<List<Quest>>(stream, mapper.typeFactory.constructCollectionType(List::class.java, Quest::class.java))
    }

    init {
        title = "Kaga - Quests Chooser"
        root.setInitialSceneSize(600.0, 400.0, true)

        val indexColumn = TableColumn<Quest, String>("ID")
        val descColumn = TableColumn<Quest, String>("Description (Hover over quest for requirements)")
        val enableColumn = TableColumn<Quest, Boolean>("Enable")

        indexColumn.setWidthRatio(tableView(), 0.1)
        descColumn.setWidthRatio(tableView(), 0.75)
        enableColumn.setWidthRatio(tableView(), 0.15)
        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(indexColumn, descColumn, enableColumn)

        indexColumn.setCellValueFactory { SimpleStringProperty(it.value.id) }
        descColumn.setCellValueFactory { SimpleStringProperty(it.value.description) }
        descColumn.setCellFactory {
            with(TableCell<Quest, String>()) {
                this.itemProperty().addListener { _, _, newVal ->
                    if (newVal != null) {
                        val tooltip = Tooltip("- ${rowItem.requirements.joinToString("\n- ")}")
                        tooltip.isWrapText = true
                        tooltip.maxWidth = 500.0
                        Tooltip.install(this, tooltip)
                    }
                }
                textProperty().bind(itemProperty())
                this
            }
        }
        enableColumn.cellFactory = CheckBoxTableCell.forTableColumn(enableColumn)
        enableColumn.setCellValueFactory { it.value.enabledProperty }
        enableColumn.isEditable = true
        tableView().items.addAll(questList)
    }

    override fun onSaveButton() {
        Kaga.PROFILE!!.quests.quests.setAll(tableView().items
                .filter { it.enabledProperty.get() }
                .map { it.id.toLowerCase() }
                .sorted()
        )
        close()
    }
}
