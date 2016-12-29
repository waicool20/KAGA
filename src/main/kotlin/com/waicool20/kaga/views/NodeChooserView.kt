package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.cell.TextFieldListCell
import javafx.stage.Stage


class NodeChooserView(val group: Int) {
    @FXML private lateinit var listView: ListView<String>

    @FXML private fun initialize() {
        listView.cellFactory = TextFieldListCell.forListView()
        with (Kaga.PROFILE!!.lbas) {
            when (group) {
                1 -> listView.items.addAll(group1Nodes)
                2 -> listView.items.addAll(group2Nodes)
                3 -> listView.items.addAll(group3Nodes)
                else -> listView.items.add("Unknown Group")
            }
        }
        val addText = "<Add Item>"
        listView.items.add(addText)
        listView.setOnEditCommit { event -> run {
            with (listView.items) {
                if (event.index != size - 1) {
                    removeAt(event.index)
                    if (event.newValue != "") add(event.index, event.newValue)
                    listView.selectionModel.select(event.index)
                } else {
                    if (event.newValue == "") {
                        removeAt(event.index)
                        add(addText)
                    } else {
                        add(size - 1, event.newValue)
                    }
                }
                event.consume()
            }
        }}
    }

    @FXML private fun onSaveButton() {
        with (Kaga.PROFILE!!.lbas) {
            with (listView.items) {
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

    @FXML private fun onCancelButton() = close()

    private fun close() = (listView.scene.window as Stage).close()
}
