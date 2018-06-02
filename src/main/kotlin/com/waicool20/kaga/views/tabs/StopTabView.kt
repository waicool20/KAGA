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
import com.waicool20.kaga.config.KancolleAutoProfile.ScheduledStopMode
import com.waicool20.waicoolutils.javafx.addListener
import com.waicool20.waicoolutils.javafx.asTimeSpinner
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.HBox
import javafx.util.StringConverter
import tornadofx.*
import java.util.concurrent.TimeUnit

class StopTabView {
    @FXML private lateinit var enableScriptStopCheckBox: CheckBox
    @FXML private lateinit var scriptStopCountSpinner: Spinner<Int>
    @FXML private lateinit var scriptStopTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var scriptStopTimeMinSpinner: Spinner<Int>

    @FXML private lateinit var enableExpStopCheckBox: CheckBox
    @FXML private lateinit var expStopModeComboBox: ComboBox<ScheduledStopMode>
    @FXML private lateinit var expStopCountSpinner: Spinner<Int>
    @FXML private lateinit var expStopTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var expStopTimeMinSpinner: Spinner<Int>

    @FXML private lateinit var enableSortieStopCheckBox: CheckBox
    @FXML private lateinit var sortieStopModeComboBox: ComboBox<ScheduledStopMode>
    @FXML private lateinit var sortieStopCountSpinner: Spinner<Int>
    @FXML private lateinit var sortieStopTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var sortieStopTimeMinSpinner: Spinner<Int>

    @FXML private lateinit var scriptStopContent: HBox
    @FXML private lateinit var expStopContent: HBox
    @FXML private lateinit var sortieStopContent: HBox

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private class StopTimeContainer(
            val stopTimeProperty: StringProperty,
            val hourSpinner: Spinner<Int>,
            val minSpinner: Spinner<Int>
    ) {
        init {
            stopTimeProperty.value.toIntOrNull()?.let { String.format("%04d", it) }?.apply {
                hourSpinner.valueFactory.value = substring(0, 2).toInt()
                minSpinner.valueFactory.value = substring(2, 4).toInt()
            } ?: run {
                hourSpinner.valueFactory.value = -1
                minSpinner.valueFactory.value = -1
            }

            hourSpinner.valueProperty().addListener("${hourSpinner.id}_HourListener") { _ -> updateTime() }
            minSpinner.valueProperty().addListener("${minSpinner.id}_MinListener") { _ -> updateTime() }
        }

        private fun updateTime() {
            val time = hourSpinner.valueProperty().asString("%02d").value +
                    minSpinner.valueProperty().asString("%02d").value
            stopTimeProperty.value = if (time.contains("-")) "" else time
        }
    }

    private fun setValues() {
        val stopModeConverter = object : StringConverter<ScheduledStopMode>() {
            override fun toString(mode: ScheduledStopMode) = mode.prettyString
            override fun fromString(string: String) = ScheduledStopMode.fromPrettyString(string)
        }
        expStopModeComboBox.items.setAll(ScheduledStopMode.values().toList())
        expStopModeComboBox.converter = stopModeConverter
        sortieStopModeComboBox.items.setAll(ScheduledStopMode.values().toList())
        sortieStopModeComboBox.converter = stopModeConverter

        scriptStopCountSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999)
        expStopCountSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999)
        sortieStopCountSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999)

        scriptStopTimeHourSpinner.asTimeSpinner(TimeUnit.HOURS, allowInvalid = true)
        scriptStopTimeMinSpinner.asTimeSpinner(TimeUnit.MINUTES, allowInvalid = true)
        expStopTimeHourSpinner.asTimeSpinner(TimeUnit.HOURS, allowInvalid = true)
        expStopTimeMinSpinner.asTimeSpinner(TimeUnit.MINUTES, allowInvalid = true)
        sortieStopTimeHourSpinner.asTimeSpinner(TimeUnit.HOURS, allowInvalid = true)
        sortieStopTimeMinSpinner.asTimeSpinner(TimeUnit.MINUTES, allowInvalid = true)

        Kaga.PROFILE.scheduledStop.apply {
            scriptStopCountSpinner.valueFactory.value = scriptStopCount.toIntOrNull() ?: -1
            expStopCountSpinner.valueFactory.value = expStopCount.toIntOrNull() ?: -1
            sortieStopCountSpinner.valueFactory.value = sortieStopCount.toIntOrNull() ?: -1

            StopTimeContainer(scriptStopTimeProperty, scriptStopTimeHourSpinner, scriptStopTimeMinSpinner)
            StopTimeContainer(expStopTimeProperty, expStopTimeHourSpinner, expStopTimeMinSpinner)
            StopTimeContainer(sortieStopTimeProperty, sortieStopTimeHourSpinner, sortieStopTimeMinSpinner)
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE.scheduledStop) {
            enableScriptStopCheckBox.bind(scriptStopEnabledProperty)
            enableExpStopCheckBox.bind(expStopEnabledProperty)
            enableSortieStopCheckBox.bind(sortieStopEnabledProperty)

            expStopModeComboBox.bind(expStopModeProperty)
            sortieStopModeComboBox.bind(sortieStopModeProperty)

            fun Spinner<Int>.listenCount(name: String, property: StringProperty) {
                valueProperty().addListener("${name}StopCountListener") { newVal ->
                    property.value = if (newVal > 0) "$newVal" else ""
                }
            }

            scriptStopCountSpinner.listenCount("Script", scriptStopCountProperty)
            expStopCountSpinner.listenCount("Exp", expStopCountProperty)
            sortieStopCountSpinner.listenCount("Sortie", sortieStopCountProperty)
        }

        scriptStopContent.disableProperty().bind(enableScriptStopCheckBox.selectedProperty().not())
        expStopContent.disableProperty().bind(enableExpStopCheckBox.selectedProperty().not())
        expStopModeComboBox.disableProperty().bind(enableExpStopCheckBox.selectedProperty().not())
        sortieStopContent.disableProperty().bind(enableSortieStopCheckBox.selectedProperty().not())
        sortieStopModeComboBox.disableProperty().bind(enableSortieStopCheckBox.selectedProperty().not())
    }
}
