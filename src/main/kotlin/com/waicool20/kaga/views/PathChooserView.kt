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
    private val kancolleAutoDirTextField: TextField by fxid()
    private val saveButton: Button by fxid()
    private val pathErrorsText: Label by fxid()

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        pathChooserFlavorText.text =
                "Hello there Admiral! " +
                        "This might be your first time starting up this application " +
                        "or there was a problem finding the files below! " +
                        "Either way before you begin your adventures, " +
                        "please configure the paths first!"
        checkErrors()
    }

    @FXML private fun openSikulixJarChooser() {
        with(FileChooser()) {
            title = "Path to Sikulix Jar File..."
            extensionFilters.add(FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"))
            val file = showOpenDialog(null)
            if (file != null) {
                Kaga.CONFIG.sikulixJarPath = file.toPath()
                sikulixJarPathTextField.text = file.path
                checkErrors()
            }
        }
    }

    @FXML private fun openKancolleAutoRootChooser() {
        with(DirectoryChooser()) {
            title = "Path to Kancolle Auto root directory..."
            val directory = showDialog(null)
            if (directory != null) {
                Kaga.CONFIG.kancolleAutoRootDirPath = directory.toPath()
                kancolleAutoDirTextField.text = directory.path
                checkErrors()
            }
        }
    }

    @FXML private fun onSaveButtonPressed() {
        if (Kaga.CONFIG.isValid()) {
            logger.info("Configuration was found valid! Starting main application...")
            Kaga.CONFIG.save()
            (saveButton.scene.window as Stage).close()
            Kaga.INSTANCE.startMainApplication()
        }
    }

    private fun checkErrors() {
        sikulixJarPathTextField.style = "-fx-border-color:${if (!Kaga.CONFIG.sikulixJarIsValid()) "red" else "inherit"}"
        kancolleAutoDirTextField.style = "-fx-border-color: ${if (!Kaga.CONFIG.kancolleAutoRootDirPathIsValid()) "red" else "inherit"}"
        setErrorText()
    }

    private fun setErrorText() {
        var errors = ""
        if (sikulixJarPathTextField.style.contains("red")) {
            errors += "Invalid Sikuli Jar File!\n"
        }
        if (kancolleAutoDirTextField.style.contains("red")) {
            errors += "Invalid Kancolle Auto directory!\n"
        }
        pathErrorsText.text = errors
    }
}
