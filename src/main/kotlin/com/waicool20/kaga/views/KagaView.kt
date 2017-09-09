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
import com.waicool20.kaga.util.AlertFactory
import com.waicool20.kaga.util.setSideWithHorizontalText
import com.waicool20.kaga.views.tabs.ExpeditionsTabView
import com.waicool20.kaga.views.tabs.GeneralTabView
import com.waicool20.kaga.views.tabs.PvpTabView
import com.waicool20.kaga.views.tabs.SchedulingTabView
import com.waicool20.kaga.views.tabs.misc.MiscTabView
import com.waicool20.kaga.views.tabs.quests.QuestsTabView
import com.waicool20.kaga.views.tabs.sortie.SortieTabView
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.TabPane
import javafx.scene.layout.HBox
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import tornadofx.*
import java.awt.Desktop
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.streams.toList


class KagaView {
    private val runningText = "Kancolle Auto is running!"
    private val notRunningText = "Kancolle Auto is not running!"

    @FXML private lateinit var kagaStatus: Label
    @FXML private lateinit var startStopButton: SplitMenuButton
    @FXML private lateinit var profileNameComboBox: ComboBox<String>
    @FXML private lateinit var profileSelectionHBox: HBox
    @FXML private lateinit var tabpane: TabPane

    @FXML private lateinit var generalTabController: GeneralTabView
    @FXML private lateinit var schedulingTabController: SchedulingTabView
    @FXML private lateinit var expeditionsTabController: ExpeditionsTabView
    @FXML private lateinit var pvpTabController: PvpTabView
    @FXML private lateinit var sortieTabController: SortieTabView
    @FXML private lateinit var miscTabController: MiscTabView
    @FXML private lateinit var questsTabController: QuestsTabView

    private val logger = LoggerFactory.getLogger(javaClass)

    @FXML fun initialize() {
        Kaga.ROOT_STAGE.addEventHandler(WindowEvent.WINDOW_HIDDEN, { Kaga.KANCOLLE_AUTO.stop() })
        tabpane.setSideWithHorizontalText(Side.LEFT)
        createBindings()
        checkStartStopButton()
    }

    private fun createBindings() {
        profileNameComboBox.bind(Kaga.PROFILE.nameProperty)
    }

    @FXML private fun showProfiles() {
        val currentProfile = profileNameComboBox.value
        val profiles = Files.walk(Kaga.CONFIG_DIR)
                .filter { Files.isRegularFile(it) }
                .map { it.fileName.toString() }
                .map {
                    "(.+?)-config\\.ini".toRegex().matchEntire(it)?.groupValues?.get(1) ?: ""
                }.filter(String::isNotEmpty)
                .filter { it != currentProfile }
                .toList().sorted()
        if (profiles.isNotEmpty()) {
            profileNameComboBox.items.setAll(profiles)
        }
    }

    @FXML private fun onSelectProfile() {
        val newProfile = profileNameComboBox.value
        val path = Kaga.CONFIG_DIR.resolve("$newProfile-config.ini")
        if (Files.exists(path)) {
            try {
                val profile = KancolleAutoProfile.load(path)
                Kaga.PROFILE = profile
                Kaga.CONFIG.currentProfile = profile.name
                Kaga.CONFIG.save()
                createBindings()
                generalTabController.initialize()
                schedulingTabController.initialize()
                expeditionsTabController.initialize()
                pvpTabController.initialize()
                sortieTabController.initialize()
                miscTabController.initialize()
                questsTabController.initialize()
                AlertFactory.info(
                        content = "Profile ${profile.name} has been loaded!"
                ).showAndWait()
            } catch (e: Exception) {
                val warning = "Failed to parse profile $newProfile, reason: ${e.message}"
                logger.error(warning)
                AlertFactory.error(
                        content = warning
                ).showAndWait()
            }
        }
    }

    @FXML private fun onSaveButton() = Kaga.PROFILE.run {
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

    @FXML private fun onDeleteButton() = Kaga.PROFILE.run {
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

    @FXML private fun onStartStopButton() {
        if (!Kaga.KANCOLLE_AUTO.isRunning()) {
            startKancolleAuto()
        } else {
            Kaga.KANCOLLE_AUTO.stop()
        }
    }

    @FXML private fun startWithoutWritingConfig() {
        if (!Kaga.KANCOLLE_AUTO.isRunning()) startKancolleAuto(false)
    }

    @FXML private fun stopAtPort() {
        if (Kaga.KANCOLLE_AUTO.isRunning()) Kaga.KANCOLLE_AUTO.stopAtPort()
    }

    private fun startKancolleAuto(saveConfig: Boolean = true) {
        thread {
            Platform.runLater {
                kagaStatus.text = runningText
                startStopButton.text = "Stop"
                checkStartStopButton()
                profileSelectionHBox.isDisable = true
            }
            Kaga.KANCOLLE_AUTO.startAndWait(saveConfig)
            Platform.runLater {
                kagaStatus.text = notRunningText
                startStopButton.text = "Start"
                checkStartStopButton()
                profileSelectionHBox.isDisable = false
            }
        }
        Kaga.CONSOLE_STAGE.toFront()
        Kaga.STATS_STAGE.toFront()
    }

    private fun checkStartStopButton() {
        startStopButton.apply {
            val color = if (text == "Start") "green" else "red"
            stylesheets.clear()
            stylesheets.add("styles/${color}style.css")
            items.partition { it.id.contains(text, true) }.let {
                it.first.forEach { it.isVisible = true }
                it.second.forEach { it.isVisible = false }
            }
        }
    }

    @FXML private fun clearCrashLogs() {
        var count = 0
        Files.walk(Kaga.CONFIG.kancolleAutoRootDirPath.resolve("crashes"))
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".log") }
                .peek { count++ }
                .forEach { Files.delete(it) }
        logger.info("$count crash logs have been deleted!")
        AlertFactory.info(
                content = "$count crash logs have been deleted!"
        ).showAndWait()
    }

    @FXML private fun openLatestCrashLog() {
        val log = Files.walk(Kaga.CONFIG.kancolleAutoRootDirPath.resolve("crashes"), 1)
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

    @FXML private fun openHowto() {
        AlertFactory.info(
                title = "KAGA - How do I use KAGA?",
                content = "Try pressing the Shift button while hovering over the label of an option to show a tooltip"
        ).showAndWait()
    }

    @FXML private fun openRepo() {
        if (Desktop.isDesktopSupported()) {
            thread { Desktop.getDesktop().browse(URI("https://github.com/waicool20/KAGA")) }
            Kaga.ROOT_STAGE.toBack()
        }
    }

    @FXML private fun openAbout() {
        AlertFactory.info(
                title = "KAGA - About",
                content = """
                        Kancolle Auto GUI App by waicool20

                        Version: ${Kaga.VERSION_INFO.version}
                        Kancolle-Auto Compatibility: ${Kaga.VERSION_INFO.kcAutoCompatibility}
                        """.trimIndent()
        ).showAndWait()
    }

    @FXML private fun openConsole() = Kaga.CONSOLE_STAGE.show()

    @FXML private fun openStats() = Kaga.STATS_STAGE.show()

    @FXML private fun quit() = Kaga.exit()
}
