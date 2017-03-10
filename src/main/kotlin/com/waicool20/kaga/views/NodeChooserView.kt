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

package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.*
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.TableView
import java.nio.file.Files
import java.util.stream.Collectors

class NodeImgStringConverter(val regexMap: Map<Regex, Regex>) {

    fun toPrettyString(imageName: String?): String {
        if (imageName == null) return ""
        regexMap.entries.forEach {
            if (imageName.matches(it.key)) {
                val groups = it.key.matchEntire(imageName)?.groupValues
                if (groups != null) {
                    var string = it.value.toString().replace("\\\\(.)".toRegex(), { it.groupValues[1] })
                    groups.subList(1, groups.size).forEach { s ->
                        string = string.replaceFirst("\\(.+?\\)".toRegex(), s)
                    }
                    return string
                }
            }
        }
        return ""
    }

    fun toImageName(prettyString: String?): String {
        if (prettyString == null) return ""
        regexMap.entries.forEach {
            if (prettyString.matches(it.value)) {
                val groups = it.value.matchEntire(prettyString)?.groupValues
                if (groups != null) {
                    var string = it.key.toString().replace("\\\\(.)".toRegex(), { it.groupValues[1] })
                    groups.subList(1, groups.size).forEach { s ->
                        string = string.replaceFirst("\\(.+?\\)".toRegex(), s)
                    }
                    return string
                }
            }
        }
        return ""
    }

    fun matches(string: String): Boolean =
            regexMap.keys.map { string.matches(it) }.contains(true)
}

abstract class NodeChooserView(private val nodeColumnTitle: String, regexMap: Map<Regex, Regex>) : SingleListView<String>() {
    @FXML protected lateinit var tableView: TableView<String>
    protected val converter: NodeImgStringConverter? = NodeImgStringConverter(regexMap)
    protected var nodeColumn: OptionsColumn? = null

    @FXML open fun initialize() {
        if (converter == null) return
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.setWidthRatio(tableView, 0.25)
        val selections = Files.walk(Kaga.CONFIG.kancolleAutoRootDirPath.resolve("kancolle_auto.sikuli/combat.sikuli"), 1)
                .map { it.fileName.toString().replace(".png", "") }
                .sorted()
                .filter { converter.matches(it) }
                .map { converter.toPrettyString(it) }
                .collect(Collectors.toList<String>())
        nodeColumn = OptionsColumn(nodeColumnTitle, selections, tableView)
        nodeColumn?.setWidthRatio(tableView, 0.75)
        nodeColumn?.setCellValueFactory { data -> SimpleStringProperty(data.value) }
        nodeColumn?.isSortable = false

        tableView.lockColumnWidths()
        tableView.disableHeaderMoving()
        tableView.columns.addAll(nodeNumColumn, nodeColumn)
    }

    @FXML override fun onSaveButton() {
        with(tableView.items.map { converter?.toImageName(it) ?: "" }) {
            save(this.subList(0, size - 1))
        }
    }

    override fun tableView() = tableView

    abstract fun save(items: List<String>)
}
