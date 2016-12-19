package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SortieTabController {

    @FXML private CheckBox enableButton;
    @FXML private ComboBox<Integer> fleetCompComboBox;
    @FXML private ComboBox<Integer> areaComboBox;
    @FXML private ComboBox<Integer> subareaComboBox;
    @FXML private CheckBox combinedFleetCheckBox;
    @FXML private TextField nodesTextField;
    @FXML private TextField nodeSelectsTextField;
    @FXML private CheckBox nightBattlesCheckBox;
    @FXML private TextField retreatLimitTextField;
    @FXML private TextField repairLimitTextField;
    @FXML private TextField repairTimeLimitTextField;
    @FXML private CheckBox checkFatigueCheckBox;
    @FXML private CheckBox checkPortCheckBox;
    @FXML private CheckBox medalStopCheckBox;
    @FXML private CheckBox lastNodePushCheckBox;

    @FXML public void initialize() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().enabledProperty());

        fleetCompComboBox.getItems().setAll(IntStream.range(1, 5).boxed().collect(
            Collectors.toList()));
        areaComboBox.getItems().setAll(IntStream.range(1, 6).boxed().collect(
            Collectors.toList()));
        subareaComboBox.getItems().setAll(IntStream.range(1, 6).boxed().collect(
            Collectors.toList()));
        fleetCompComboBox.valueProperty().bindBidirectional(Kaga.PROFILE.getSortie().fleetCompProperty().asObject());
        areaComboBox.valueProperty().bindBidirectional(Kaga.PROFILE.getSortie().areaProperty().asObject());
        subareaComboBox.valueProperty().bindBidirectional(Kaga.PROFILE.getSortie().subAreaProperty().asObject());

        combinedFleetCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().combinedFleetProperty());
        nodesTextField.textProperty().bindBidirectional(Kaga.PROFILE.getSortie().nodesProperty(), new NumberStringConverter());
        nodeSelectsTextField.textProperty().bindBidirectional(Kaga.PROFILE.getSortie().nodeSelectsProperty());
        nightBattlesCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().nightBattlesProperty());
        retreatLimitTextField.textProperty().bindBidirectional(Kaga.PROFILE.getSortie().retreatLimitProperty(), new NumberStringConverter());
        repairLimitTextField.textProperty().bindBidirectional(Kaga.PROFILE.getSortie().repairLimitProperty(), new NumberStringConverter());
        repairTimeLimitTextField.textProperty().bindBidirectional(Kaga.PROFILE.getSortie().repairTimeLimitProperty(), new NumberStringConverter());
        checkFatigueCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().checkFatigueProperty());
        checkPortCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().portCheckProperty());
        medalStopCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().medalStopProperty());
        lastNodePushCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getSortie().lastNodePushProperty());
    }

    @FXML private void onNodesInc() {
        Kaga.PROFILE.getSortie().setNodes(Kaga.PROFILE.getSortie().getNodes() + 1);
    }

    @FXML private void onNodesDec() {
        Kaga.PROFILE.getSortie().setNodes(Kaga.PROFILE.getSortie().getNodes() - 1);
    }

    @FXML private void onConfigureFormationsButton() {
        // TODO configure formations button
    }

    @FXML private void onRetreatLimitInc() {
        Kaga.PROFILE.getSortie().setRetreatLimit(Kaga.PROFILE.getSortie().getRetreatLimit() + 1);
    }


    @FXML private void onRetreatLimitDec() {
        Kaga.PROFILE.getSortie().setRetreatLimit(Kaga.PROFILE.getSortie().getRetreatLimit() - 1);
    }

    @FXML private void onRepairLimitInc() {
        Kaga.PROFILE.getSortie().setRepairLimit(Kaga.PROFILE.getSortie().getRepairLimit() + 1);
    }

    @FXML private void onRepairLimitDec() {
        Kaga.PROFILE.getSortie().setRepairLimit(Kaga.PROFILE.getSortie().getRepairLimit() - 1);
    }

    @FXML private void onRepairTimeLimitInc() {
        Kaga.PROFILE.getSortie().setRepairTimeLimit(Kaga.PROFILE.getSortie().getRepairTimeLimit() + 1);
    }

    @FXML private void onRepairTimeLimitDec() {
        Kaga.PROFILE.getSortie().setRepairTimeLimit(Kaga.PROFILE.getSortie().getRepairTimeLimit() - 1);
    }
}
