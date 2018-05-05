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

package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.handlers.GlobalShortcutHandler
import com.waicool20.kaga.util.*
import com.waicool20.kaga.views.tabs.*
import com.waicool20.kaga.views.tabs.shipswitcher.ShipSwitcherTabView
import com.waicool20.kaga.views.tabs.sortie.SortieTabView
import javafx.animation.PauseTransition
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.stage.WindowEvent
import javafx.util.Duration
import org.controlsfx.glyphfont.Glyph
import org.slf4j.LoggerFactory
import tornadofx.*
import java.awt.Desktop
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.streams.toList


class KagaView {
    @FXML private lateinit var kagaStatus: Label
    @FXML private lateinit var startStopButton: SplitMenuButton
    @FXML private lateinit var saveButton: Button
    @FXML private lateinit var deleteButton: Button
    @FXML private lateinit var pauseButton: ToggleButton
    @FXML private lateinit var profileNameComboBox: ComboBox<String>
    @FXML private lateinit var profileSelectionHBox: HBox
    @FXML private lateinit var tabpane: TabPane

    @FXML private lateinit var generalTabController: GeneralTabView
    @FXML private lateinit var schedulingTabController: SchedulingTabView
    @FXML private lateinit var expeditionsTabController: ExpeditionsTabView
    @FXML private lateinit var pvpTabController: PvpTabView
    @FXML private lateinit var sortieTabController: SortieTabView
    @FXML private lateinit var lbasTabController: LbasTabView
    @FXML private lateinit var shipSwitcherTabController: ShipSwitcherTabView
    @FXML private lateinit var questsTabController: QuestsTabView
    @FXML private lateinit var preferencesTabController: PreferencesTabView

    private val logger = LoggerFactory.getLogger(javaClass)

    @FXML
    fun initialize() {
        Kaga.ROOT_STAGE.addEventHandler(WindowEvent.WINDOW_HIDDEN, { Kaga.KCAUTO_KAI.stop() })
        tabpane.setSideWithHorizontalText(Side.LEFT)
        createBindings()
        checkStartStopButton()
        preferencesTabController.testApiKey()
        registerShortcuts()
        saveButton.graphic = Glyph("FontAwesome", "SAVE")
        deleteButton.graphic = Glyph("FontAwesome", "TRASH")
        pauseButton.graphic = Glyph("FontAwesome", "PAUSE")
    }

    private val canSwitch = AtomicBoolean(true)

    private fun registerShortcuts() {
        with(Kaga.CONFIG) {
            val pause = PauseTransition(Duration.seconds(1.5))
            val listener: (String) -> Unit = { shortcut ->
                if (shortcut.length > 1) {
                    GlobalShortcutHandler.registerShortcut("StartStopScript", shortcut, ::onStartStopButton)
                } else {
                    GlobalShortcutHandler.deregisterShortcut("StartStopScript")
                }
            }
            listener(startStopScriptShortcut)
            startStopScriptShortcutProperty.addListener("StartStopScriptShortcutProperty") { newVal ->
                pause.setOnFinished { listener(newVal) }
                pause.playFromStart()
            }
        }

        val profileListener: (Boolean) -> Unit = { switchDown ->
            if (!Kaga.KCAUTO_KAI.isRunning() && canSwitch.get()) {
                runLater {
                    canSwitch.set(false)
                    updateProfileItems()
                    profileNameComboBox.apply {
                        val list = (items + value).sorted()
                        val index = if (switchDown) {
                            (list.indexOf(value) + 1).takeIf { it < list.size } ?: 0
                        } else {
                            (list.indexOf(value) - 1).takeIf { it >= 0 } ?: list.size-1
                        }
                        value = list[index]
                    }
                    thread {
                        TimeUnit.MILLISECONDS.sleep(300)
                        canSwitch.set(true)
                    }
                }
            }
        }
        GlobalShortcutHandler.registerShortcut("ProfileDown", "CTRL+SHIFT+DOWN") {
            profileListener(true)
        }
        GlobalShortcutHandler.registerShortcut("ProfileUp", "CTRL+SHIFT+UP") {
            profileListener(false)
        }
    }

