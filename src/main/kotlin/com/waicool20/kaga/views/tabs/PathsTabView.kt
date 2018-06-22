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

package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import java.awt.Desktop
import java.nio.file.Path
import kotlin.concurrent.thread

class PathsTabView {
    @FXML private lateinit var sikulixJarPathLink: Hyperlink
    @FXML private lateinit var kcaRootPathLink: Hyperlink

    @FXML
    fun initialize() {
        setValues()
    }

    fun setValues() {
        with(Kaga.CONFIG) {
            sikulixJarPathLink.setOnAction { openFile(sikulixJarPath.parent) }
            kcaRootPathLink.setOnAction { openFile(kcaRootDirPath) }
            sikulixJarPathLink.text = sikulixJarPath.toString()
            kcaRootPathLink.text = kcaRootDirPath.toString()
        }
    }

    private fun openFile(path: Path) {
        if (Desktop.isDesktopSupported()) {
            thread { Desktop.getDesktop().open(path.toFile()) }
            Kaga.ROOT_STAGE.toBack()
        }
    }
}
