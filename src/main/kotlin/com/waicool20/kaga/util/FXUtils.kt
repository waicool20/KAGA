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

package com.waicool20.kaga.util

import com.sun.javafx.scene.control.skin.TableHeaderRow
import com.waicool20.kaga.Kaga
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.WindowEvent
import javafx.util.Callback
import javafx.util.StringConverter
import java.util.concurrent.TimeUnit


fun Parent.setInitialSceneSizeAsMin() = sceneProperty().setInitialSizeAsMin()
fun ReadOnlyObjectProperty<Scene>.setInitialSizeAsMin() = setInitialSize(null, null, true)
fun Parent.setInitialSceneSize(width: Double, height: Double, asMinimum: Boolean) = sceneProperty().setInitialSize(width, height, asMinimum)

fun ObservableValue<*>.listen(unit: () -> Unit) = addListener { _ -> unit.invoke() }

fun ReadOnlyObjectProperty<Scene>.setInitialSize(width: Double?, height: Double?, asMinimum: Boolean) {
    addListener { _, _, newVal ->
        newVal?.windowProperty()?.addListener { _, _, newWindow ->
            newWindow?.addEventFilter(WindowEvent.WINDOW_SHOWN, {
                with(it.target as Stage) {
                    if (width != null && height != null) {
                        this.width = width
                        this.height = height
                    }
                    if (asMinimum) {
                        minHeight = this.height + 25
                        minWidth = this.width + 25
                    }
                }
            })
        }
    }
}

fun TableView<*>.lockColumnWidths() {
    columns.addListener(ListChangeListener<TableColumn<*, *>> {
        while (it.next()) {
            it.addedSubList.forEach { column -> column.isResizable = false }
        }
    })
    columns.forEach { column -> column.isResizable = false }
}

fun TableView<*>.disableHeaderMoving() {
    widthProperty().listen {
        val row = lookup("TableHeaderRow") as TableHeaderRow
        row.reorderingProperty().listen { -> row.isReordering = false }
    }
}

fun TableColumn<*, *>.setWidthRatio(tableView: TableView<*>, ratio: Double) =
        prefWidthProperty().bind(tableView.widthProperty().subtract(20).multiply(ratio))

fun Node.getParentTabPane(): TabPane? {
    var parentNode = parent
    while (parentNode != null) {
        if (parentNode is TabPane) {
            return parentNode
        } else {
            parentNode = parentNode.parent
        }
    }
    return null
}

fun TabPane.setSideWithHorizontalText(side: Side, width: Double = 100.0) {
    this.side = side
    if (side == Side.TOP || side == Side.BOTTOM) return
    tabMinHeight = width
    tabMaxHeight = width
    tabs.forEach { tab ->
        var text = tab.text
        if (text == "" && tab.properties.containsKey("text")) {
            text = tab.properties["text"].toString()
        } else {
            tab.properties.put("text", tab.text)
        }
        val rotation = if (side == Side.LEFT) 90.0 else -90.0
        val label = Label(text)
        val pane = StackPane(Group(label))
        label.rotate = rotation
        pane.rotate = rotation
        tab.graphic = pane
        tab.text = ""
    }
    isRotateGraphic = true
}

private val spinnerWraps = mutableMapOf<Spinner<*>, Boolean>()
fun <T> Spinner<T>.updateOtherSpinnerOnWrap(spinner: Spinner<T>, min: T, max: T) {
    spinnerWraps.putIfAbsent(this, false)
    addEventHandler(MouseEvent.ANY, { event ->
        if (event.eventType == MouseEvent.MOUSE_PRESSED ||
                event.eventType == MouseEvent.MOUSE_RELEASED) {
            if (event.button == MouseButton.PRIMARY) {
                val node = event.target as Node
                if (node is StackPane && node.getParent() is Spinner<*>) {
                    if (node.styleClass.contains("increment-arrow-button") ||
                            node.styleClass.contains("decrement-arrow-button")) {
                        spinnerWraps.put(this, event.eventType == MouseEvent.MOUSE_PRESSED)
                    }
                }
            }
        }
    })
    valueProperty().addListener { _, oldVal, newVal ->
        if (spinnerWraps[this] ?: false) {
            if (oldVal == max && newVal == min) {
                spinner.increment()
            } else if (oldVal == min && newVal == max) {
                spinner.decrement()
            }
        }
    }
}

fun Spinner<Int>.asTimeSpinner(unit: TimeUnit) {
    val formatter = object : StringConverter<Int>() {
        override fun toString(integer: Int?): String =
                if (integer == null) "00" else String.format("%02d", integer)

        override fun fromString(s: String): Int = s.toInt()
    }
    editor.textFormatter = TextFormatter(formatter)
    editor.alignment = Pos.CENTER
    valueFactory = when (unit) {
        TimeUnit.DAYS -> SpinnerValueFactory.IntegerSpinnerValueFactory(0, 31)
        TimeUnit.HOURS -> SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23)
        TimeUnit.MINUTES -> SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59)
        TimeUnit.SECONDS -> SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59)
        else -> SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0)
    }
    valueFactory.isWrapAround = true
}

