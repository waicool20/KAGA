package com.waicool20.kaga.util

import com.sun.javafx.scene.control.skin.TableHeaderRow
import com.waicool20.kaga.Kaga
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
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

fun ReadOnlyObjectProperty<Scene>.setInitialSize(width: Double?, height: Double?, asMinimum: Boolean) {
    addListener { obs, oldVal, newVal ->
        newVal?.windowProperty()?.addListener { obs, oldVal, newVal ->
            newVal?.addEventFilter(WindowEvent.WINDOW_SHOWN, { event ->
                with(event.target as Stage) {
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
    columns.addListener(ListChangeListener<TableColumn<*, *>> { change ->
        while (change.next()) {
            change.addedSubList.forEach { column -> column.isResizable = false }
        }
    })
    columns.forEach { column -> column.isResizable = false }
}

fun TableView<*>.disableHeaderMoving() {
    widthProperty().addListener { obs, oldVal, newVal ->
        val row = lookup("TableHeaderRow") as TableHeaderRow
        row.reorderingProperty().addListener({ obs, oldVal, newVal -> row.isReordering = false })
    }
}

fun TableColumn<*, *>.setWidthRatio(tableView: TableView<*>, ratio: Double) =
        this.prefWidthProperty().bind(tableView.widthProperty().subtract(20).multiply(ratio))

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

fun <T> Spinner<T>.updateOtherSpinnerOnWrap(spinner: Spinner<T>, min: T, max: T) {
    this.valueProperty().addListener { obs, oldVal, newVal ->
        if (oldVal == max && newVal == min) {
            spinner.increment()
        } else if (oldVal == min && newVal == max) {
            spinner.decrement()
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
                      content: String): Alert {
        with(Alert(type)) {
            this.title = title
            this.headerText = header
            this.contentText = content
            setOnHidden { stage?.toFront() }
            return this
        }
    }

    fun info(stage: Stage? = Kaga.ROOT_STAGE, title: String = "KAGA - Info",
             header: String? = null, content: String): Alert {
        return alert(Alert.AlertType.INFORMATION, stage, title, header, content)
    }

    fun warn(stage: Stage? = Kaga.ROOT_STAGE, title: String = "KAGA - Warning",
             header: String? = null, content: String): Alert {
        return alert(Alert.AlertType.WARNING, stage, title, header, content)
    }
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
            val cell = TableCell<T, String>()
            cell.textProperty().bind(javafx.beans.binding.Bindings.`when`(cell.emptyProperty())
                    .then("")
                    .otherwise(cell.indexProperty().add(start).asString()))
            cell
        }
    }
}

class OptionsColumn(text: String = "", options: List<String>, table: TableView<String>) : TableColumn<String, String>(text) {
    init {
        val addText = "<Add Item>"
        setCellFactory {
            with(ComboBoxTableCell<String, String>()) {
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
                items.setAll(if (index != table.items.size - 1) addText else "")
                items.addAll(options)
                this
            }
        }
        setOnEditCommit { event ->
            run {
                with(table.items) {
                    val index = event.tablePosition.row
                    if (index != size - 1) {
                        removeAt(index)
                        if (event.newValue != addText) add(index, event.newValue)
                        table.selectionModel.select(index)
                    } else {
                        if (event.newValue != addText) {
                            add(size - 1, event.newValue)
                        }
                    }
                    table.refresh()
                    event.consume()
                }
            }
        }
        table.items.addListener(ListChangeListener<String> { change ->
            if (change.next()) {
                if (table.items[table.items.size - 1] != addText) {
                    table.items.add(addText)
                }
            }
        })
    }
}
