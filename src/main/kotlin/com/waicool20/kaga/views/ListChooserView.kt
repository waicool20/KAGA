package com.waicool20.kaga.views

import javafx.scene.Parent
import javafx.scene.control.ListView
import javafx.stage.Stage
import javafx.stage.WindowEvent
import tornadofx.Fragment

abstract class ListChooserView : Fragment() {
    override val root: Parent by fxml("/views/listview.fxml")
    protected val rightListView: ListView<String> by fxid()
    protected val leftListView: ListView<String> by fxid()

    init {
        root.sceneProperty().addListener { obs, oldVal, newVal ->
            newVal?.windowProperty()?.addListener { obs, oldVal, newVal ->
                newVal?.addEventFilter(WindowEvent.WINDOW_SHOWN, { event ->
                    with(event.target as Stage) {
                        minHeight = height + 25
                        minWidth = width + 25
                    }
                })
            }
        }
    }

    abstract fun toRightButton()
    abstract fun toLeftButton()
    abstract fun onSaveButton()
    abstract fun onCancelButton()
}
