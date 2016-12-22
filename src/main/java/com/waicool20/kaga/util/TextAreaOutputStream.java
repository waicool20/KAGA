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

    @Override
    public void write(int b) throws IOException {
        appendText(String.valueOf((char)b));
    }

    private void appendText(String valueOf) {
        Platform.runLater(() -> {
            console.appendText(valueOf);
            console.setScrollTop(Double.MAX_VALUE);
        });
    }
}
