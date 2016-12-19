package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

public class QuestsTabController {

    @FXML private CheckBox enableButton;
    @FXML private TextField checkScheduleTextField;

    @FXML public void initialize() {
        enableButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getQuests().enabledProperty());
        checkScheduleTextField.textProperty().bindBidirectional(Kaga.PROFILE.getQuests().checkScheduleProperty(), new NumberStringConverter());
    }

    @FXML private void onConfigureQuestsButton() {

    }

    @FXML private void onCheckScheduleInc() {
        Kaga.PROFILE.getQuests().setCheckSchedule(Kaga.PROFILE.getQuests().getCheckSchedule() + 1);
    }

    @FXML private void onCheckScheduleDec() {
        Kaga.PROFILE.getQuests().setCheckSchedule(Kaga.PROFILE.getQuests().getCheckSchedule() + 1);
    }
}
