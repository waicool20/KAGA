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


class SubSwitchChooserView : SingleListView<Submarines>() {
    val enabledSubs = mutableMapOf<Submarines, SimpleBooleanProperty>()

    init {
        title = "Submarine Switching Chooser"
        val subNameColumn = TableColumn<Submarines, String>("Submarine")
        val enableColumn = TableColumn<Submarines, Boolean>("Enable")

        subNameColumn.setWidthRatio(tableView(), 0.75)
        enableColumn.setWidthRatio(tableView(), 0.25)
        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(subNameColumn, enableColumn)

        subNameColumn.setCellValueFactory { data ->
            when (data.value.prettyString) {
                "All" -> SimpleStringProperty("All (Checking this ignores others)")
                else -> SimpleStringProperty(data.value.prettyString)
            }
        }
        Submarines.values().forEach {
            enabledSubs.put(it, SimpleBooleanProperty(Kaga.PROFILE!!.submarineSwitch.enabledSubs.contains(it)))
        }
        enableColumn.cellFactory = CheckBoxTableCell.forTableColumn(enableColumn)
        enableColumn.setCellValueFactory { data -> enabledSubs[data.value] }
        tableView().items.addAll(enabledSubs.keys)
    }

    override fun onSaveButton() {
        Kaga.PROFILE!!.submarineSwitch.enabledSubs.setAll(enabledSubs.filter({ it.value.value }).keys)
        close()
    }

    override fun onCancelButton() {
        close()
    }
}
