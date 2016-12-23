package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpeditionsTabController {
    @FXML private CheckBox enableButton;
    @FXML private ComboBox<Integer> fleet2ComboBox;
    @FXML private ComboBox<Integer> fleet3ComboBox;
    @FXML private ComboBox<Integer> fleet4ComboBox;

    @FXML private GridPane content;

    @FXML public void initialize() {
        setValues();
        createBindings();
    }

    private void setValues() {
        fleet2ComboBox.getItems()
            .setAll(IntStream.range(1, 41).boxed().collect(Collectors.toList()));
        fleet3ComboBox.getItems()
            .setAll(IntStream.range(1, 41).boxed().collect(Collectors.toList()));
        fleet4ComboBox.getItems()
            .setAll(IntStream.range(1, 41).boxed().collect(Collectors.toList()));
    }

    private void createBindings() {
        enableButton.selectedProperty()
            .bindBidirectional(Kaga.PROFILE.getExpeditions().enabledProperty());
        ObjectBindings.bindBidirectionally(fleet2ComboBox.valueProperty(),
            Kaga.PROFILE.getExpeditions().fleet2Property());
        ObjectBindings.bindBidirectionally(fleet3ComboBox.valueProperty(),
            Kaga.PROFILE.getExpeditions().fleet3Property());
        ObjectBindings.bindBidirectionally(fleet4ComboBox.valueProperty(),
            Kaga.PROFILE.getExpeditions().fleet4Property());

        content.visibleProperty().bind(enableButton.selectedProperty());
    }
}
