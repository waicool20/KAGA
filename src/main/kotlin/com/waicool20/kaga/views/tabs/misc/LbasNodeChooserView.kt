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
import com.waicool20.kaga.views.NodeChooserView
import javafx.fxml.FXML

private val regexMap = mapOf("_node_lbas_E-(\\d)-(\\w)_(1|2)".toRegex() to "E(\\d): Node (\\w) Selection (1|2)".toRegex(),
        "_node_lbas_E-(\\d)-(\\w)_(1|2)_(cleared)".toRegex() to "E(\\d): Node (\\w) Selection (1|2) \\[(\\w+?)\\]".toRegex(),
        "node_lbas_(\\d)-(\\d)-(\\w)_(1|2)".toRegex() to "(\\d)-(\\d): Node (\\w) Selection (1|2)".toRegex())

class LbasNodeChooserView(val group: Int) : NodeChooserView("LBAS Node", regexMap) {
    @FXML override fun initialize() {
        super.initialize()
        nodeColumn?.filter = { cell, string ->
            val check = (string.endsWith("1") || string.endsWith("1 [cleared]")) && !tableView.items.contains(string)
            when (cell.index) {
                0 -> check
                1 -> {
                    val node = tableView.items[0]
                    val selectionCheck = string == node.replace("Selection 1", "Selection 2")
                    string.startsWith(node.takeWhile { it != ':' }) &&
                            (check || selectionCheck) &&
                            string != node.replace(" [cleared]", "")
                }
                else -> false
            }
        }
        nodeColumn?.maxRows = 2

        with(Kaga.PROFILE.lbas) {
            when (group) {
                1 -> tableView.items.addAll(group1Nodes.map { converter?.toPrettyString(it) })
                2 -> tableView.items.addAll(group2Nodes.map { converter?.toPrettyString(it) })
                3 -> tableView.items.addAll(group3Nodes.map { converter?.toPrettyString(it) })
                else -> tableView.items.add("Unknown Group")
            }
        }
    }

    override fun save(items: List<String>) {
        with(Kaga.PROFILE.lbas) {
            when (group) {
                1 -> group1Nodes.setAll(items)
                2 -> group2Nodes.setAll(items)
                3 -> group3Nodes.setAll(items)
                else -> Unit
            }
        }
        close()
    }
}
