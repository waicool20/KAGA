package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.config.KancolleAutoProfile;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

public class GeneralTabController {
    @FXML private TextField programTextField;
    @FXML private ChoiceBox<KancolleAutoProfile.RecoveryMethod> recoveryMethodChoiceBox;
    @FXML private Label sikuliScriptJarPathLabel;
    @FXML private Label kancolleAutoRootPathLabel;
    @FXML private CheckBox basicRecoveryCheckBox;
    @FXML private TextField paranoiaTextField;
    @FXML private TextField sleepCycleTextField;
    @FXML private TextField sleepModifierTextField;

    @FXML public void initialize() {
        sikuliScriptJarPathLabel.setText(Kaga.CONFIG.getSikuliScriptJarPath().toString());
        kancolleAutoRootPathLabel.setText(Kaga.CONFIG.getKancolleAutoRootDirPath().toString());
        recoveryMethodChoiceBox.getItems().setAll(KancolleAutoProfile.RecoveryMethod.values());

        recoveryMethodChoiceBox.valueProperty().bindBidirectional(Kaga.PROFILE.getGeneral().recoveryMethodProperty());
        programTextField.textProperty().bindBidirectional(Kaga.PROFILE.getGeneral().programProperty());
        basicRecoveryCheckBox.selectedProperty().bindBidirectional(Kaga.PROFILE.getGeneral().basicRecoveryProperty());
        paranoiaTextField.textProperty().bindBidirectional(Kaga.PROFILE.getGeneral().paranoiaProperty(), new NumberStringConverter());
        sleepCycleTextField.textProperty().bindBidirectional(Kaga.PROFILE.getGeneral().sleepCycleProperty(), new NumberStringConverter());
        sleepModifierTextField.textProperty().bindBidirectional(Kaga.PROFILE.getGeneral().sleepModifierProperty(), new NumberStringConverter());
    }

    @FXML private void onParanoiaInc() {
        Kaga.PROFILE.getGeneral().setParanoia(Kaga.PROFILE.getGeneral().getParanoia() + 1);
    }

    @FXML private void onParanoiaDec() {
        Kaga.PROFILE.getGeneral().setParanoia(Kaga.PROFILE.getGeneral().getParanoia() - 1);
    }

    @FXML private void onSleepCycleInc() {
        Kaga.PROFILE.getGeneral().setSleepCycle(Kaga.PROFILE.getGeneral().getSleepCycle() + 1);
    }

    @FXML private void onSleepCycleDec() {
        Kaga.PROFILE.getGeneral().setSleepCycle(Kaga.PROFILE.getGeneral().getSleepCycle() - 1);
    }

    @FXML private void onSleepModifierInc() {
        Kaga.PROFILE.getGeneral().setSleepModifier(Kaga.PROFILE.getGeneral().getSleepModifier() + 1);
    }

    @FXML private void onSleepModifierDec() {
        Kaga.PROFILE.getGeneral().setSleepModifier(Kaga.PROFILE.getGeneral().getSleepModifier() - 1);
    }
}
