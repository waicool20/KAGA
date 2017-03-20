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
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.layout.HBox
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import tornadofx.*
import java.awt.Desktop
import java.net.URI
import java.nio.file.Files
import java.util.regex.Pattern
import java.util.stream.Collectors


class KagaView {
    private val runningText = "Kancolle Auto is running!"
    private val notRunningText = "Kancolle Auto is not running!"

    @FXML private lateinit var kagaStatus: Label
    @FXML private lateinit var startStopButton: Button
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
    }

    private fun createBindings() {
        profileNameComboBox.bind(Kaga.PROFILE!!.nameProperty)
    }

    @FXML private fun showProfiles() {
        val currentProfile = profileNameComboBox.value
        val profiles = Files.walk(Kaga.CONFIG_DIR)
                .filter { Files.isRegularFile(it) }
                .map { it.fileName.toString() }
                .map {
                    val matcher = Pattern.compile("(.+?)-config\\.ini").matcher(it)
                    if (matcher.matches()) matcher.group(1) else ""
                }.filter(String::isNotEmpty)
                .filter { it != currentProfile }
                .collect(Collectors.toList<String>()).sorted()
        if (profiles.isNotEmpty()) {
            profileNameComboBox.items.setAll(profiles)
        }
    }

    @FXML private fun onSelectProfile() {
        val newProfile = profileNameComboBox.value
        val path = Kaga.CONFIG_DIR.resolve("$newProfile-config.ini")
        if (Files.exists(path)) {
            val profile = KancolleAutoProfile.load(path)
            if (profile != null) {
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
            }
        }
    }

    @FXML private fun onSaveButton() {
        with(Kaga.PROFILE!!) {
            if (name == "<Current Profile>") {
                val text = "Not a valid profile name, didn't save it..."
                logger.warn(text)
                AlertFactory.info(content = text).showAndWait()
                return
            }
            Kaga.CONFIG.currentProfile = name
            Kaga.CONFIG.save()
            save()
            AlertFactory.info(content = "Profile $name was saved!").showAndWait()
        }
    }

    @FXML private fun onDeleteButton() {
        with(Kaga.PROFILE!!) {
            val text = "Not a valid profile name, didn't delete it..."
            if (name == "<Current Profile>") {
                logger.warn(text)
                AlertFactory.warn(content = text).showAndWait()
                return
            }
            val toDelete = name
            if (delete()) {
                profileNameComboBox.value = ""
                AlertFactory.info(content = "Profile $toDelete was deleted").showAndWait()
            } else {
                AlertFactory.warn(content = text).showAndWait()
            }
        }
    }

    @FXML private fun onStartStopButton() {
        if (!Kaga.KANCOLLE_AUTO.isRunning()) {
            Thread {
                Platform.runLater {
                    kagaStatus.text = runningText
                    startStopButton.text = "Stop"
                    startStopButton.style = "-fx-background-color: red"
                    profileSelectionHBox.isDisable = true
                }
                Kaga.KANCOLLE_AUTO.startAndWait()
                Platform.runLater {
                    kagaStatus.text = notRunningText
                    startStopButton.text = "Start"
                    startStopButton.style = "-fx-background-color: lightgreen"
                    profileSelectionHBox.isDisable = false
                }
            }.start()
            Kaga.CONSOLE_STAGE.toFront()
            Kaga.STATS_STAGE.toFront()
        } else {
            Kaga.KANCOLLE_AUTO.stop()
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

    @FXML private fun openHowto() {
        AlertFactory.info(
                title = "KAGA - How do I use KAGA?",
                content = "Try pressing the Shift button while hovering over the label of an option to show a tooltip"
        ).showAndWait()
    }

    @FXML private fun openRepo() {
        if (Desktop.isDesktopSupported()) {
            Thread({
                Desktop.getDesktop().browse(URI("https://github.com/waicool20/KAGA"))
            }).start()
            Kaga.ROOT_STAGE.toBack()
        }
    }

    @FXML private fun openConsole() = Kaga.CONSOLE_STAGE.show()

    @FXML private fun openStats() = Kaga.STATS_STAGE.show()

    @FXML private fun quit() = Kaga.exit()
}
