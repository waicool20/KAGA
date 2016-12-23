package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.config.KancolleAutoProfile;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;

public class SchedulingTabController {
    @FXML private CheckBox enableSleepButton;
    @FXML private TextField startTimeTextField;
    @FXML private Spinner<Double> sleepLengthSpinner;

    @FXML private CheckBox enableAutoStopButton;
    @FXML private ChoiceBox<KancolleAutoProfile.ScheduledStopMode> modeChoiceBox;
    @FXML private Spinner<Integer> countSpinner;

    @FXML private GridPane sleepContent;
    @FXML private GridPane stopContent;

    @FXML public void initialize() {
        setValues();
        createBindings();
    }


    private void setValues() {
        sleepLengthSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 0.1));
        modeChoiceBox.getItems().setAll(KancolleAutoProfile.ScheduledStopMode.values());
        countSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    private void createBindings() {
        enableSleepButton.selectedProperty()
            .bindBidirectional(Kaga.PROFILE.getScheduledSleep().enabledProperty());
        startTimeTextField.textProperty()
            .bindBidirectional(Kaga.PROFILE.getScheduledSleep().startTimeProperty(),
                new NumberStringConverter("#"));
        ObjectBindings.bindBidirectionally(sleepLengthSpinner.getValueFactory().valueProperty(),
            Kaga.PROFILE.getScheduledSleep().lengthProperty());

        enableAutoStopButton.selectedProperty()
            .bindBidirectional(Kaga.PROFILE.getScheduledStop().enabledProperty());
        modeChoiceBox.valueProperty()
            .bindBidirectional(Kaga.PROFILE.getScheduledStop().modeProperty());
        ObjectBindings.bindBidirectionally(countSpinner.getValueFactory().valueProperty(),
            Kaga.PROFILE.getScheduledStop().countProperty());

        sleepContent.visibleProperty().bind(enableSleepButton.selectedProperty());
        stopContent.visibleProperty().bind(enableAutoStopButton.selectedProperty());
    }
}
