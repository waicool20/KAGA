package com.waicool20.kaga.handlers;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyEvent;

public class KeyboardIncrementHandler implements EventHandler<KeyEvent> {
    @Override public void handle(KeyEvent keyEvent) {
        EventTarget target = keyEvent.getTarget();
        if (target instanceof Spinner) {
            Spinner spinner = (Spinner) target;
            switch (keyEvent.getCode()) {
                case UP:
                    spinner.increment();
                    break;
                case DOWN:
                    spinner.decrement();
                    break;
            }
        }
    }
}
