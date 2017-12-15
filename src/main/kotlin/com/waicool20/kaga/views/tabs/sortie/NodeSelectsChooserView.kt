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

package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.IndexColumn
import com.waicool20.kaga.util.disableHeaderMoving
import com.waicool20.kaga.util.lockColumnWidths
import com.waicool20.kaga.util.setWidthRatio
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.ComboBoxTableCell
import tornadofx.*

data class NodeSelect(val source: SimpleStringProperty, val destination: SimpleStringProperty) {
    fun isValid() = source.isNotNull.value && destination.isNotNull.value
}

class NodeSelectsChooserView : SingleListView<NodeSelect>(showControlButtons = true) {

    init {
        title = "KAGA - Node Selects Chooser"
        val indexColumn = IndexColumn<NodeSelect>("#", 1).apply {
            setWidthRatio(tableView(), 0.20)
        }
        val sourceNodeColumn = TableColumn<NodeSelect, String>("Source Node").apply {
            cellFactory = ComboBoxTableCell.forTableColumn(KancolleAutoProfile.VALID_NODES)
            setCellValueFactory { it.value.source }
            setWidthRatio(tableView(), 0.40)
            isSortable = false
        }

        val destNodeColumn = TableColumn<NodeSelect, String>("Destination Node").apply {
            cellFactory = ComboBoxTableCell.forTableColumn(KancolleAutoProfile.VALID_NODES)
            setCellValueFactory { it.value.destination }
            setWidthRatio(tableView(), 0.40)
            isSortable = false
        }

        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(indexColumn, sourceNodeColumn, destNodeColumn)
        val items = Kaga.PROFILE.sortie.nodeSelects.map {
            NodeSelect(SimpleStringProperty("${it.first()}"), SimpleStringProperty("${it.last()}"))
        }
        tableView().items.addAll(items)
    }

    override fun onAddButton() {
        if (tableView().items.last().isValid()) {
            tableView().items.add(NodeSelect(SimpleStringProperty(), SimpleStringProperty()))
        }
    }

    override fun onRemoveButton() {
        tableView().selectedItem?.let { tableView().items.remove(it) }
    }

    override fun onSaveButton() {
        tableView().items.filter { it.isValid() }
                .map { "${it.source.value}>${it.destination.value}" }
                .let {
                    Kaga.PROFILE.sortie.nodeSelects.setAll(it)
                }
        close()
    }
}
