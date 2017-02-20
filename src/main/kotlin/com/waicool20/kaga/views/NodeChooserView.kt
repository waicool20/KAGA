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
                    var string = it.value.toString().replace("\\\\(.)".toRegex(), {it.groupValues[1]})
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
                    var string = it.key.toString().replace("\\\\(.)".toRegex(), {it.groupValues[1]})
                    groups.subList(1, groups.size).forEach { s ->
                        string = string.replaceFirst("\\(.+?\\)".toRegex(), s)
                    }
                    return string
                }
            }
        }
        return ""
    }

    fun matches(string: String): Boolean {
        regexMap.keys.forEach {
            if (string.matches(it)) {
                return true
            }
        }
        return false
    }
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
