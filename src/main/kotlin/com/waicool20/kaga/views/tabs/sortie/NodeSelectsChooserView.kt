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
import com.waicool20.kaga.views.NodeChooserView
import javafx.fxml.FXML

private val regexMap = mapOf("_node_E-(\\d)-(\\w)".toRegex() to "E(\\d): Node (\\w)".toRegex(),
        "node_(\\d)-(\\d)-(\\w)".toRegex() to "(\\d)-(\\d): Node (\\w)".toRegex())

class NodeSelectsChooserView : NodeChooserView("Selection", regexMap) {
    @FXML override fun initialize() {
        super.initialize()
        nodeColumn?.filter = { _, string ->
            !tableView.items.contains(string)
        }
        tableView.items.addAll(Kaga.PROFILE.sortie.nodeSelects.mapNotNull { converter?.toPrettyString(it) })
    }

    override fun save(items: List<String>) {
        Kaga.PROFILE.sortie.nodeSelects.setAll(tableView.items.mapNotNull { converter?.toImageName(it) }.dropLast(1))
        closeWindow()
    }
}


