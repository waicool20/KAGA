package com.waicool20.kaga.views

import com.waicool20.kaga.util.TeeOutputStream
import com.waicool20.kaga.util.TextAreaOutputStream
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import tornadofx.View
import java.io.PrintStream


class ConsoleView : View() {
    override val root: GridPane by fxml("/views/console.fxml", hasControllerAttribute = true)
    private val consoleTextArea: TextArea by fxid()
    private var outStream: PrintStream? = null
    private var errStream: PrintStream? = null

    init {
        val textArea = TextAreaOutputStream(consoleTextArea, 1000)
        outStream = PrintStream(TeeOutputStream(System.out, textArea))
        errStream = PrintStream(TeeOutputStream(System.err, textArea))
        System.setOut(outStream)
        System.setErr(errStream)
    }

    @FXML private fun onClear() = consoleTextArea.clear()
}
