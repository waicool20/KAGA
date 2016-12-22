package com.waicool20.kaga.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {
    private TextArea console;

    public TextAreaOutputStream(TextArea console) {
        this.console = console;
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
            console.appendText(string);
            console.setScrollTop(Double.MAX_VALUE);
            console.setText(console.getText().replaceAll("<ESC>\\[.+?m", ""));
        });
    }
}
