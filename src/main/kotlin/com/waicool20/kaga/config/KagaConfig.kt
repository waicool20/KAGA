package com.waicool20.kaga.config

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.waicool20.kaga.Kaga
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.slf4j.LoggerFactory
import tornadofx.getValue
import tornadofx.setValue
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile

@JsonIgnoreProperties(ignoreUnknown = true)
class KagaConfig(currentProfile: String = "",
                 sikulixJarPath: Path = Paths.get(""),
                 kancolleAutoRootDirPath: Path = Paths.get(""),
                 preventLock: Boolean = false,
                 clearConsoleOnStart: Boolean = true,
                 autoRestartOnKCAutoCrash: Boolean = true,
                 debugModeEnabled: Boolean = true) {
    @JsonIgnore val currentProfileProperty = SimpleStringProperty(currentProfile)
    @JsonIgnore val sikulixJarPathProperty = SimpleObjectProperty<Path>(sikulixJarPath)
    @JsonIgnore val kancolleAutoRootDirPathProperty = SimpleObjectProperty<Path>(kancolleAutoRootDirPath)
    @JsonIgnore val preventLockProperty = SimpleBooleanProperty(preventLock)
    @JsonIgnore val clearConsoleOnStartProperty = SimpleBooleanProperty(clearConsoleOnStart)
    @JsonIgnore val autoRestartOnKCAutoCrashProperty = SimpleBooleanProperty(autoRestartOnKCAutoCrash)
    @JsonIgnore val debugModeEnabledProperty = SimpleBooleanProperty(debugModeEnabled)

    @get:JsonProperty var currentProfile by currentProfileProperty
    @get:JsonProperty var sikulixJarPath by sikulixJarPathProperty
    @get:JsonProperty var kancolleAutoRootDirPath by kancolleAutoRootDirPathProperty
    @get:JsonProperty var preventLock by preventLockProperty
    @get:JsonProperty var clearConsoleOnStart by clearConsoleOnStartProperty
    @get:JsonProperty var autoRestartOnKCAutoCrash by autoRestartOnKCAutoCrashProperty
    @get:JsonProperty var debugModeEnabled by debugModeEnabledProperty

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        debugModeEnabledProperty.addListener { obs, oldVal, newVal ->
            Kaga.setLogLevel(Level.toLevel(logLevel()))
        }
    }

    companion object Loader {
        private val loaderLogger = LoggerFactory.getLogger(KagaConfig.Loader::class.java)
        val CONFIG_FILE: Path = Paths.get(Kaga.CONFIG_DIR.toString(), "kaga.json")
        @JvmStatic fun load(): KagaConfig {
            loaderLogger.info("Attempting to load KAGA configuration")
            loaderLogger.debug("Loading KAGA configuration from $CONFIG_FILE")
            if (Files.notExists(CONFIG_FILE)) {
                loaderLogger.debug("Configuration not found, creating file \"$CONFIG_FILE\"")
                Files.createDirectories(CONFIG_FILE.parent)
                Files.createFile(CONFIG_FILE)
            }
            try {
                with(ObjectMapper().readValue(CONFIG_FILE.toFile(), KagaConfig::class.java)) {
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

    fun sikulixJarIsValid(): Boolean {
        if (Files.exists(sikulixJarPath) && Files.isRegularFile(sikulixJarPath)) {
            val manifest = JarFile(sikulixJarPath.toFile()).manifest
            return manifest.mainAttributes.getValue("Main-Class") == "org.sikuli.ide.Sikulix"
        }
        return false
    }

    fun kancolleAutoRootDirPathIsValid(): Boolean =
            Files.exists(Paths.get(kancolleAutoRootDirPath.toString(), "kancolle_auto.sikuli"))

    @JsonIgnore fun isValid(): Boolean =
            sikulixJarIsValid() && kancolleAutoRootDirPathIsValid()

    fun logLevel() = if (debugModeEnabled) "DEBUG" else "INFO"

    fun save() {
        logger.info("Saving KAGA configuration file")
        logger.debug("Saved $this to ${Loader.CONFIG_FILE}")
        ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(Loader.CONFIG_FILE.toFile(), this)
        logger.info("Saving KAGA configuration was successful")
    }

    override fun toString(): String = ObjectMapper().writeValueAsString(this)
}


