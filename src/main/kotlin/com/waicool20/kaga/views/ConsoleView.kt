package com.waicool20.kaga.views

import com.waicool20.kaga.util.TextAreaOutputStream
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import tornadofx.View
import java.io.PrintStream


class ConsoleView : View() {
    override val root: GridPane by fxml("/views/console.fxml", hasControllerAttribute = true)
    private val consoleTextArea: TextArea by fxid()
    private var printStream: PrintStream? = null

    init {
        printStream = PrintStream(TextAreaOutputStream(consoleTextArea, 1000))
        System.setOut(printStream)
        System.setErr(printStream)
    }

    @FXML private fun onClear() {
        consoleTextArea.text = ""
    }
}
