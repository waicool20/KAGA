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
import com.waicool20.kaga.views.SingleListView
import com.waicool20.waicoolutils.javafx.columns.IndexColumn
import com.waicool20.waicoolutils.javafx.disableHeaderMoving
import com.waicool20.waicoolutils.javafx.lockColumnWidths
import com.waicool20.waicoolutils.javafx.setWidthRatio
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.input.KeyCode

data class NightBattleEntry(val node: SimpleStringProperty, val nightBattle: SimpleBooleanProperty) {
    fun isValid(): Boolean = node.isNotNull.value
}

class NightBattlesChooserView : SingleListView<NightBattleEntry>(showControlButtons = true) {

    init {
        title = "KAGA - Night Battle Chooser"
        val indexColumn = IndexColumn<NightBattleEntry>("#", 1).apply {
            setWidthRatio(tableView(), 0.20)
        }
        val nodeColumn = TableColumn<NightBattleEntry, String>("Node").apply {
            cellFactory = ComboBoxTableCell.forTableColumn(KancolleAutoProfile.VALID_NODES)
            setCellValueFactory { it.value.node }
            setWidthRatio(tableView(), 0.40)
            isSortable = false
        }

        val destNodeColumn = TableColumn<NightBattleEntry, Boolean>("Night Battle?").apply {
            cellFactory = CheckBoxTableCell.forTableColumn(this)
            setCellValueFactory { it.value.nightBattle }
            setWidthRatio(tableView(), 0.40)
            isSortable = false
        }

        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(indexColumn, nodeColumn, destNodeColumn)
        val items = Kaga.PROFILE.sortie.nightBattles.map { str ->
            str.takeLastWhile { it != ':' }.toBoolean().let {
                NightBattleEntry(SimpleStringProperty(str.takeWhile { it != ':' }), SimpleBooleanProperty(it))
            }
        }

        tableView().setOnKeyPressed {
            if (it.code == KeyCode.SPACE) {
                tableView().selectionModel.selectedItems.forEach { it.nightBattle.set(!it.nightBattle.get()) }
            }
        }
        tableView().items.addAll(items)
    }

    override fun onAddButton() {
        with(tableView().items) {
            if (isEmpty() || last().isValid()) {
                add(NightBattleEntry(SimpleStringProperty("${size + 1}"), SimpleBooleanProperty()))
            }
        }
    }

    override fun onSaveButton() {
        tableView().items.filter { it.isValid() }
                .map { "${it.node.value}:${it.nightBattle.value}" }
                .let { Kaga.PROFILE.sortie.nightBattlesProperty.setAll(it) }
        closeWindow()
    }
}
