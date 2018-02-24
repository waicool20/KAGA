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
import com.waicool20.kaga.config.KancolleAutoProfile.CombatFormation
import com.waicool20.kaga.util.IndexColumn
import com.waicool20.kaga.util.disableHeaderMoving
import com.waicool20.kaga.util.lockColumnWidths
import com.waicool20.kaga.util.setWidthRatio
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.util.StringConverter

data class FormationEntry(val node: SimpleStringProperty, val formation: SimpleObjectProperty<CombatFormation>) {
    fun isValid() = node.isNotNull.value && formation.isNotNull.value
}

class FormationChooserView : SingleListView<FormationEntry>(showControlButtons = true) {

    init {
        title = "KAGA - Formation Chooser"
        val indexColumn = IndexColumn<FormationEntry>("#", 1).apply {
            setWidthRatio(tableView(), 0.20)
        }
        val nodeColumn = TableColumn<FormationEntry, String>("Node").apply {
            cellFactory = ComboBoxTableCell.forTableColumn(KancolleAutoProfile.VALID_NODES)
            setCellValueFactory { it.value.node }
            setWidthRatio(tableView(), 0.40)
            isSortable = false
        }

        val formationColumn = TableColumn<FormationEntry, CombatFormation>("Formation").apply {
            val converter = object : StringConverter<CombatFormation>() {
                override fun toString(formation: CombatFormation?) = formation?.prettyString ?: ""
                override fun fromString(string: String?) = CombatFormation.fromPrettyString(string
                        ?: "")
            }
            cellFactory = ComboBoxTableCell.forTableColumn(converter, FXCollections.observableList(CombatFormation.values().toList()))
            setCellValueFactory { it.value.formation }
            setWidthRatio(tableView(), 0.40)
            isSortable = false
        }

        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(indexColumn, nodeColumn, formationColumn)
        val items = Kaga.PROFILE.sortie.formations.mapNotNull { str ->
            CombatFormation.values().find {
                it.toString().equals(str.takeLastWhile { c -> c != ':' }, true)
            }?.let { formation ->
                        FormationEntry(SimpleStringProperty(str.takeWhile { it != ':' }), SimpleObjectProperty(formation))
                    }
        }
        tableView().items.addAll(items)
    }

    override fun onAddButton() {
        if (tableView().items.let { it.isEmpty() || it.last().isValid() }) {
            tableView().items.add(FormationEntry(SimpleStringProperty(), SimpleObjectProperty()))
        }
    }

    override fun onSaveButton() {
        tableView().items.filter { it.isValid() }
                .map { "${it.node.value}:${it.formation.value}" }
                .let { Kaga.PROFILE.sortie.formationsProperty.setAll(it) }
        closeWindow()
    }
}
