package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class LbasTabController {
    @FXML private CheckBox enableButton;
    @FXML private CheckBox group1CheckBox;
    @FXML private CheckBox group2CheckBox;
    @FXML private CheckBox group3CheckBox;

    @FXML public void initialize() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getLbas().enabledProperty());
    }

    @FXML private void onConfigureGroup1NodesButton() {

    }

    @FXML private void onConfigureGroup2NodesButton() {

    }

    @FXML private void onConfigureGroup3NodesButton() {

    }
}
