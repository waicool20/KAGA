package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SortieTabController {

    @FXML private CheckBox enableButton;
    @FXML private ComboBox<Integer> fleetCompComboBox;
    @FXML private ComboBox<Integer> areaComboBox;
    @FXML private ComboBox<Integer> subareaComboBox;
    @FXML private CheckBox combinedFleetCheckBox;
    @FXML private Spinner<Integer> nodesSpinner;
    @FXML private TextField nodeSelectsTextField;
    @FXML private CheckBox nightBattlesCheckBox;
    @FXML private Spinner<Integer> retreatLimitSpinner;
    @FXML private Spinner<Integer> repairLimitSpinner;
    @FXML private Spinner<Integer> repairTimeLimitSpinner;
    @FXML private CheckBox checkFatigueCheckBox;
    @FXML private CheckBox checkPortCheckBox;
    @FXML private CheckBox medalStopCheckBox;
    @FXML private CheckBox lastNodePushCheckBox;

    @FXML public void initialize() {
        setValues();
        createBindings();
    }

    private void setValues() {
        fleetCompComboBox.getItems().setAll(IntStream.range(1, 5).boxed().collect(
            Collectors.toList()));
        areaComboBox.getItems().setAll(IntStream.range(1, 6).boxed().collect(
            Collectors.toList()));
        subareaComboBox.getItems().setAll(IntStream.range(1, 6).boxed().collect(
            Collectors.toList()));
        nodesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        retreatLimitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        repairLimitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        repairTimeLimitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    private void createBindings() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().enabledProperty());
        ObjectBindings.bindBidirectionally(fleetCompComboBox.valueProperty(), Kaga.PROFILE.getSortie().fleetCompProperty());
        ObjectBindings.bindBidirectionally(areaComboBox.valueProperty(), Kaga.PROFILE.getSortie().areaProperty());
        ObjectBindings.bindBidirectionally(subareaComboBox.valueProperty(), Kaga.PROFILE.getSortie().subAreaProperty());
        combinedFleetCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().combinedFleetProperty());
        ObjectBindings.bindBidirectionally(nodesSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getSortie().nodesProperty());
        nodeSelectsTextField.textProperty().bindBidirectional(Kaga.PROFILE.getSortie().nodeSelectsProperty());
        nightBattlesCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().nightBattlesProperty());
        ObjectBindings.bindBidirectionally(retreatLimitSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getSortie().retreatLimitProperty());
        ObjectBindings.bindBidirectionally(repairLimitSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getSortie().repairLimitProperty());
        ObjectBindings.bindBidirectionally(repairTimeLimitSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getSortie().repairTimeLimitProperty());
        checkFatigueCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().checkFatigueProperty());
        checkPortCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().portCheckProperty());
        medalStopCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().medalStopProperty());
        lastNodePushCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().lastNodePushProperty());
    }

    @FXML private void onConfigureFormationsButton() {
        // TODO configure formations button
    }
}
