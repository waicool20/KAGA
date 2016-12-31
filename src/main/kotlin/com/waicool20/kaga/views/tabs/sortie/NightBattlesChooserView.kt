package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.IndexColumn
import com.waicool20.kaga.util.disableHeaderMoving
import com.waicool20.kaga.util.lockColumnWidths
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.util.StringConverter


class NightBattlesChooserView : SingleListView<String>() {

    init {
        title = "KAGA - Night Battle Chooser"
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.prefWidthProperty().bind(tableView().widthProperty().multiply(0.25))

        val nightBattleColumn = TableColumn<String, String>("Night Battle?")
        nightBattleColumn.prefWidthProperty().bind(tableView().widthProperty().multiply(0.75))

        nightBattleColumn.isSortable = false
        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(nodeNumColumn, nightBattleColumn)

        nightBattleColumn.setCellValueFactory { data -> SimpleStringProperty(data.value) }
        val addText = "<Add Item>"
        nightBattleColumn.setCellFactory {
            with(ComboBoxTableCell<String, String>()) {
                converter = object : StringConverter<String>() {
                    override fun toString(string: String?): String {
                        if (index != tableView.items.size - 1) {
                            return if (string == addText) "" else string ?: ""
                        } else {
                            return string ?: ""
                        }
                    }

                    override fun fromString(string: String?): String = ""
                }
                items.add(if (index != tableView().items.size - 1) addText else "")
                items.addAll("Yes", "No")
                this
            }
        }
        tableView().items.addAll(Kaga.PROFILE!!.sortie.nightBattles.map { if (it) "Yes" else "No" })
        tableView().items.add(addText)
        nightBattleColumn.setOnEditCommit { event ->
            run {
                with(tableView().items) {
                    val index = event.tablePosition.row
                    if (index != size - 1) {
                        removeAt(index)
                        if (event.newValue != addText) add(index, event.newValue)
                        tableView().selectionModel.select(index)
                    } else {
                        if (event.newValue != addText) {
                            add(size - 1, event.newValue)
                        }
                    }
                    event.consume()
                }
            }
        }
    }

    override fun onSaveButton() {
        with(tableView().items) {
            Kaga.PROFILE!!.sortie.nightBattles.setAll(subList(0, size - 1).map { it == "Yes" })
        }
        close()
    }

    override fun onCancelButton() = close()
}
