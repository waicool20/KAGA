package com.waicool20.kaga.config;

import com.waicool20.kaga.Kaga;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class KagaConfig {

    private String currentProfile;
    private Path sikuliScriptJarPath;
    private Path kancolleAutoRootDirPath;

    public KagaConfig(String currentProfile, Path sikuliScriptJarPath,
        Path kancolleAutoRootDirPath) {
        this.currentProfile = currentProfile;
        this.sikuliScriptJarPath = sikuliScriptJarPath;
        this.kancolleAutoRootDirPath = kancolleAutoRootDirPath;
    }

    public String getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(String profile) {
        this.currentProfile = profile;
    }

    public Path getSikuliScriptJarPath() {
        return sikuliScriptJarPath;
    }

    public void setSikuliScriptJarPath(Path sikuliScriptJarPath) {
        this.sikuliScriptJarPath = sikuliScriptJarPath;
    }

    public Path getKancolleAutoRootDirPath() {
        return kancolleAutoRootDirPath;
    }

    public void setKancolleAutoRootDirPath(Path kancolleAutoRootDirPath) {
        this.kancolleAutoRootDirPath = kancolleAutoRootDirPath;
    }

    public boolean sikuliScriptJarIsValid() {
        if (Files.exists(sikuliScriptJarPath) && Files.isRegularFile(sikuliScriptJarPath)) {
            try {
                Manifest manifest = new JarFile(sikuliScriptJarPath.toFile()).getManifest();
                return manifest.getMainAttributes().getValue("Main-Class")
                    .equals("org.sikuli.basics.SikuliScript");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean kancolleAutoRootDirPathIsValid() {
        return Files.exists(Paths.get(kancolleAutoRootDirPath.toString(), "kancolle_auto.sikuli"));
    }

    public boolean isValid() {
        return sikuliScriptJarIsValid() && kancolleAutoRootDirPathIsValid();
    }

    public static KagaConfig load(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        Ini.Section kaga = new Wini(path.toFile()).get("Kaga");
        if (kaga == null) {
            return new KagaConfig("", Paths.get(""), Paths.get(""));
        }
        return new KagaConfig(
            IniUtils.getString(kaga, "currentProfile", ""),
            Paths.get(IniUtils.getString(kaga, "sikuliScriptJarPath", "")),
            Paths.get(IniUtils.getString(kaga, "kancolleAutoRootDirPath", "")));
    }

    public void save() throws IOException {
        Wini ini = new Wini(Kaga.CONFIG_FILE.toFile());
        ini.put("Kaga", "currentProfile", currentProfile);
        ini.put("Kaga", "sikuliScriptJarPath", sikuliScriptJarPath);
        ini.put("Kaga", "kancolleAutoRootDirPath", kancolleAutoRootDirPath);
        ini.store();
    }
}
