package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.StreamGobbler
import com.waicool20.kaga.views.tabs.ExpeditionsTabView
import com.waicool20.kaga.views.tabs.GeneralTabView
import com.waicool20.kaga.views.tabs.PvpTabView
import com.waicool20.kaga.views.tabs.SchedulingTabView
import com.waicool20.kaga.views.tabs.lbas.LbasTabView
import com.waicool20.kaga.views.tabs.quests.QuestsTabView
import com.waicool20.kaga.views.tabs.sortie.SortieTabView
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import tornadofx.bind
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern
import java.util.stream.Collectors

class KagaView {
    private var kancolleAutoProcess: Process? = null
    private var streamGobbler: StreamGobbler? = null

    private val runningText = "Kancolle Auto is running!"
    private val notRunningText = "Kancolle Auto is not running!"

    @FXML private lateinit var kagaStatus: Label
    @FXML private lateinit var startStopButton: Button
    @FXML private lateinit var profileNameComboBox: ComboBox<String>

    @FXML private lateinit var generalTabController: GeneralTabView
    @FXML private lateinit var schedulingTabController: SchedulingTabView
    @FXML private lateinit var expeditionsTabController: ExpeditionsTabView
    @FXML private lateinit var pvpTabController: PvpTabView
    @FXML private lateinit var sortieTabController: SortieTabView
    @FXML private lateinit var lbasTabController: LbasTabView
    @FXML private lateinit var questsTabController: QuestsTabView

    @FXML fun initialize() {
        profileNameComboBox.bind(Kaga.PROFILE!!.nameProperty)
    }

    @FXML private fun showProfiles() {
        val currentProfile = profileNameComboBox.value
        val profiles = Files.walk(Kaga.CONFIG_DIR)
                .filter({ path -> Files.isRegularFile(path) })
                .map({ path -> path.fileName.toString() })
                .map({ name ->
                    val matcher = Pattern.compile("(.+?)-config\\.ini").matcher(name)
                    if (matcher.matches()) matcher.group(1) else ""
                }).filter(String::isNotEmpty)
                .filter({ name -> name != currentProfile })
                .collect(Collectors.toList<String>())
        if (profiles.isNotEmpty()) {
            profileNameComboBox.items.setAll(profiles)
        }
    }

    @FXML private fun onSelectProfile() {
        val newProfile = profileNameComboBox.value
        val path = Paths.get(Kaga.CONFIG_DIR.toString(), "$newProfile-config.ini")
        if (Files.exists(path)) {
            val profile = KancolleAutoProfile.load(path)
            if (profile != null) {
                Kaga.PROFILE = profile
                Kaga.CONFIG.currentProfile = profile.name
                Kaga.CONFIG.save()
                this.initialize()
                generalTabController.initialize()
                schedulingTabController.initialize()
                expeditionsTabController.initialize()
                pvpTabController.initialize()
                sortieTabController.initialize()
                lbasTabController.initialize()
                questsTabController.initialize()
            }
        }
    }

    @FXML private fun onSaveButton() {
        Kaga.CONFIG.currentProfile = Kaga.PROFILE!!.name
        Kaga.CONFIG.save()
        Kaga.PROFILE!!.save()
        showStatus("Profile was saved!", 5)
    }

    @FXML private fun onDeleteButton() {
        Kaga.PROFILE!!.delete()
        profileNameComboBox.value = ""
        showStatus("Profile was deleted", 5)
    }

    @FXML private fun onStartStopButton() {
        if (kancolleAutoProcess == null || !kancolleAutoProcess!!.isAlive) {
            Kaga.PROFILE!!.save(Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "config.ini"))
            val args = listOf(
                    "java",
                    "-jar",
                    Kaga.CONFIG.sikuliScriptJarPath.toString(),
                    "-r",
                    Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "kancolle_auto.sikuli").toString()
            )
            Kaga.CONSOLE_STAGE.toFront()
            val processMonitor = Thread {
                kancolleAutoProcess = ProcessBuilder(args).start()
                streamGobbler = StreamGobbler(kancolleAutoProcess)
                Platform.runLater {
                    kagaStatus.text = runningText
                    startStopButton.text = "Stop"
                    startStopButton.style = "-fx-background-color: red"
                }
                streamGobbler?.run()
                kancolleAutoProcess?.waitFor()
                Platform.runLater {
                    kagaStatus.text = notRunningText
                    startStopButton.text = "Start"
                    startStopButton.style = "-fx-background-color: lightgreen"
                }
            }
            processMonitor.start()
        } else {
            kancolleAutoProcess?.destroy()
        }
    }

    @FXML private fun openConsole() = Kaga.CONSOLE_STAGE.show()

    @FXML private fun quit() = System.exit(0)

    private fun showStatus(status: String, seconds: Long) {
        Thread({
            Platform.runLater { kagaStatus.text = status }
            Thread.sleep(seconds * 1000)
            Platform.runLater { kagaStatus.text = if (kancolleAutoProcess?.isAlive ?: false) runningText else notRunningText }
        }).start()
    }
}
