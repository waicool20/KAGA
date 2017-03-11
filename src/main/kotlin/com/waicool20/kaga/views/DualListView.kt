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

import com.waicool20.kaga.util.setInitialSceneSizeAsMin
import javafx.scene.Parent
import javafx.scene.control.ListView
import tornadofx.*

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
