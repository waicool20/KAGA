package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PvpTabController {
    @FXML private CheckBox enableButton;
    @FXML private ComboBox<Integer> fleetCompComboBox;

    @FXML public void initialize() {
        setValues();
        createBindings();
    }

    private void setValues() {
        fleetCompComboBox.getItems().setAll(IntStream.range(1, 5).boxed().collect(Collectors.toList()));
    }

    private void createBindings() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getPvp().enabledProperty());
        ObjectBindings.bindBidirectionally(fleetCompComboBox.valueProperty(), Kaga.PROFILE.getPvp().fleetCompProperty());
    }
}
