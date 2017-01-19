package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.setInitialSceneSizeAsMin
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.stage.Stage
import tornadofx.Fragment


abstract class SingleListView<T> : Fragment() {
    final override val root: Parent by fxml("/views/single-list.fxml")
    private val tableView: TableView<T> by fxid()

    init {
        root.setInitialSceneSizeAsMin()
    }

    abstract fun onSaveButton()
    abstract fun onCancelButton()

    protected open fun tableView(): TableView<T> = tableView
    protected fun close() {
        (tableView().scene.window as Stage).close()
        Kaga.ROOT_STAGE.toFront()
    }
}
