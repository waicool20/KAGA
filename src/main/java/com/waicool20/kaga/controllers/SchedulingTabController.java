package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.config.KancolleAutoProfile;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

public class SchedulingTabController {
    @FXML private CheckBox enableSleepButton;
    @FXML private TextField startTimeTextField;
    @FXML private TextField sleepLengthTextField;

    @FXML private CheckBox enableAutoStopButton;
    @FXML private ChoiceBox<KancolleAutoProfile.ScheduledStopMode> modeChoiceBox;
    @FXML private TextField countTextField;

    @FXML public void initialize() {
        enableSleepButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getScheduledSleep().enabledProperty());
        startTimeTextField.textProperty().bindBidirectional(Kaga.PROFILE.getScheduledSleep().startTimeProperty(), new NumberStringConverter("#"));
        sleepLengthTextField.textProperty().bindBidirectional(Kaga.PROFILE.getScheduledSleep().lengthProperty(), new NumberStringConverter());

        enableAutoStopButton.selectedProperty().bindBidirectional(Kaga.PROFILE.getScheduledStop().enabledProperty());
        modeChoiceBox.getItems().setAll(KancolleAutoProfile.ScheduledStopMode.values());
        modeChoiceBox.valueProperty().bindBidirectional(Kaga.PROFILE.getScheduledStop().modeProperty());
        countTextField.textProperty().bindBidirectional(Kaga.PROFILE.getScheduledStop().countProperty(), new NumberStringConverter());
    }
}
