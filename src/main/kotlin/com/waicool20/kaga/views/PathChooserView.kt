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
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import tornadofx.*


class PathChooserView : View() {
    override val root: GridPane by fxml("/views/path-chooser.fxml", hasControllerAttribute = true)
    private val pathChooserFlavorText: Label by fxid()
    private val sikulixJarPathTextField: TextField by fxid()
    private val kcaDirTextField: TextField by fxid()
    private val saveButton: Button by fxid()
    private val pathErrorsText: Label by fxid()

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        pathChooserFlavorText.text = "Hello there Admiral! " +
                "This might be your first time starting up this application " +
                "or there was a problem finding the files below! " +
                "Either way before you begin your adventures, " +
                "please configure the paths first!"
        checkErrors()
    }

    @FXML
    private fun openSikulixJarChooser() = FileChooser().run {
        title = "Path to Sikulix Jar File..."
        extensionFilters.add(FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"))
        showOpenDialog(null)?.let {
            Kaga.CONFIG.sikulixJarPath = it.toPath()
            sikulixJarPathTextField.text = it.path
            checkErrors()
        }
    }

    @FXML
    private fun openKCAutoRootChooser() = DirectoryChooser().run {
        title = "Path to KCAuto root directory..."
        showDialog(null)?.let {
            Kaga.CONFIG.kcaRootDirPath = it.toPath()
            kcaDirTextField.text = it.path
            checkErrors()
        }
    }

    @FXML
    private fun onSaveButtonPressed() {
        if (Kaga.CONFIG.isValid()) {
            logger.info("Configuration was found valid! Starting main application...")
            Kaga.CONFIG.save()
            (saveButton.scene.window as Stage).close()
            Kaga.startMainApplication()
        }
    }

    private fun checkErrors() {
        sikulixJarPathTextField.style = "-fx-border-color:${if (!Kaga.CONFIG.sikulixJarIsValid()) "red" else "inherit"}"
        kcaDirTextField.style = "-fx-border-color: ${if (!Kaga.CONFIG.kancolleAutoRootDirPathIsValid()) "red" else "inherit"}"
        setErrorText()
    }

    private fun setErrorText() {
        var errors = ""
        if (sikulixJarPathTextField.style.contains("red")) {
            errors += "Invalid Sikuli Jar File!\n"
        }
        if (kcaDirTextField.style.contains("red")) {
            errors += "Invalid Kancolle Auto directory!\n"
        }
        pathErrorsText.text = errors
    }
}
