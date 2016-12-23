package com.waicool20.kaga.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {
    private TextArea console;
    private int maxLines;

    public TextAreaOutputStream(TextArea console, int maxLines) {
        this.console = console;
        this.maxLines = maxLines;
    }

    @Override public void write(int b) throws IOException {
        char c = (char) b;
        switch (c) {
            case '\u001B':
                appendText("<ESC>");
                break;
            default:
                appendText(String.valueOf(c));
                break;
        }

    }

    private void appendText(String string) {
        Platform.runLater(() -> {
            String current = console.getText();
            if (current.split("\n").length > maxLines) {
                current = current.replaceFirst(".+?\n", "");
            }
            current += string;
            console.setText(current.replaceAll("<ESC>\\[.+?m", ""));
            console.setScrollTop(Double.MAX_VALUE);
        });
    }
}
