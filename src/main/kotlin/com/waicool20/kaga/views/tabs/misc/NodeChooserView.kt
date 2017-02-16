package com.waicool20.kaga.views.tabs.misc

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.*
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.TableView
import java.nio.file.Files
import java.util.stream.Collectors


class NodeChooserView(val group: Int) : SingleListView<String>() {
    @FXML private lateinit var tableView: TableView<String>

    private object NodeImgStringConverter {
        val eventImgNameRegex = "_node_lbas_E-(\\d)-(\\w)_(1|2)".toRegex()
        val imgNameRegex = "node_lbas_(\\d)-(\\d)-(\\w)_(1|2)".toRegex()
        private val eventImgNameRegexI = "E(\\d): Node (\\w) Selection (1|2)".toRegex()
        private val imgNameRegexI = "(\\d)-(\\d): Node (\\w) Selection (1|2)".toRegex()

        fun toPrettyString(imageName: String?): String {
            if (imageName == null) return ""
            if (imageName.matches(eventImgNameRegex)) {
                val groups = eventImgNameRegex.matchEntire(imageName)?.groupValues
                if (groups != null) {
                    return "E${groups[1]}: Node ${groups[2]} Selection ${groups[3]}"
                }
            } else if (imageName.matches(imgNameRegex)) {
                val groups = imgNameRegex.matchEntire(imageName)?.groupValues
                if (groups != null) {
                    return "${groups[1]}-${groups[2]}: Node ${groups[3]} Selection ${groups[4]}"
                }
            }
            return ""
        }

        fun toImageName(prettyString: String?): String {
            if (prettyString == null) return ""
            if (prettyString.matches(eventImgNameRegexI)) {
                val groups = eventImgNameRegexI.matchEntire(prettyString)?.groupValues
                if (groups != null) {
                    return "_node_lbas_E-${groups[1]}-${groups[2]}_${groups[3]}"
                }
            } else if (prettyString.matches(imgNameRegexI)) {
                val groups = imgNameRegexI.matchEntire(prettyString)?.groupValues
                if (groups != null) {
                    return "node_lbas_${groups[1]}-${groups[2]}-${groups[3]}_${groups[4]}"
                }
            }
            return ""
        }
    }

    @FXML private fun initialize() {
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.setWidthRatio(tableView, 0.25)
        val selections = Files.walk(Kaga.CONFIG.kancolleAutoRootDirPath.resolve("kancolle_auto.sikuli/combat.sikuli"), 1)
                .map { it.fileName.toString().replace(".png", "") }
                .sorted()
                .filter { it.matches(NodeImgStringConverter.eventImgNameRegex) || it.matches(NodeImgStringConverter.imgNameRegex) }
                .map { NodeImgStringConverter.toPrettyString(it) }
                .collect(Collectors.toList<String>())
        val imageNameColumn = OptionsColumn("LBAS Nodes", selections, tableView, { cell, string ->
            val check = string.endsWith("1") && !tableView.items.contains(string)
            when (cell.index) {
                0 -> check
                1 -> {
                    val node1 = tableView.items[0]
                    string.startsWith(node1.takeWhile { it != ':' }) &&
                            (check || string == node1.replace("Selection 1", "Selection 2"))
                }
                else -> false
            }
        }, 2)
        imageNameColumn.setWidthRatio(tableView, 0.75)
        imageNameColumn.setCellValueFactory { data -> SimpleStringProperty(data.value) }
        imageNameColumn.isSortable = false

        tableView.lockColumnWidths()
        tableView.disableHeaderMoving()
        tableView.columns.addAll(nodeNumColumn, imageNameColumn)

        with(Kaga.PROFILE!!.lbas) {
            when (group) {
                1 -> tableView.items.addAll(group1Nodes.map { NodeImgStringConverter.toPrettyString(it) })
                2 -> tableView.items.addAll(group2Nodes.map { NodeImgStringConverter.toPrettyString(it) })
                3 -> tableView.items.addAll(group3Nodes.map { NodeImgStringConverter.toPrettyString(it) })
                else -> tableView.items.add("Unknown Group")
            }
        }
    }

    override fun tableView() = tableView

    @FXML override fun onSaveButton() {
        with(Kaga.PROFILE!!.lbas) {
            with(tableView.items.map { NodeImgStringConverter.toImageName(it) }) {
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
