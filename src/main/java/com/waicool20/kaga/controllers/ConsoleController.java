package com.waicool20.kaga.controllers;

import com.waicool20.kaga.util.TextAreaOutputStream;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.PrintStream;

public class ConsoleController {
    @FXML TextArea consoleTextArea;
    private PrintStream printStream;

    @FXML public void initialize() {
        TextAreaOutputStream console = new TextAreaOutputStream(consoleTextArea, 1000);
        printStream = new PrintStream(console);
        System.setOut(printStream);
        System.setErr(printStream);
    }

    @FXML private void onClear() {
        consoleTextArea.setText("");
    }
}