    private fun createBindings() {
        profileNameComboBox.bind(Kaga.PROFILE.nameProperty)
        pauseButton.selectedProperty().bindBidirectional(Kaga.PROFILE.general.pauseProperty)
        pauseButton.selectedProperty().addListener("PauseButtonListener") { newVal ->
            if (newVal) {
                logger.info("Script will be paused on the next cycle.")
            } else {
                logger.info("Script will resume shortly.")
            }
        }
    }

    @FXML
    private fun updateProfileItems() {
        val currentProfile = profileNameComboBox.value
        val profiles = Files.walk(Kaga.CONFIG_DIR).toList()
                .filter { Files.isRegularFile(it) }
                .map { it.fileName.toString() }
                .mapNotNull {
                    Regex("(.+?)-config\\.ini").matchEntire(it)?.groupValues?.get(1)
                }
                .filter { it != currentProfile }
                .sorted()
        if (profiles.isNotEmpty()) {
            profileNameComboBox.items.setAll(profiles)
        }
    }

    @FXML
    private fun onSelectProfile() {
        val newProfile = profileNameComboBox.value
        val path = Kaga.CONFIG_DIR.resolve("$newProfile-config.ini")
                .takeIf { Files.exists(it) } ?: return
        thread {
            try {
                val profile = KancolleAutoProfile.load(path)
                Kaga.PROFILE = profile
                Kaga.CONFIG.currentProfile = profile.name
                Kaga.CONFIG.save()
                runLater {
                    createBindings()
                    generalTabController.initialize()
                    schedulingTabController.initialize()
                    expeditionsTabController.initialize()
                    pvpTabController.initialize()
                    sortieTabController.initialize()
                    lbasTabController.initialize()
                    questsTabController.initialize()
                    shipSwitcherTabController.initialize()
                    Tooltip("Profile ${profile.name} has been loaded!").apply {
                        fadeAfter(700)
                        showAt(profileNameComboBox, TooltipSide.TOP_LEFT)
                    }
                }
            } catch (e: Exception) {
                val warning = "Failed to parse profile $newProfile, reason: ${e.message}"
                logger.error(warning)
                runLater {
                    Tooltip("XX $warning").apply {
                        fadeAfter(5000)
                        showAt(profileNameComboBox, TooltipSide.TOP_LEFT)
                    }
                }
            }
        }
    }

    @FXML
    private fun onSaveButton() = Kaga.PROFILE.run {
        if (name == KancolleAutoProfile.DEFAULT_NAME) {
            val text = "Not a valid profile name, didn't save it..."
            logger.warn(text)
            AlertFactory.info(content = text).showAndWait()
            return@run
        }
        Kaga.CONFIG.currentProfile = name
        Kaga.CONFIG.save()
        save()
        AlertFactory.info(content = "Profile $name was saved!").showAndWait()
    }

    @FXML
    private fun onDeleteButton() = Kaga.PROFILE.run {
        val warning = "Not a valid profile name, didn't delete it..."
        if (name == KancolleAutoProfile.DEFAULT_NAME) {
            logger.warn(warning)
            AlertFactory.warn(content = warning).showAndWait()
            return@run
        }
        val toDelete = name
        if (delete()) {
            profileNameComboBox.value = ""
            AlertFactory.info(content = "Profile $toDelete was deleted").showAndWait()
        } else {
            AlertFactory.warn(content = warning).showAndWait()
        }
    }

    @FXML
    private fun onStartStopButton() {
        if (!Kaga.KCAUTO_KAI.isRunning()) {
            startKancolleAuto()
        } else {
            Kaga.KCAUTO_KAI.stop()
            pauseButton.isDisable = true
            pauseButton.isSelected = false
        }
    }

    @FXML
    private fun startWithoutWritingConfig() {
        if (!Kaga.KCAUTO_KAI.isRunning()) startKancolleAuto(false)
    }

