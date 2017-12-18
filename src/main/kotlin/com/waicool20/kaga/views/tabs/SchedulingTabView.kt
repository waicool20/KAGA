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
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.controlsfx.control.RangeSlider
import tornadofx.*
import java.time.LocalTime
import kotlin.math.roundToInt


class SchedulingTabView {
    @FXML private lateinit var enableSleepButton: CheckBox
    @FXML private lateinit var sleepRangeSlider: RangeSlider
    @FXML private lateinit var sleepTimeLabel: Label

    /* TODO Disabled temporarily till kcauto-kai is finalized
    @FXML private lateinit var enableAutoStopButton: CheckBox
    @FXML private lateinit var modeChoiceBox: ChoiceBox<KancolleAutoProfile.ScheduledStopMode>
    @FXML private lateinit var countSpinner: Spinner<Int>*/

    @FXML private lateinit var sleepContent: VBox
    /* TODO Disabled temporarily till kcauto-kai is finalized
    @FXML private lateinit var stopContent: GridPane*/

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }


    private fun setValues() {
        with(Kaga.PROFILE.scheduledSleep) {
            val sTime = String.format("%04d", startTime.toInt()).let { LocalTime.of(it.substring(0, 2).toInt(), it.substring(2, 4).toInt()) }
            val endTime = sTime.plusMinutes((length * 60).toLong())
            sleepRangeSlider.lowValue = sTime.hour + (sTime.minute / 60.0)
            sleepRangeSlider.highValue = endTime.hour + (endTime.minute / 60.0)
            updateSleepTime()
        }

        /* TODO Disabled temporarily till kcauto-kai is finalized
        modeChoiceBox.items.setAll(*KancolleAutoProfile.ScheduledStopMode.values())
        countSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE)*/
    }

    private fun createBindings() {
        enableSleepButton.selectedProperty().bindBidirectional(Kaga.PROFILE.scheduledSleep.enabledProperty)
        sleepRangeSlider.lowValueProperty().addListener { _ -> updateSleepTime() }
        sleepRangeSlider.highValueProperty().addListener { _ -> updateSleepTime() }
/*        with(Kaga.PROFILE.scheduledSleep) {
            enableSleepButton.bind(enabledProperty)
            val binding = Bindings.concat(startTimeHourSpinner.valueProperty().asString("%02d"),
                    startTimeMinSpinner.valueProperty().asString("%02d"))
            startTimeProperty.bind(binding)
            sleepLengthSpinner.bind(lengthProperty)
        }*/
        /* TODO Disabled temporarily till kcauto-kai is finalized
        with(Kaga.PROFILE.scheduledStop) {
            enableAutoStopButton.bind(enabledProperty)
            modeChoiceBox.bind(modeProperty)
            countSpinner.bind(countProperty)
        }*/
        sleepContent.disableProperty().bind(Bindings.not(enableSleepButton.selectedProperty()))
        /* TODO Disabled temporarily till kcauto-kai is finalized
        stopContent.disableProperty().bind(Bindings.not(enableAutoStopButton.selectedProperty()))*/
    }

    private fun updateSleepTime() {
        with(sleepRangeSlider) {
            val startHour = lowValue.toInt()
            val startMinute = ((lowValue - startHour) * 60).toInt()
            val sTime = formatTime(startHour, startMinute)

            val endHour = highValue.toInt()
            val endMinute = ((highValue - endHour) * 60).toInt()
            val endTime = formatTime(endHour, endMinute)

            sleepTimeLabel.text = "$sTime - $endTime"

            with(Kaga.PROFILE.scheduledSleep){
                startTime = sTime.replace(":", "")
                length = ((highValue - lowValue) * 100).roundToInt() / 100.0
            }
        }
    }

    private fun formatTime(hour: Int, minute: Int) = String.format("%02d:%02d", hour, minute)
}
