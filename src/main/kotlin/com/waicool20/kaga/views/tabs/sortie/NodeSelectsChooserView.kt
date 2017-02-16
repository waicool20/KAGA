package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.views.NodeChooserView
import javafx.fxml.FXML

private val regexMap = mapOf("_node_E-(\\d)-(\\w)".toRegex() to "E(\\d): Node (\\w)".toRegex(),
        "node_(\\d)-(\\d)-(\\w)".toRegex() to "(\\d)-(\\d): Node (\\w)".toRegex())

class NodeSelectsChooserView : NodeChooserView("Selection", regexMap) {
    @FXML override fun initialize() {
        super.initialize()
        nodeColumn?.filter = { cell, string ->
            !tableView.items.contains(string)
        }
        tableView.items.addAll(Kaga.PROFILE!!.sortie.nodeSelects.map { converter?.toPrettyString(it) })
    }

    override fun save(items: List<String>) {
        Kaga.PROFILE!!.sortie.nodeSelects.setAll(tableView.items.map { converter?.toImageName(it) })
        close()
    }
}


