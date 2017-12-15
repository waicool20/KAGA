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
import com.waicool20.kaga.util.setInitialSceneSizeAsMin
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.ButtonBar
import javafx.scene.control.TableView
import javafx.stage.Stage
import tornadofx.*


abstract class SingleListView<T>(showControlButtons: Boolean = false) : Fragment() {
    final override val root: Parent by fxml("/views/single-list.fxml")
    private val tableView: TableView<T> by fxid()
    private val controlButtons: ButtonBar by fxid()

    init {
        root.setInitialSceneSizeAsMin()
        controlButtons.isVisible = showControlButtons
    }

    abstract fun onSaveButton()
    @FXML protected open fun onCancelButton() = closeWindow()
    @FXML protected open fun onAddButton() = Unit
    @FXML protected open fun onRemoveButton() {
        tableView().selectedItem?.let { tableView().items.remove(it) }
    }

    protected open fun tableView(): TableView<T> = tableView
    protected fun closeWindow() {
        (tableView().scene.window as Stage).close()
        Kaga.ROOT_STAGE.toFront()
    }
}
