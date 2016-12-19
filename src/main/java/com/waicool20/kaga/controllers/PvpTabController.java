package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PvpTabController {
    @FXML private CheckBox enableButton;
    @FXML private ComboBox<Integer> fleetCompComboBox;

    private IntegerProperty fleetComp;

    @FXML public void initialize() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getPvp().enabledProperty());
        fleetCompComboBox.getItems().setAll(IntStream.range(1, 5).boxed().collect(Collectors.toList()));
        fleetComp = IntegerProperty.integerProperty(fleetCompComboBox.valueProperty());
        fleetComp.bindBidirectional(Kaga.PROFILE.getPvp().fleetCompProperty());
    }
}
