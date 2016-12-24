package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.config.KancolleAutoProfile;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import javax.swing.*;

public class SchedulingTabController {
    @FXML private CheckBox enableSleepButton;
    @FXML private Spinner<Integer> startTimeHourSpinner;
    @FXML private Spinner<Integer> startTimeMinSpinner;
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
        startTimeHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        startTimeMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        startTimeHourSpinner.getValueFactory().setWrapAround(true);
        startTimeMinSpinner.getValueFactory().setWrapAround(true);
        startTimeHourSpinner.getEditor().setAlignment(Pos.CENTER);
        startTimeMinSpinner.getEditor().setAlignment(Pos.CENTER);
        StringConverter<Integer> formatter = new StringConverter<Integer>() {
            @Override public String toString(Integer integer) {
                return integer == null ? "00" : String.format("%02d", integer);
            }

            @Override public Integer fromString(String s) {
                return Integer.parseInt(s);
            }
        };
        startTimeHourSpinner.getEditor().setTextFormatter(new TextFormatter<>(formatter));
        startTimeMinSpinner.getEditor().setTextFormatter(new TextFormatter<>(formatter));
        String startTime = Kaga.PROFILE.getScheduledSleep().getStartTime();
        startTimeHourSpinner.getValueFactory().setValue(Integer.parseInt(startTime.substring(0, 2)));
        startTimeMinSpinner.getValueFactory().setValue(Integer.parseInt(startTime.substring(2, 4)));
        sleepLengthSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 0.1));
        modeChoiceBox.getItems().setAll(KancolleAutoProfile.ScheduledStopMode.values());
        countSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    private void createBindings() {
        enableSleepButton.selectedProperty()
            .bindBidirectional(Kaga.PROFILE.getScheduledSleep().enabledProperty());
        Kaga.PROFILE.getScheduledSleep().startTimeProperty().bind(
            Bindings.concat(startTimeHourSpinner.getValueFactory().valueProperty().asString("%02d"),
                startTimeMinSpinner.getValueFactory().valueProperty().asString("%02d")));
        startTimeMinSpinner.getValueFactory().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal == 59 && newVal == 0) {
                startTimeHourSpinner.increment();
            } else if (oldVal == 0 && newVal == 59) {
                startTimeHourSpinner.decrement();
            }
        });
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
