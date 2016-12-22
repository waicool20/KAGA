package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class QuestsTabController {

    @FXML private CheckBox enableButton;
    @FXML private Spinner<Integer> checkScheduleSpinner;

    @FXML public void initialize() {
        setValues();
        createBindings();
    }

    private void setValues() {
        checkScheduleSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    private void createBindings() {
        enableButton.selectedProperty()
            .bindBidirectional(Kaga.PROFILE.getQuests().enabledProperty());
        ObjectBindings.bindBidirectionally(checkScheduleSpinner.getValueFactory().valueProperty(),
            Kaga.PROFILE.getQuests().checkScheduleProperty());
    }

    @FXML private void onConfigureQuestsButton() {

    }
}
