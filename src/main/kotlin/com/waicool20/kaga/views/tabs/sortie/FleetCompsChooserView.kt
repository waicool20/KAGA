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
import com.waicool20.kaga.util.disableHeaderMoving
import com.waicool20.kaga.util.lockColumnWidths
import com.waicool20.kaga.util.setWidthRatio
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.CheckBoxTableCell
/*
data class FleetComp(val number: Int) {
    val enabledProperty = SimpleBooleanProperty(Kaga.PROFILE.sortie.fleetComps.contains(number))
}

class FleetCompsChooserView : SingleListView<FleetComp>() {
    val fleetCompList = (1..10).map(::FleetComp)

    init {
        title = "KAGA - FleetComps Chooser"

        val fleetCompColumn = TableColumn<FleetComp, String>("Fleet Comp")
        val enableColumn = TableColumn<FleetComp, Boolean>("Enable")


        fleetCompColumn.setWidthRatio(tableView(), 0.5)
        enableColumn.setWidthRatio(tableView(), 0.5)
        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(fleetCompColumn, enableColumn)

        fleetCompColumn.setCellValueFactory { SimpleStringProperty(it.value.number.toString()) }
        enableColumn.cellFactory = CheckBoxTableCell.forTableColumn(enableColumn)
        enableColumn.setCellValueFactory { it.value.enabledProperty }
        enableColumn.isEditable = true
        tableView().items.addAll(fleetCompList)
    }

    override fun onSaveButton() {
        Kaga.PROFILE.sortie.fleetComps
                .setAll(tableView().items.filter { it.enabledProperty.value }.map { it.number })
        closeWindow()
    }
}*/
