package com.waicool20.kaga.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.waicool20.kaga.Kaga
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile

@JsonIgnoreProperties("valid")
data class KagaConfig(var currentProfile: String = "",
                 var sikuliScriptJarPath: Path = Paths.get(""),
                 var kancolleAutoRootDirPath: Path = Paths.get(""),
                 var preventLock: Boolean = false) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object Loader {
        private val loaderLogger = LoggerFactory.getLogger(KagaConfig.Loader::class.java)
        @JvmStatic fun load(path: Path): KagaConfig {
            loaderLogger.info("Attempting to load KAGA configuration")
            loaderLogger.debug("Loading KAGA configuration from $path")
            if (Files.notExists(path)) {
                loaderLogger.debug("Configuration not found, creating file \"${Kaga.CONFIG_FILE}\"")
                Files.createDirectories(path.parent)
                Files.createFile(path)
            }
            try {
                with (ObjectMapper().readValue(Kaga.CONFIG_FILE.toFile(), KagaConfig::class.java)) {
                    loaderLogger.info("Loading KAGA configuration was successful")
                    loaderLogger.debug("Loaded $this")
                    return this
                }
            } catch (e: JsonMappingException) {
                loaderLogger.warn("No valid Json configuration was found, error: ${e.message}")
            }
            loaderLogger.info("Using a default configuration")
            return KagaConfig()
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
        logger.info("Saving KAGA configuration file")
        logger.debug("Saved $this to ${Kaga.CONFIG_FILE}")
        ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(Kaga.CONFIG_FILE.toFile(), this)
        logger.info("Saving KAGA configuration was successful")
    }
}


