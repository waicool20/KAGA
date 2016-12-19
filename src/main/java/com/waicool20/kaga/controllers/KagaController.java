package com.waicool20.kaga.controllers;

import com.waicool20.kaga.Kaga;
import com.waicool20.kaga.config.KancolleAutoProfile;
import com.waicool20.kaga.util.StreamGobbler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KagaController {

    private Process kancolleAutoProcess;
    private StreamGobbler streamGobbler;

    @FXML private Label kagaStatus;
    @FXML private Button startStopButton;
    @FXML private ComboBox<String> profileNameComboBox;

    @FXML private GeneralTabController generalTabController;
    @FXML private SchedulingTabController schedulingTabController;
    @FXML private ExpeditionsTabController expeditionsTabController;
    @FXML private PvpTabController pvpTabController;
    @FXML private SortieTabController sortieTabController;
    @FXML private LbasTabController lbasTabController;
    @FXML private QuestsTabController questsTabController;

    @FXML private void initialize() {
        profileNameComboBox.valueProperty().bindBidirectional(Kaga.PROFILE.nameProperty());
    }

    @FXML private void showProfiles() {
        try {
            String currentProfile = profileNameComboBox.getValue();
            List<String> profiles = Files.walk(Kaga.CONFIG_DIR)
                .filter(path -> Files.isRegularFile(path))
                .map(path -> path.getFileName().toString())
                .map(name -> {
                    Matcher matcher = Pattern.compile("(.+?)-config\\.ini").matcher(name);
                    return matcher.matches() ? matcher.group(1) : "";
                })
                .filter(name -> !name.isEmpty())
                .filter(name -> !name.equals(currentProfile))
                .collect(Collectors.toList());
            if (!profiles.isEmpty()) {
                profileNameComboBox.getItems().setAll(profiles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onSelectProfile() {
        String newProfile = profileNameComboBox.getValue();
        if (newProfile != null) {
            Path path = Paths.get(Kaga.CONFIG_DIR.toString(), newProfile + "-config.ini");
            try {
                if (Files.exists(path)) {
                    Kaga.PROFILE = KancolleAutoProfile.load(path);
                    Kaga.CONFIG.setCurrentProfile(Kaga.PROFILE.getName());
                    Kaga.CONFIG.save();
                    this.initialize();
                    generalTabController.initialize();
                    schedulingTabController.initialize();
                    expeditionsTabController.initialize();
                    pvpTabController.initialize();
                    sortieTabController.initialize();
                    lbasTabController.initialize();
                    questsTabController.initialize();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void onSaveButton() {
        try {
            Kaga.CONFIG.setCurrentProfile(Kaga.PROFILE.getName());
            Kaga.CONFIG.save();
            Kaga.PROFILE.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onDeleteButton() {
        try {
            Kaga.PROFILE.delete();
            profileNameComboBox.setValue("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onStartStopButton() {
        try {
            if (kancolleAutoProcess == null || !kancolleAutoProcess.isAlive()) {
                Kaga.PROFILE.save(Paths.get(Kaga.CONFIG.getKancolleAutoRootDirPath().toString(), "config.ini"));
                List<String> args = new LinkedList<>();
                args.add("java");
                args.add("-jar");
                args.add(Kaga.CONFIG.getSikuliScriptJarPath().toString());
                args.add("-r");
                args.add(Paths.get(Kaga.CONFIG.getKancolleAutoRootDirPath().toString(), "kancolle_auto.sikuli").toString());

                kancolleAutoProcess = new ProcessBuilder(args)
                    .start();
                streamGobbler = new StreamGobbler(kancolleAutoProcess);
                streamGobbler.run();
                kagaStatus.setText("Kancolle Auto is running!");
                startStopButton.setText("Stop");
                startStopButton.setStyle("-fx-background-color: red");
            } else {
                kancolleAutoProcess.destroy();
                kagaStatus.setText("Kancolle Auto is not running!");
                startStopButton.setText("Start");
                startStopButton.setStyle("-fx-background-color: lightgreen");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void openConsole() {
        Kaga.CONSOLE_STAGE.show();
    }

    @FXML private void quit() {
        System.exit(0);
    }
}