object AlertFactory {
    private fun alert(type: Alert.AlertType, stage: Stage? = Kaga.ROOT_STAGE,
                      title: String = "KAGA - Info", header: String? = null,
                      content: String) = Alert(type).apply {
        this.title = title
        this.headerText = header
        this.contentText = content
        setOnHidden { stage?.toFront() }
    }

    fun info(stage: Stage? = Kaga.ROOT_STAGE,
             title: String = "KAGA - Info",
             header: String? = null,
             content: String) =
            alert(Alert.AlertType.INFORMATION, stage, title, header, content)

    fun warn(stage: Stage? = Kaga.ROOT_STAGE,
             title: String = "KAGA - Warning",
             header: String? = null,
             content: String) =
            alert(Alert.AlertType.WARNING, stage, title, header, content)

    fun error(stage: Stage? = Kaga.ROOT_STAGE,
              title: String = "KAGA - Warning",
              header: String? = null,
              content: String) =
            alert(Alert.AlertType.ERROR, stage, title, header, content)
}

class DeselectableCellFactory<T> : Callback<ListView<T>, ListCell<T>> {
    override fun call(viewList: ListView<T>): ListCell<T> {
        val cell = object : ListCell<T>() {
            override fun updateItem(item: T, empty: Boolean) {
                super.updateItem(item, empty)
                text = item?.toString()
            }
        }
        with(cell) {
            addEventFilter(MouseEvent.MOUSE_PRESSED, { event ->
                viewList.requestFocus()
                if (!cell.isEmpty) {
                    val index = cell.index
                    with(viewList.selectionModel) {
                        if (selectedIndices.contains(index)) {
                            clearSelection(index)
                        } else {
                            select(index)
                        }
                    }
                    event.consume()
                }
            })
        }
        return cell
    }
}

class NoneSelectableCellFactory(val regex: Regex) : Callback<ListView<String>, ListCell<String>> {
    override fun call(p0: ListView<String>?): ListCell<String> {
        return object : ListCell<String>() {
            override fun updateItem(item: String?, empty: Boolean) {
                super.updateItem(item, empty)
                if (item != null) {
                    if (empty) {
                        text = null
                        isDisable = false
                    } else {
                        text = item
                        isDisable = item.matches(regex)
                    }
                }
            }
        }
    }
}

class IndexColumn<T>(text: String = "", start: Int = 0) : TableColumn<T, String>(text) {
    init {
        isSortable = false
        setCellFactory {
            TableCell<T, String>().apply {
                textProperty().bind(javafx.beans.binding.Bindings.`when`(emptyProperty())
                        .then("")
                        .otherwise(indexProperty().add(start).asString()))
            }
        }
    }
}

class OptionsColumn(text: String = "", var options: List<String>, table: TableView<String>,
                    var filter: (cell: TableCell<String, String>, string: String) -> Boolean = { _, _ -> true },
                    var maxRows: Int = Integer.MAX_VALUE) : TableColumn<String, String>(text) {
    init {
        val addText = "<Add Item>"
        setCellFactory {
            ComboBoxTableCell<String, String>().apply {
                converter = object : StringConverter<String>() {
                    override fun toString(string: String?): String {
                        if (index != table.items.size - 1) {
                            return if (string == addText) "" else string ?: ""
                        } else {
                            return string ?: ""
                        }
                    }

                    override fun fromString(string: String?): String = ""
                }
                indexProperty().listen {
                    items.setAll(if (index != table.items.size - 1) addText else "")
                    items.addAll(options.filter { filter.invoke(this, it) })
                }
            }
        }
        setOnEditCommit { event ->
            with(table.items) {
                val index = event.tablePosition.row
                if (index != size - 1) {
                    removeAt(index)
                    if (event.newValue != addText) add(index, event.newValue)
                    table.selectionModel.select(index)
                } else {
                    if (event.newValue != addText && index < maxRows && event.newValue != "") {
                        add(size - 1, event.newValue)
                    }
                }
                table.refresh()
                event.consume()
            }
        }
        table.items.addListener(ListChangeListener<String> {
            if (it.next()) {
                if (table.items[table.items.size - 1] != addText) {
                    table.items.add(addText)
                }
            }
        })
        table.sceneProperty().addListener { _, _, newVal ->
            newVal?.windowProperty()?.addListener { _, _, newWindow ->
                newWindow?.addEventFilter(WindowEvent.WINDOW_SHOWN, {
                    if (table.items.size == 0) table.items.add(addText)
                })
            }
        }
    }
}
