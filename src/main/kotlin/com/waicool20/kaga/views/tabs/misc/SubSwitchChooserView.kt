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

package com.waicool20.kaga.views.tabs.misc

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile.Submarines
import com.waicool20.kaga.util.disableHeaderMoving
import com.waicool20.kaga.util.lockColumnWidths
import com.waicool20.kaga.util.setWidthRatio
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.CheckBoxTableCell

/* TODO Disabled temporarily till kcauto-kai is finalized
class SubSwitchChooserView : SingleListView<Submarines>() {
    val enabledSubs = mutableMapOf<Submarines, SimpleBooleanProperty>()

    init {
        title = "Submarine Switching Chooser"
        val subNameColumn = TableColumn<Submarines, String>("Submarine")
        val subClassColumn = TableColumn<Submarines, String>("Class")
        val enableColumn = TableColumn<Submarines, Boolean>("Enable")

        subNameColumn.setWidthRatio(tableView(), 0.5)
        subClassColumn.setWidthRatio(tableView(), 0.25)
        enableColumn.setWidthRatio(tableView(), 0.25)
        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(subNameColumn, subClassColumn, enableColumn)

        subNameColumn.setCellValueFactory { SimpleStringProperty(it.value.prettyString) }
        subClassColumn.setCellValueFactory {
            when (it.value.isSSV) {
                true -> SimpleStringProperty("SSV")
                false -> SimpleStringProperty("SS")
                else -> SimpleStringProperty("")
            }
        }
        Submarines.values().forEach {
            enabledSubs.put(it, SimpleBooleanProperty(Kaga.PROFILE.submarineSwitch.enabledSubs.contains(it)))
        }
        enableColumn.cellFactory = CheckBoxTableCell.forTableColumn(enableColumn)
        enableColumn.setCellValueFactory { enabledSubs[it.value] }
        tableView().items.addAll(enabledSubs.keys)
    }

    override fun onSaveButton() {
        Kaga.PROFILE.submarineSwitch.enabledSubs.setAll(enabledSubs.filter({ it.value.value }).keys)
        closeWindow()
    }
}
*/
