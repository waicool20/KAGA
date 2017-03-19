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

package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.*
import javafx.beans.binding.Bindings
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.StringConverter
import org.slf4j.LoggerFactory
import tornadofx.*
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


class SortieTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var eventCheckBox: CheckBox
    @FXML private lateinit var fleetCompComboBox: ComboBox<Int>
    @FXML private lateinit var areaComboBox: ComboBox<String>
    @FXML private lateinit var combinedFleetCheckBox: CheckBox
    @FXML private lateinit var nodesSpinner: Spinner<Int>
    @FXML private lateinit var retreatLimitComboBox: ComboBox<Int>
    @FXML private lateinit var repairLimitComboBox: ComboBox<Int>
    @FXML private lateinit var repairTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var repairTimeMinSpinner: Spinner<Int>
    @FXML private lateinit var checkFatigueCheckBox: CheckBox
    @FXML private lateinit var checkPortCheckBox: CheckBox
    @FXML private lateinit var medalStopCheckBox: CheckBox
    @FXML private lateinit var lastNodePushCheckBox: CheckBox

    @FXML private lateinit var content: GridPane

    private val logger = LoggerFactory.getLogger(javaClass)

    private val maps = setOf(
            "-- World 1 --",
            "1-1", "1-2", "1-3", "1-4", "1-5", "1-6",
            "-- World 2 --",
            "2-1", "2-2", "2-3", "2-4", "2-5",
            "-- World 3 --",
            "3-1", "3-2", "3-3", "3-4", "3-5",
            "-- World 4 --",
            "4-1", "4-2", "4-3", "4-4", "4-5",
            "-- World 5 --",
            "5-1", "5-2", "5-3", "5-4", "5-5",
            "-- World 6 --",
            "6-1", "6-2", "6-3", "6-4", "6-5"
    )

    private val eventMaps = setOf(
            "-- Page 1 --",
            "1-1", "1-2",
            "-- Page 2 --",
            "2-1", "2-2",
            "-- Page 3 --",
            "3-1", "3-2",
            "-- Page 4 --",
            "4-1", "4-2",
            "-- Page 5 --",
            "5-1", "5-2",
            "-- Page 6 --",
            "6-1", "6-2",
            "-- Page 7 --",
            "7-1", "7-2",
            "-- Page 8 --",
            "8-1", "8-2")

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private val eventCheckBoxListener = ChangeListener<Boolean> { obs, oldVal, newVal ->
        setAreaItems(newVal)
        areaComboBox.value = areaComboBox.items.find { !it.matches("--.+?--".toRegex()) }
        setProfileArea(areaComboBox.selectionModel.selectedItem)
    }

    private fun setValues() {
        eventCheckBox.selectedProperty().removeListener(eventCheckBoxListener)
        fleetCompComboBox.items.setAll((1..5).toList())
        areaComboBox.cellFactory = NoneSelectableCellFactory("--.+?--".toRegex())
        with(Kaga.PROFILE!!.sortie) {
            if (area == "E") {
                setAreaItems(true)
                eventCheckBox.isSelected = true
                areaComboBox.value = subarea
            } else {
                setAreaItems(false)
                eventCheckBox.isSelected = false
                areaComboBox.value = "$area-$subarea"
            }
        }
        nodesSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE)
        val damageLevels = listOf("Light damage", "Moderate damage", "Critical damage", "Null")
        val damageConverter = object : StringConverter<Int>() {
            override fun toString(int: Int?): String = damageLevels[int ?: 3]

            override fun fromString(string: String?): Int = damageLevels.indexOf(string)
        }
        retreatLimitComboBox.items.setAll((0..2).toList())
        repairLimitComboBox.items.setAll((0..2).toList())
        retreatLimitComboBox.converter = damageConverter
        repairLimitComboBox.converter = damageConverter

        repairTimeHourSpinner.asTimeSpinner(TimeUnit.HOURS)
        repairTimeMinSpinner.asTimeSpinner(TimeUnit.MINUTES)
        repairTimeMinSpinner.updateOtherSpinnerOnWrap(repairTimeHourSpinner, 0, 59)
        with(String.format("%04d", Kaga.PROFILE!!.sortie.repairTimeLimit.toInt())) {
            repairTimeHourSpinner.valueFactory.value = this.substring(0, 2).toInt()
            repairTimeMinSpinner.valueFactory.value = this.substring(2, 4).toInt()
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.sortie) {
            enableButton.bind(enabledProperty)
            fleetCompComboBox.bind(fleetCompProperty)
            areaComboBox.valueProperty().addListener { obsVal, oldVal, newVal ->
                setProfileArea(newVal)
            }
            eventCheckBox.selectedProperty().addListener(eventCheckBoxListener)
            combinedFleetCheckBox.bind(combinedFleetProperty)
            nodesSpinner.bind(nodesProperty)
            retreatLimitComboBox.bind(retreatLimitProperty)
            repairLimitComboBox.bind(repairLimitProperty)
            val binding = Bindings.concat(repairTimeHourSpinner.valueProperty().asString("%02d"),
                    repairTimeMinSpinner.valueProperty().asString("%02d"))
            repairTimeLimitProperty.bind(binding)
            checkFatigueCheckBox.bind(checkFatigueProperty)
            checkPortCheckBox.bind(portCheckProperty)
            medalStopCheckBox.bind(medalStopProperty)
            lastNodePushCheckBox.bind(lastNodePushProperty)
        }
        combinedFleetCheckBox.visibleProperty().bind(eventCheckBox.selectedProperty())
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
        eventCheckBox.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }

    @FXML private fun onConfigureNodeSelectsButton() {
        val loader = FXMLLoader(Kaga::class.java.classLoader.getResource("views/single-list.fxml"))
        loader.setController(NodeSelectsChooserView())
        val scene = Scene(loader.load())
        with(Stage()) {
            this.scene = scene
            title = "KAGA - Node Selects Configuration"
            initOwner(Kaga.ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            show()
            minHeight = height + 25
            minWidth = width + 25
        }
    }

    @FXML private fun onConfigureFormationsButton() =
            find(FormationChooserView::class).openModal(owner = Kaga.ROOT_STAGE.owner)

    @FXML private fun onConfigureNightBattlesButton() =
            find(NightBattlesChooserView::class).openModal(owner = Kaga.ROOT_STAGE.owner)

    private fun setProfileArea(map: String?) {
        if (map == null) return
        with(Kaga.PROFILE!!.sortie) {
            if (eventCheckBox.isSelected) {
                area = "E"
                subarea = map
            } else {
                area = map[0].toString()
                subarea = map[2].toString()
            }
        }
    }

    private fun setAreaItems(isEvent: Boolean) {
        if (isEvent) {
            try {
                val lastEventMap = Files.walk(Kaga.CONFIG.kancolleAutoRootDirPath.resolve("kancolle_auto.sikuli/combat.sikuli"), 1)
                        .map { it.fileName.toString() }
                        .filter { it.startsWith("_event_panel_") }
                        .map { it.replace("_event_panel_", "").replace(".png", "") }
                        .sorted().collect(Collectors.toList<String>()).last()
                areaComboBox.items.setAll(eventMaps.take(eventMaps.indexOfFirst { it == lastEventMap } + 1))
            } catch (e: NoSuchElementException) {
                areaComboBox.items.setAll()
                logger.warn("No event maps were found during scan. Please check/update your Kancolle Auto installation.")
                AlertFactory.warn(
                        content = "No event maps were found. Is an Kancolle Auto up to date? Is an event really going on?"
                ).showAndWait()
            }
        } else {
            areaComboBox.items.setAll(maps)
        }
    }
}