    @FXML
    private fun stopAtPort() {
        if (Kaga.KCAUTO_KAI.isRunning()) Kaga.KCAUTO_KAI.stopAtPort()
    }

    private fun startKancolleAuto(saveConfig: Boolean = true) {
        thread {
            runLater {
                kagaStatus.text = "KCAuto-Kai is running!"
                startStopButton.text = "Stop"
                pauseButton.isDisable = false
                checkStartStopButton()
                profileSelectionHBox.isDisable = true
            }
            Kaga.KCAUTO_KAI.startAndWait(saveConfig)
            runLater {
                kagaStatus.text = "KCAuto-Kai is not running!"
                startStopButton.text = "Start"
                pauseButton.isDisable = true
                pauseButton.isSelected = false
                checkStartStopButton()
                profileSelectionHBox.isDisable = false
            }
        }
        runLater {
            Kaga.CONSOLE_STAGE.toFront()
            Kaga.STATS_STAGE.toFront()
        }
    }

    private fun checkStartStopButton() {
        startStopButton.apply {
            val color = if (text == "Start") "green" else "red"
            styleClass.removeAll { it.endsWith("-split-menu") }
            styleClass.add("$color-split-menu")
            items.partition { it.id.contains(text, true) }.let {
                it.first.forEach { it.isVisible = true }
                it.second.forEach { it.isVisible = false }
            }
        }
    }

    @FXML
    private fun clearCrashLogs() {
        var count = 0
        Files.walk(Kaga.CONFIG.kcaKaiRootDirPath.resolve("crashes"))
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".log") }
                .peek { count++ }
                .forEach { Files.delete(it) }
        logger.info("$count crash logs have been deleted!")
        AlertFactory.info(
                content = "$count crash logs have been deleted!"
        ).showAndWait()
    }

    @FXML
    private fun openLatestCrashLog() {
        val log = Files.walk(Kaga.CONFIG.kcaKaiRootDirPath.resolve("crashes"), 1)
                .filter { Files.isRegularFile(it) }
                .filter { it.fileName.toString().endsWith(".log") }
                .map(Path::toFile)
                .sorted().toList()
                .lastOrNull()
        if (log == null) {
            AlertFactory.warn(
                    content = "No crash logs were found!"
            ).showAndWait()
        } else {
            if (Desktop.isDesktopSupported()) {
                thread { Desktop.getDesktop().open(log) }
                Kaga.ROOT_STAGE.toBack()
            }
        }
    }

    @FXML
    private fun openHowto() {
        AlertFactory.info(
                title = "KAGA - How do I use KAGA?",
                content = "Try pressing the Shift button while hovering over the label of an option to show a tooltip"
        ).showAndWait()
    }

    @FXML
    private fun openRepo() {
        if (Desktop.isDesktopSupported()) {
            thread { Desktop.getDesktop().browse(URI("https://github.com/waicool20/KAGA")) }
            Kaga.ROOT_STAGE.toBack()
        }
    }

    @FXML
    private fun checkForUpdates() = Kaga.checkForUpdates(true)

    private val helpText by lazy { Kaga::class.java.classLoader.getResourceAsStream("help.txt").bufferedReader().readText() }

    @FXML
    private fun openAbout() {
        AlertFactory.info(
                title = "KAGA - About",
                content = helpText.replace("<KAGA_VERSION>", Kaga.VERSION_INFO.version)
                        .replace("<KCAUTO_KAI_COMPAT>", Kaga.VERSION_INFO.kcAutoCompatibility)
                        .replace("<KCAUTO_KAI_VERSION>", Kaga.KCAUTO_KAI.version)
        ).showAndWait()
    }

    @FXML
    private fun openConsole() = Kaga.CONSOLE_STAGE.show()

    @FXML
    private fun openStats() = Kaga.STATS_STAGE.show()

    @FXML
    private fun quit() = Kaga.exit()
}
