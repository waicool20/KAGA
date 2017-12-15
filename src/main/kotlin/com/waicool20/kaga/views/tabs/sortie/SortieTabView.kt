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
import com.waicool20.kaga.config.KancolleAutoProfile
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
import kotlin.streams.toList


class SortieTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var engineComboBox: ComboBox<KancolleAutoProfile.Engine>
    @FXML private lateinit var mapComboBox: ComboBox<String>
    @FXML private lateinit var nodesSpinner: Spinner<Int>
    @FXML private lateinit var fleetModeComboBox: ComboBox<KancolleAutoProfile.FleetMode>


    @FXML private lateinit var retreatLimitComboBox: ComboBox<Int>
    @FXML private lateinit var repairLimitComboBox: ComboBox<Int>
    @FXML private lateinit var reserveDocksCheckBox: CheckBox
    @FXML private lateinit var repairTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var repairTimeMinSpinner: Spinner<Int>
    @FXML private lateinit var checkFatigueCheckBox: CheckBox
    @FXML private lateinit var checkPortCheckBox: CheckBox
    @FXML private lateinit var medalStopCheckBox: CheckBox

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
            "6-1", "6-2", "6-3", "6-4", "6-5",
            "-- Event --",
            "E-1", "E-2", "E-3", "E-4",
            "E-5", "E-6", "E-7", "E-8"
    )

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        val engineConverter = object: StringConverter<KancolleAutoProfile.Engine>() {
            override fun toString(engine: KancolleAutoProfile.Engine?) = engine?.prettyString ?: ""
            override fun fromString(string: String?) = KancolleAutoProfile.Engine.fromPrettyString(string ?: "")
        }
        engineComboBox.converter = engineConverter
        engineComboBox.items.setAll(KancolleAutoProfile.Engine.values().toList())

        mapComboBox.cellFactory = NoneSelectableCellFactory("--.+?--".toRegex())
        mapComboBox.items.setAll(maps)

        nodesSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12)

        val fleetModeConverter = object: StringConverter<KancolleAutoProfile.FleetMode>() {
            override fun toString(fleetMode: KancolleAutoProfile.FleetMode?) = fleetMode?.prettyString ?: ""
            override fun fromString(string: String?) = KancolleAutoProfile.FleetMode.fromPrettyString(string ?: "")
        }
        fleetModeComboBox.converter = fleetModeConverter
        fleetModeComboBox.items.setAll(KancolleAutoProfile.FleetMode.values().toList())


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
        with(String.format("%04d", Kaga.PROFILE.sortie.repairTimeLimit.toInt())) {
            repairTimeHourSpinner.valueFactory.value = substring(0, 2).toInt()
            repairTimeMinSpinner.valueFactory.value = substring(2, 4).toInt()
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE.sortie) {
            enableButton.bind(enabledProperty)
            engineComboBox.bind(engineProperty)
            mapComboBox.bind(mapProperty)
            nodesSpinner.bind(nodesProperty)
            fleetModeComboBox.bind(fleetModeProperty)

            retreatLimitComboBox.bind(retreatLimitProperty)
            reserveDocksCheckBox.bind(reserveDocksProperty)
            repairLimitComboBox.bind(repairLimitProperty)
            val binding = Bindings.concat(repairTimeHourSpinner.valueProperty().asString("%02d"),
                    repairTimeMinSpinner.valueProperty().asString("%02d"))
            repairTimeLimitProperty.bind(binding)
            checkFatigueCheckBox.bind(checkFatigueProperty)
            checkPortCheckBox.bind(portCheckProperty)
            medalStopCheckBox.bind(medalStopProperty)
        }
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
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
}
