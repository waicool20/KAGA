package com.waicool20.kaga.config

import com.waicool20.kaga.Kaga
import org.ini4j.Wini
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile

class KagaConfig(var currentProfile: String, var sikuliScriptJarPath: Path, var kancolleAutoRootDirPath: Path) {

    companion object Loader {
        @JvmStatic fun load(path: Path): KagaConfig {
            if (Files.notExists(path)) {
                Files.createDirectories(path.parent)
                Files.createFile(path)
            }
            val kaga = Wini(path.toFile())["Kaga"] ?: return KagaConfig("", Paths.get(""), Paths.get(""))
            return KagaConfig(kaga["currentProfile"] ?: "",
                    Paths.get(kaga["sikuliScriptJarPath"] ?: ""),
                    Paths.get(kaga["kancolleAutoRootDirPath"] ?: ""))
        }
    }

    fun sikuliScriptJarIsValid(): Boolean {
        if (Files.exists(sikuliScriptJarPath) && Files.isRegularFile(sikuliScriptJarPath)) {
            val manifest = JarFile(sikuliScriptJarPath.toFile()).manifest
            return manifest.mainAttributes.getValue("Main-Class") == "org.sikuli.basics.SikuliScript"
        }
        return false
    }

    fun kancolleAutoRootDirPathIsValid(): Boolean =
            Files.exists(Paths.get(kancolleAutoRootDirPath.toString(), "kancolle_auto.sikuli"))

    fun isValid(): Boolean =
            sikuliScriptJarIsValid() && kancolleAutoRootDirPathIsValid()

    fun save() {
        val ini = Wini(Kaga.CONFIG_FILE.toFile())
        ini.put("Kaga", "currentProfile", currentProfile)
        ini.put("Kaga", "sikuliScriptJarPath", sikuliScriptJarPath)
        ini.put("Kaga", "kancolleAutoRootDirPath", kancolleAutoRootDirPath)
        ini.store()
    }
}


