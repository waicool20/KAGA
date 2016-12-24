package com.waicool20.kaga;

import com.waicool20.kaga.config.KagaConfig;
import com.waicool20.kaga.config.KancolleAutoProfile;
import com.waicool20.kaga.handlers.KeyboardIncrementHandler;
import com.waicool20.kaga.handlers.MouseIncrementHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Kaga extends Application {

    public static Path JAR_DIR;
    public static Path CONFIG_DIR;
    public static Path CONFIG_FILE;
    public static Stage ROOT_STAGE;
    public static Stage CONSOLE_STAGE;

    public static KagaConfig CONFIG;
    public static KancolleAutoProfile PROFILE;

    public static void main(String[] args) throws Exception {
        JAR_DIR = Paths.get(Kaga.class.getProtectionDomain().getCodeSource().getLocation().toURI())
            .getParent();
        CONFIG_DIR = Paths.get(JAR_DIR.toString(), "kaga");
        CONFIG_FILE = Paths.get(CONFIG_DIR.toString(), "kaga.ini");
        Application.launch(args);
    }

    public static void showPathChooser() throws IOException {
        Parent root =
            FXMLLoader.load(Kaga.class.getClassLoader().getResource("views/path-chooser.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Configure KAGA paths...");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void startMainApplication() throws IOException {
        String currentProfile = CONFIG.getCurrentProfile();
        PROFILE = KancolleAutoProfile
            .load(Paths.get(CONFIG_DIR.toString(), currentProfile + "-config.ini"));
        if (PROFILE == null) {
            PROFILE = KancolleAutoProfile
                .load(Paths.get(CONFIG.getKancolleAutoRootDirPath().toString(), "config.ini"));
        }
        if (PROFILE != null) {
            Kaga.CONFIG.setCurrentProfile(Kaga.PROFILE.getName());
            Kaga.CONFIG.save();
        }
        Parent root = FXMLLoader.load(Kaga.class.getClassLoader().getResource("views/kaga.fxml"));
        Scene scene = new Scene(root);
        ROOT_STAGE.setTitle("KAGA - Kancolle Auto GUI App");
        ROOT_STAGE.setScene(scene);
        ROOT_STAGE.show();
        ROOT_STAGE.setMinWidth(ROOT_STAGE.getWidth() + 25);
        ROOT_STAGE.setMinHeight(ROOT_STAGE.getHeight() + 25);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new KeyboardIncrementHandler());
        MouseIncrementHandler handler = new MouseIncrementHandler(1000L, 40);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, handler);
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, handler);

        setupConsole();
    }

    private static void setupConsole() throws IOException {
        Parent consoleWindow =
            FXMLLoader.load(Kaga.class.getClassLoader().getResource("views/console.fxml"));
        Scene consoleScene = new Scene(consoleWindow, 600, 800);
        CONSOLE_STAGE = new Stage();
        CONSOLE_STAGE.initModality(Modality.WINDOW_MODAL);
        CONSOLE_STAGE.setTitle("KAGA Debug");
        CONSOLE_STAGE.setMinHeight(300);
        CONSOLE_STAGE.setMinWidth(600);
        CONSOLE_STAGE.setScene(consoleScene);
    }

    @Override public void start(Stage stage) throws Exception {
        ROOT_STAGE = stage;
        initialize();
    }

    private void initialize() throws IOException {
        if (Files.notExists(CONFIG_DIR)) {
            Files.createDirectory(CONFIG_DIR);
        }
        CONFIG = KagaConfig.load(CONFIG_FILE);
        if (CONFIG.isValid()) {
            startMainApplication();
        } else {
            showPathChooser();
        }
    }
}
