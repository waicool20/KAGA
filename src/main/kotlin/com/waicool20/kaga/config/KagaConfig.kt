/*
 * GPLv3 License
 *
 *  Copyright (c) KAGA by waicool20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.waicool20.kaga.config

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.listen
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.slf4j.LoggerFactory
import tornadofx.*
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
                 autoRestartMaxRetries: Int = 10,
                 debugModeEnabled: Boolean = true,
                 showDebugOnStart: Boolean = true,
                 showStatsOnStart: Boolean = true,
                 checkForUpdates: Boolean = true) {
    @JsonIgnore val currentProfileProperty = SimpleStringProperty(currentProfile)
    @JsonIgnore val sikulixJarPathProperty = SimpleObjectProperty<Path>(sikulixJarPath)
    @JsonIgnore val kancolleAutoRootDirPathProperty = SimpleObjectProperty<Path>(kancolleAutoRootDirPath)
    @JsonIgnore val preventLockProperty = SimpleBooleanProperty(preventLock)
    @JsonIgnore val clearConsoleOnStartProperty = SimpleBooleanProperty(clearConsoleOnStart)
    @JsonIgnore val autoRestartOnKCAutoCrashProperty = SimpleBooleanProperty(autoRestartOnKCAutoCrash)
    @JsonIgnore val autoRestartMaxRetriesProperty = SimpleIntegerProperty(autoRestartMaxRetries)
    @JsonIgnore val debugModeEnabledProperty = SimpleBooleanProperty(debugModeEnabled)
    @JsonIgnore val showDebugOnStartProperty = SimpleBooleanProperty(showDebugOnStart)
    @JsonIgnore val showStatsOnStartProperty = SimpleBooleanProperty(showStatsOnStart)
    @JsonIgnore val checkForUpdatesProperty = SimpleBooleanProperty(checkForUpdates)

    @get:JsonProperty var currentProfile by currentProfileProperty
    @get:JsonProperty var sikulixJarPath by sikulixJarPathProperty
    @get:JsonProperty var kancolleAutoRootDirPath by kancolleAutoRootDirPathProperty
    @get:JsonProperty var preventLock by preventLockProperty
    @get:JsonProperty var clearConsoleOnStart by clearConsoleOnStartProperty
    @get:JsonProperty var autoRestartOnKCAutoCrash by autoRestartOnKCAutoCrashProperty
    @get:JsonProperty var autoRestartMaxRetries by autoRestartMaxRetriesProperty
    @get:JsonProperty var debugModeEnabled by debugModeEnabledProperty
    @get:JsonProperty var showDebugOnStart by showDebugOnStartProperty
    @get:JsonProperty var showStatsOnStart by showStatsOnStartProperty
    @get:JsonProperty var checkForUpdates by checkForUpdatesProperty

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        debugModeEnabledProperty.listen {
            Kaga.setLogLevel(Level.toLevel(logLevel()))
        }
    }

    companion object Loader {
        private val loaderLogger = LoggerFactory.getLogger(KagaConfig.Loader::class.java)
        val CONFIG_FILE: Path = Kaga.CONFIG_DIR.resolve("kaga.json")
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
            Files.exists(kancolleAutoRootDirPath.resolve("kancolle_auto.sikuli"))

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


