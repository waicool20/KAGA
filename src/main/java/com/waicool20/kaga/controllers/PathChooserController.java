package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class PathChooserController {

    @FXML private Label pathChooserFlavorText;

    @FXML private TextField sikuliScriptJarPathTextField;

    @FXML private TextField kancolleAutoDirTextField;

    @FXML private Button saveButton;

    @FXML private Label pathErrorsText;

    @FXML
    public void initialize() {
        checkErrors();
        pathChooserFlavorText.setText("Hello there Admiral! "
            + "This might be your first time starting up this application "
            + "or there was a problem finding the files below! "
            + "Either way before you begin your adventures, "
            + "please configure the paths first!");
    }

    public void openSikuliScriptJarChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Path to Sikuli Script Jar File...");
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Kaga.CONFIG.setSikuliScriptJarPath(file.toPath());
            sikuliScriptJarPathTextField.setText(file.getPath());
            checkErrors();
        }
    }

    public void openKancolleAutoRootChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Path to Kancolle Auto root directory...");
        File directory = directoryChooser.showDialog(null);
        if (directory != null) {
            Kaga.CONFIG.setKancolleAutoRootDirPath(directory.toPath());
            kancolleAutoDirTextField.setText(directory.getPath());
            checkErrors();
        }
    }

    public void onSaveButtonPressed() throws IOException {
        Kaga.CONFIG.save();
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
        Kaga.startMainApplication();
    }

    private void checkErrors() {
        sikuliScriptJarPathTextField.setStyle("-fx-border-color:" + (!Kaga.CONFIG.sikuliScriptJarIsValid() ? "red" : "inherit"));
        kancolleAutoDirTextField.setStyle("-fx-border-color:" + (!Kaga.CONFIG.kancolleAutoRootDirPathIsValid() ? "red" : "inherit"));
        setErrorText();
    }

    private void setErrorText() {
        String errors = "";
        if (sikuliScriptJarPathTextField.getStyle().contains("red")) {
            errors += "Invalid Sikuli Jar File!\n";
        }
        if (kancolleAutoDirTextField.getStyle().contains("red")) {
            errors += "Invalid Kancolle Auto directory!\n";
        }
        pathErrorsText.setText(errors);
    }
}
