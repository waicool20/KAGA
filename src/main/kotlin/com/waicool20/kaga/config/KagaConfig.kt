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
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.readValue
import com.waicool20.kaga.Kaga
import com.waicool20.waicoolutils.javafx.json.fxJacksonObjectMapper
import com.waicool20.waicoolutils.javafx.listen
import com.waicool20.waicoolutils.logging.LoggerUtils
import org.slf4j.LoggerFactory
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile

@JsonIgnoreProperties(ignoreUnknown = true)
class KagaConfig(currentProfile: String = "",
                 sikulixJarPath: Path = Paths.get(""),
                 kcaRootDirPath: Path = Paths.get(""),
                 preventLock: Boolean = false,
                 clearConsoleOnStart: Boolean = true,
                 autoRestartOnKCAutoCrash: Boolean = true,
                 autoRestartMaxRetries: Int = 10,
                 debugModeEnabled: Boolean = true,
                 showDebugOnStart: Boolean = true,
                 showStatsOnStart: Boolean = true,
                 checkForUpdates: Boolean = true,
                 apiKey: String = "",
                 startStopScriptShortcut: String = "CTRL+SHIFT+ENTER"
) {
    val currentProfileProperty = currentProfile.toProperty()
    val sikulixJarPathProperty = sikulixJarPath.toProperty()
    val kcaRootDirPathProperty = kcaRootDirPath.toProperty()
    val preventLockProperty = preventLock.toProperty()
    val clearConsoleOnStartProperty = clearConsoleOnStart.toProperty()
    val autoRestartOnKCAutoCrashProperty = autoRestartOnKCAutoCrash.toProperty()
    val autoRestartMaxRetriesProperty = autoRestartMaxRetries.toProperty()
    val debugModeEnabledProperty = debugModeEnabled.toProperty()
    val showDebugOnStartProperty = showDebugOnStart.toProperty()
    val showStatsOnStartProperty = showStatsOnStart.toProperty()
    val checkForUpdatesProperty = checkForUpdates.toProperty()
    val apiKeyProperty = apiKey.toProperty()
    val startStopScriptShortcutProperty = startStopScriptShortcut.toProperty()

    var currentProfile by currentProfileProperty
    var sikulixJarPath by sikulixJarPathProperty
    var kcaRootDirPath by kcaRootDirPathProperty
    var preventLock by preventLockProperty
    var clearConsoleOnStart by clearConsoleOnStartProperty
    var autoRestartOnKCAutoCrash by autoRestartOnKCAutoCrashProperty
    var autoRestartMaxRetries by autoRestartMaxRetriesProperty
    var debugModeEnabled by debugModeEnabledProperty
    var showDebugOnStart by showDebugOnStartProperty
    var showStatsOnStart by showStatsOnStartProperty
    var checkForUpdates by checkForUpdatesProperty
    var apiKey by apiKeyProperty
    var startStopScriptShortcut by startStopScriptShortcutProperty

    private val logger = LoggerFactory.getLogger(javaClass)


    init {
        debugModeEnabledProperty.listen {
            LoggerUtils.setLogLevel(Level.toLevel(logLevel()))
        }
    }

    companion object Loader {
        private val mapper = fxJacksonObjectMapper()
        private val loaderLogger = LoggerFactory.getLogger(KagaConfig.Loader::class.java)
        val CONFIG_FILE: Path = Kaga.CONFIG_DIR.resolve("kaga.json")
        @JvmStatic
        fun load(): KagaConfig {
            loaderLogger.info("Attempting to load KAGA configuration")
            loaderLogger.debug("Loading KAGA configuration from $CONFIG_FILE")
            if (Files.notExists(CONFIG_FILE)) {
                loaderLogger.debug("Configuration not found, creating file \"$CONFIG_FILE\"")
                Files.createDirectories(CONFIG_FILE.parent)
                Files.createFile(CONFIG_FILE)
            }
            try {
                with(mapper.readValue<KagaConfig>(CONFIG_FILE.toFile())) {
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
            Files.exists(kcaRootDirPath.resolve("kcauto.sikuli"))

    @JsonIgnore
    fun isValid(): Boolean =
            sikulixJarIsValid() && kancolleAutoRootDirPathIsValid()

    fun logLevel() = if (debugModeEnabled) "DEBUG" else "INFO"

    fun save() {
        logger.info("Saving KAGA configuration file")
        logger.debug("Saved $this to $CONFIG_FILE")
        mapper.writerWithDefaultPrettyPrinter().writeValue(CONFIG_FILE.toFile(), this)
        logger.info("Saving KAGA configuration was successful")
    }

    override fun toString(): String = mapper.writeValueAsString(this)
}


