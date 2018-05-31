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

package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KagaConfig
import com.waicool20.kaga.kcauto.YuuBot
import com.waicool20.util.javafx.AlertFactory
import com.waicool20.util.javafx.bind
import com.waicool20.util.javafx.listen
import com.waicool20.util.javafx.listenDebounced
import javafx.fxml.FXML
import javafx.scene.control.*
import tornadofx.*
import java.awt.Desktop
import java.net.URI
import kotlin.concurrent.thread

class PreferencesTabView {
    @FXML private lateinit var preventLockCheckBox: CheckBox
    @FXML private lateinit var clearConsoleCheckBox: CheckBox
    @FXML private lateinit var restartSessionCheckBox: CheckBox
    @FXML private lateinit var maxRetriesSpinner: Spinner<Int>
    @FXML private lateinit var debugModeEnableCheckBox: CheckBox
    @FXML private lateinit var showDebugCheckBox: CheckBox
    @FXML private lateinit var showStatsCheckBox: CheckBox
    @FXML private lateinit var checkForUpdatesCheckBox: CheckBox
    @FXML private lateinit var apiKeyTextField: TextField
    @FXML private lateinit var startStopShortcutTextField: TextField

    private val borderStyle = "-fx-border-width: 2px"

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    fun setValues() {
        with(Kaga.CONFIG) {
            maxRetriesSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Int.MAX_VALUE)
            startStopShortcutTextField.textFormatter = TextFormatter<String> { it.apply { text = text.toUpperCase() } }
            apiKeyTextField.text = apiKey
        }
    }

    fun createBindings() {
        with(Kaga.CONFIG) {
            preventLockCheckBox.selectedProperty().bindBidirectional(preventLockProperty)
            clearConsoleCheckBox.selectedProperty().bindBidirectional(clearConsoleOnStartProperty)
            restartSessionCheckBox.selectedProperty().bindBidirectional(autoRestartOnKCAutoCrashProperty)
            maxRetriesSpinner.bind(autoRestartMaxRetriesProperty)
            debugModeEnableCheckBox.selectedProperty().bindBidirectional(debugModeEnabledProperty)
            showDebugCheckBox.selectedProperty().bindBidirectional(showDebugOnStartProperty)
            showStatsCheckBox.selectedProperty().bindBidirectional(showStatsOnStartProperty)
            checkForUpdatesCheckBox.selectedProperty().bindBidirectional(checkForUpdatesProperty)
            startStopShortcutTextField.bind(startStopScriptShortcutProperty)

            apiKeyTextField.textProperty().run {
                listen { apiKeyTextField.style = "-fx-border-color: yellow;$borderStyle" }
                listenDebounced(1000, "ApiKeyTextFieldProperty") { newVal ->
                    testApiKey(newVal)
                }
            }
        }
    }

    fun testApiKey(apiKey: String = Kaga.CONFIG.apiKey) {
        apiKeyTextField.style = "-fx-border-color: yellow;$borderStyle"
        YuuBot.testApiKey(apiKey) { status ->
            Kaga.CONFIG.apiKey = when (status) {
                YuuBot.ApiKeyStatus.VALID -> {
                    apiKeyTextField.style = "-fx-border-color: lightgreen;$borderStyle"
                    apiKey
                }
                YuuBot.ApiKeyStatus.INVALID -> {
                    apiKeyTextField.style = "-fx-border-color: red;$borderStyle"
                    ""
                }
                YuuBot.ApiKeyStatus.UNKNOWN -> apiKey
            }
        }
    }

    @FXML
    private fun onSaveButton() {
        Kaga.CONFIG.save()
        AlertFactory.info(content = "Preferences were saved!").showAndWait()
    }

    @FXML
    private fun onResetButton() {
        Kaga.CONFIG = KagaConfig.load()
        initialize()
        AlertFactory.info(content = "Preferences were reset!").showAndWait()
    }

    @FXML
    private fun openDiscordLink() {
        if (Desktop.isDesktopSupported()) {
            thread { Desktop.getDesktop().browse(URI("https://discord.gg/2tt5Der")) }
            Kaga.ROOT_STAGE.toBack()
        }
    }
}
