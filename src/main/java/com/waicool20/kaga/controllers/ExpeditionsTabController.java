package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpeditionsTabController {
    @FXML private CheckBox enableButton;
    @FXML private ComboBox<Integer> fleet2ComboBox;
    @FXML private ComboBox<Integer> fleet3ComboBox;
    @FXML private ComboBox<Integer> fleet4ComboBox;

    private IntegerProperty fleet2;
    private IntegerProperty fleet3;
    private IntegerProperty fleet4;

    @FXML public void initialize() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getExpeditions().enabledProperty());
        fleet2ComboBox.getItems().setAll(IntStream.range(1, 41).boxed().collect(Collectors.toList()));
        fleet3ComboBox.getItems().setAll(IntStream.range(1, 41).boxed().collect(Collectors.toList()));
        fleet4ComboBox.getItems().setAll(IntStream.range(1, 41).boxed().collect(Collectors.toList()));

        fleet2 = IntegerProperty.integerProperty(fleet2ComboBox.valueProperty());
        fleet3 = IntegerProperty.integerProperty(fleet3ComboBox.valueProperty());
        fleet4 = IntegerProperty.integerProperty(fleet4ComboBox.valueProperty());
        fleet2.bindBidirectional(Kaga.PROFILE.getExpeditions().fleet2Property());
        fleet3.bindBidirectional(Kaga.PROFILE.getExpeditions().fleet3Property());
        fleet4.bindBidirectional(Kaga.PROFILE.getExpeditions().fleet4Property());
    }
}
