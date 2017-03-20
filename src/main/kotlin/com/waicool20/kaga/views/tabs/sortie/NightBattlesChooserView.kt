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
import com.waicool20.kaga.util.*
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty


class NightBattlesChooserView : SingleListView<String>() {

    init {
        title = "KAGA - Night Battle Chooser"
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.setWidthRatio(tableView(), 0.25)

        val nightBattleColumn = OptionsColumn("Night Battle?", listOf("Yes", "No"), tableView())
        nightBattleColumn.setWidthRatio(tableView(), 0.75)
        nightBattleColumn.isSortable = false
        nightBattleColumn.setCellValueFactory { SimpleStringProperty(it.value) }

        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(nodeNumColumn, nightBattleColumn)
        tableView().items.addAll(Kaga.PROFILE!!.sortie.nightBattles.map { if (it) "Yes" else "No" })
    }

    override fun onSaveButton() {
        with(tableView().items) {
            Kaga.PROFILE!!.sortie.nightBattles.setAll(subList(0, size - 1).map { it == "Yes" })
        }
        close()
    }
}
