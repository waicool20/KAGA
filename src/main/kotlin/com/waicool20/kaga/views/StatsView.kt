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
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import tornadofx.*
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.fixedRateTimer


class StatsView : View() {
    override val root: GridPane by fxml("/views/stats.fxml", hasControllerAttribute = true)
    private val startingTimeLabel: Label by fxid()
    private val timeElapsedLabel: Label by fxid()
    private val sortiesConductedLabel: Label by fxid()
    private val sortiesPerHourLabel: Label by fxid()
    private val expeditionsConductedLabel: Label by fxid()
    private val pvpsConductedLabel: Label by fxid()
    private val bucketsUsedLabel: Label by fxid()
    private val submarinesSwitchedLabel: Label by fxid()
    private val crashesLabel: Label by fxid()

    init {
        fixedRateTimer(period = 1000L) {
            if (Kaga.KANCOLLE_AUTO.isRunning()) {
                Platform.runLater { updateStats() }
            }
        }
    }

    private fun updateStats() = Kaga.KANCOLLE_AUTO.statsTracker.run {
        timeElapsedLabel.text = elapsedTimeSince(startingTime)
        startingTimeLabel.text = startingTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: ""
        sortiesConductedLabel.text = sortiesConductedTotal().toString()
        sortiesPerHourLabel.text = formatDecimal(sortiesConductedTotal() / hoursSince(startingTime))
        expeditionsConductedLabel.text = expeditionsConductedTotal().toString()
        pvpsConductedLabel.text = pvpsConductedTotal().toString()
        bucketsUsedLabel.text = bucketsUsedTotal().toString()
        submarinesSwitchedLabel.text = submarinesSwitchedTotal().toString()
        crashesLabel.text = crashes.toString()
    }


    private fun elapsedTimeSince(time: LocalDateTime?): String {
        if (time == null) return "0:00:00"
        with(Duration.between(time, LocalDateTime.now()).seconds) {
            return String.format("%d:%02d:%02d", this / 3600, (this % 3600) / 60, this % 60)
        }
    }

    private fun hoursSince(time: LocalDateTime?): Double {
        if (time == null) return 0.0
        return (Duration.between(time, LocalDateTime.now()).seconds / 3600.0)
    }

    private fun formatDecimal(d: Double) = DecimalFormat("0.00").format(d).replace("\uFFFD", "0.00")
}
