package com.waicool20.kaga.views

import com.waicool20.kaga.util.setInitialSceneSizeAsMin
import javafx.scene.Parent
import javafx.scene.control.ListView
import tornadofx.Fragment

abstract class DualListView<T> : Fragment() {
    final override val root: Parent by fxml("/views/dual-list.fxml")
    protected val rightListView: ListView<T> by fxid()
    protected val leftListView: ListView<T> by fxid()

    init {
        root.setInitialSceneSizeAsMin()
    }

    abstract fun toRightButton()
    abstract fun toLeftButton()
    abstract fun onSaveButton()
    abstract fun onCancelButton()
}
