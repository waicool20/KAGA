package com.waicool20.kaga.views.tabs.misc

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.IndexColumn
import com.waicool20.kaga.util.disableHeaderMoving
import com.waicool20.kaga.util.lockColumnWidths
import com.waicool20.kaga.util.setWidthRatio
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell


class NodeChooserView(val group: Int) : SingleListView<String>() {
    @FXML private lateinit var tableView: TableView<String>

    @FXML private fun initialize() {
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.setWidthRatio(tableView, 0.25)

        val imageNameColumn = TableColumn<String, String>("Image Name")
        imageNameColumn.setWidthRatio(tableView, 0.75)
        imageNameColumn.setCellValueFactory { data -> SimpleStringProperty(data.value) }
        imageNameColumn.cellFactory = TextFieldTableCell.forTableColumn()
        imageNameColumn.isSortable = false
        tableView.lockColumnWidths()
        tableView.disableHeaderMoving()
        tableView.columns.addAll(nodeNumColumn, imageNameColumn)

        with(Kaga.PROFILE!!.lbas) {
            when (group) {
                1 -> tableView.items.addAll(group1Nodes)
                2 -> tableView.items.addAll(group2Nodes)
                3 -> tableView.items.addAll(group3Nodes)
                else -> tableView.items.add("Unknown Group")
            }
        }
        val addText = "<Add Item>"
        tableView.items.add(addText)
        imageNameColumn.setOnEditCommit { event ->
            run {
                with(tableView.items) {
                    val index = event.tablePosition.row
                    if (index != size - 1) {
                        removeAt(index)
                        if (event.newValue != "") add(index, event.newValue)
                        tableView.selectionModel.select(index)
                    } else {
                        if (event.newValue == "") {
                            removeAt(index)
                            add(addText)
                        } else {
                            add(size - 1, event.newValue)
                        }
                    }
                    event.consume()
                }
            }
        }
    }

    override fun tableView() = tableView

    @FXML override fun onSaveButton() {
        with(Kaga.PROFILE!!.lbas) {
            with(tableView.items) {
                when (group) {
                    1 -> group1Nodes.setAll(subList(0, size - 1))
                    2 -> group2Nodes.setAll(subList(0, size - 1))
                    3 -> group3Nodes.setAll(subList(0, size - 1))
                    else -> null
                }
            }
        }
        close()
    }

    @FXML override fun onCancelButton() = close()
}
