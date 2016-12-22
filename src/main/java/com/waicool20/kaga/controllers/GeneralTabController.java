package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.config.KancolleAutoProfile;
import com.waicool20.kaga.util.ObjectBindings;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class GeneralTabController {
    @FXML private TextField programTextField;
    @FXML private ChoiceBox<KancolleAutoProfile.RecoveryMethod> recoveryMethodChoiceBox;
    @FXML private Label sikuliScriptJarPathLabel;
    @FXML private Label kancolleAutoRootPathLabel;
    @FXML private CheckBox basicRecoveryCheckBox;
    @FXML private Spinner<Integer> paranoiaSpinner;
    @FXML private Spinner<Integer> sleepCycleSpinner;
    @FXML private Spinner<Integer> sleepModifierSpinner;

    boolean increasing = false;

    @FXML public void initialize() {
        setValues();
        createBindings();
    }

    private void setValues() {
        sikuliScriptJarPathLabel.setText(Kaga.CONFIG.getSikuliScriptJarPath().toString());
        kancolleAutoRootPathLabel.setText(Kaga.CONFIG.getKancolleAutoRootDirPath().toString());
        recoveryMethodChoiceBox.getItems().setAll(KancolleAutoProfile.RecoveryMethod.values());
        paranoiaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        sleepCycleSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        sleepModifierSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    private void createBindings() {
        recoveryMethodChoiceBox.valueProperty().bindBidirectional(Kaga.PROFILE.getGeneral().recoveryMethodProperty());
        programTextField.textProperty().bindBidirectional(Kaga.PROFILE.getGeneral().programProperty());
        basicRecoveryCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getGeneral().basicRecoveryProperty());
        ObjectBindings.bindBidirectionally(paranoiaSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getGeneral().paranoiaProperty());
        ObjectBindings.bindBidirectionally(sleepCycleSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getGeneral().sleepCycleProperty());
        ObjectBindings.bindBidirectionally(sleepModifierSpinner.getValueFactory().valueProperty(), Kaga.PROFILE.getGeneral().sleepModifierProperty());
    }
}
