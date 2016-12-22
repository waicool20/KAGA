package com.waicool20.kaga.handlers;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.concurrent.TimeUnit;

public class MouseIncrementHandler implements EventHandler<MouseEvent> {

    private final long delay;
    private final AnimationTimer timer;
    private Spinner spinner;
    private boolean isIncrementing;
    private long startTimestamp;

    public MouseIncrementHandler(long delayMillis, long incrementInterval) {
        delay = 1000000L * delayMillis;
        this.timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now - startTimestamp >= delay) {
                    if (isIncrementing) {
                        spinner.increment();
                    } else {
                        spinner.decrement();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(incrementInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Node node = (Node) event.getTarget();
                if (node instanceof StackPane && node.getParent() instanceof Spinner) {
                    StackPane button = (StackPane) node;
                    Spinner spinner = (Spinner) node.getParent();

                    Boolean increment = null;

                    if (button.getStyleClass().contains("increment-arrow-button")) {
                        increment = Boolean.TRUE;
                    } else if (button.getStyleClass().contains("decrement-arrow-button")) {
                        increment = Boolean.FALSE;
                    }

                    if (increment != null) {
                        event.consume();
                        this.spinner = spinner;
                        this.isIncrementing = increment;
                        startTimestamp = System.nanoTime();
                        timer.handle(startTimestamp + delay);
                        timer.start();
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                timer.stop();
                spinner = null;
            }
        }

    }
}
