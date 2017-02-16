package com.waicool20.kaga.views.tabs.misc

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.views.NodeChooserView
import javafx.fxml.FXML

private val regexMap = mapOf("_node_lbas_E-(\\d)-(\\w)_(1|2)".toRegex() to "E(\\d): Node (\\w) Selection (1|2)".toRegex(),
        "node_lbas_(\\d)-(\\d)-(\\w)_(1|2)".toRegex() to "(\\d)-(\\d): Node (\\w) Selection (1|2)".toRegex())

class LbasNodeChooserView(val group: Int) : NodeChooserView("LBAS Node", regexMap) {
    @FXML override fun initialize() {
        super.initialize()
        nodeColumn?.filter = { cell, string ->
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
        }
        nodeColumn?.maxRows = 2

        with(Kaga.PROFILE!!.lbas) {
            when (group) {
                1 -> tableView.items.addAll(group1Nodes.map { converter?.toPrettyString(it) })
                2 -> tableView.items.addAll(group2Nodes.map { converter?.toPrettyString(it) })
                3 -> tableView.items.addAll(group3Nodes.map { converter?.toPrettyString(it) })
                else -> tableView.items.add("Unknown Group")
            }
        }
    }

    override fun save(items: List<String>) {
        with(Kaga.PROFILE!!.lbas) {
            when (group) {
                1 -> group1Nodes.setAll(items)
                2 -> group2Nodes.setAll(items)
                3 -> group3Nodes.setAll(items)
                else -> null
            }
        }
        close()
    }
}
