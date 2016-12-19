package com.waicool20.kaga.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleController {
    @FXML TextArea consoleTextArea;
    private PrintStream printStream;

    @FXML public void initialize() {
        Console console = new Console(consoleTextArea);
        printStream = new PrintStream(console);
        System.setOut(printStream);
        System.setErr(printStream);
    }

    @FXML private void onClear() {
        consoleTextArea.setText("");
    }

    public class Console extends OutputStream {
        private TextArea console;

        public Console(TextArea console) {
            this.console = console;
        }

        public void appendText(String valueOf) {
            Platform.runLater(() -> console.appendText(valueOf));
        }

        public void write(int b) throws IOException {
            appendText(String.valueOf((char)b));
        }
    }
}
