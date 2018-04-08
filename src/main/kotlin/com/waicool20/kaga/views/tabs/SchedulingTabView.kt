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
import com.waicool20.kaga.util.addListener
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.controlsfx.control.RangeSlider
import tornadofx.*
import java.time.LocalTime
import kotlin.math.roundToInt


class SchedulingTabView {
    @FXML private lateinit var enableSleepCheckBox: CheckBox
    @FXML private lateinit var invertSleepCheckBox: CheckBox
    @FXML private lateinit var sleepRangeSlider: RangeSlider
    @FXML private lateinit var sleepTimeLabel: Label

    @FXML private lateinit var enableExpSleepCheckBox: CheckBox
    @FXML private lateinit var invertExpSleepCheckBox: CheckBox
    @FXML private lateinit var expSleepRangeSlider: RangeSlider
    @FXML private lateinit var expSleepTimeLabel: Label

    @FXML private lateinit var enableSortieSleepCheckBox: CheckBox
    @FXML private lateinit var invertSortieSleepCheckBox: CheckBox
    @FXML private lateinit var sortieSleepRangeSlider: RangeSlider
    @FXML private lateinit var sortieSleepTimeLabel: Label

    @FXML private lateinit var sleepContent: VBox
    @FXML private lateinit var expSleepContent: VBox
    @FXML private lateinit var sortieSleepContent: VBox

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private class SleepContainer(
            val slider: RangeSlider,
            val label: Label,
            val invertProperty: BooleanProperty,
            val startTime: StringProperty,
            val length: DoubleProperty
    ) {
        init {
            val sTime = startTime.get().padStart(4, '0').let {
                LocalTime.of(it.substring(0, 2).toInt(), it.substring(2, 4).toInt())
            }
            val eTime = sTime.plusMinutes((length.get() * 60).toLong())

            if (sTime < eTime) {
                invertProperty.set(false)
                slider.highValue = eTime.hour + (eTime.minute / 60.0)
                slider.lowValue = sTime.hour + (sTime.minute / 60.0)
            } else {
                invertProperty.set(true)
                slider.highValue = sTime.hour + (sTime.minute / 60.0)
                slider.lowValue = eTime.hour + (eTime.minute / 60.0)
            }

            updateTime()
            slider.lowValueProperty().addListener("${slider.id}_LOW") { _ -> updateTime() }
            slider.highValueProperty().addListener("${slider.id}_HIGH") { _ -> updateTime() }
            invertProperty.addListener("${slider.id}_invert") { _ -> updateTime() }
        }

        fun updateTime() {
            if (invertProperty.get()) {
                slider.styleClass.add("inverted-range-slider")
            } else {
                slider.styleClass.removeAll { it.contains("inverted") }
            }
            with(slider) {
                val startHour = lowValue.toInt()
                val startMinute = ((lowValue - startHour) * 60).toInt()
                val sTime = formatTime(startHour, startMinute)

                val endHour = highValue.toInt()
                val endMinute = ((highValue - endHour) * 60).toInt()
                val eTime = formatTime(endHour, endMinute)

                label.text = "$sTime - $eTime"

                val sleepLength = ((highValue - lowValue) * 100).roundToInt() / 100.0
                if (invertProperty.get()) {
                    startTime.set(eTime.replace(":", ""))
                    length.set(24 - sleepLength)
                } else {
                    startTime.set(sTime.replace(":", ""))
                    length.set(sleepLength)
                }
            }
        }

        private fun formatTime(hour: Int, minute: Int) = String.format("%02d:%02d", hour, minute)
    }

    private fun setValues() {
        with(Kaga.PROFILE.scheduledSleep) {
            SleepContainer(
                    sleepRangeSlider,
                    sleepTimeLabel,
                    invertSleepCheckBox.selectedProperty(),
                    sleepStartTimeProperty,
                    sleepLengthProperty
            )
            SleepContainer(
                    expSleepRangeSlider,
                    expSleepTimeLabel,
                    invertExpSleepCheckBox.selectedProperty(),
                    expSleepStartTimeProperty,
                    expSleepLengthProperty
            )
            SleepContainer(
                    sortieSleepRangeSlider,
                    sortieSleepTimeLabel,
                    invertSortieSleepCheckBox.selectedProperty(),
                    sortieSleepStartTimeProperty,
                    sortieSleepLengthProperty
            )
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE.scheduledSleep) {
            enableSleepCheckBox.bind(sleepEnabledProperty)
            enableExpSleepCheckBox.bind(expSleepEnabledProperty)
            enableSortieSleepCheckBox.bind(sortieSleepEnabledProperty)
        }
        sleepContent.disableProperty().bind(enableSleepCheckBox.selectedProperty().not())
        expSleepContent.disableProperty().bind(enableExpSleepCheckBox.selectedProperty().not())
        sortieSleepContent.disableProperty().bind(enableSortieSleepCheckBox.selectedProperty().not())
        invertSleepCheckBox.disableProperty().bind(sleepContent.disableProperty())
        invertExpSleepCheckBox.disableProperty().bind(expSleepContent.disableProperty())
        invertSortieSleepCheckBox.disableProperty().bind(sortieSleepContent.disableProperty())
    }
}
