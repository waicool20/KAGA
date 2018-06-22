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
import com.waicool20.kaga.kcauto.KCAutoStats
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import javafx.scene.layout.VBox
import tornadofx.*
import java.text.DecimalFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.concurrent.fixedRateTimer


class StatsView : View() {
    override val root: VBox by fxml("/views/stats.fxml", hasControllerAttribute = true)
    private val startingTimeLabel: Label by fxid()
    private val timeElapsedLabel: Label by fxid()

    private val sortiesTitledPane: TitledPane by fxid()
    private val sortiesDoneLabel: Label by fxid()
    private val sortiesAttemptedLabel: Label by fxid()
    private val sortiesPerHourLabel: Label by fxid()

    private val pvpsConductedLabel: Label by fxid()

    private val expeditionsTitledPane: TitledPane by fxid()
    private val expeditionsSentLabel: Label by fxid()
    private val expeditionsReceivedLabel: Label by fxid()

    private val questsTitledPane: TitledPane by fxid()
    private val questsDoneLabel: Label by fxid()
    private val questsStartedLabel: Label by fxid()

    private val miscTitledPane: TitledPane by fxid()
    private val resuppliesLabel: Label by fxid()
    private val repairsLabel: Label by fxid()
    private val bucketsUsedLabel: Label by fxid()
    private val shipsSwitchedLabel: Label by fxid()
    private val crashesLabel: Label by fxid()
    private val recoveriesLabel: Label by fxid()

    init {
        val listener = ChangeListener<Number> { _, _, newVal ->
            if (newVal.toInt() % 4 == 0) currentStage?.sizeToScene()
        }
        sortiesTitledPane.heightProperty().addListener(listener)
        expeditionsTitledPane.heightProperty().addListener(listener)
        questsTitledPane.heightProperty().addListener(listener)
        miscTitledPane.heightProperty().addListener(listener)
        fixedRateTimer(period = 1000L) {
            if (Kaga.KCAUTO.isRunning()) {
                Platform.runLater { updateStats() }
            }
        }
    }

    private fun updateStats() = Kaga.KCAUTO.statsTracker.run {
        timeElapsedLabel.text = timeDelta(startingTime)
        startingTimeLabel.text = startingTime?.atZone(ZoneId.systemDefault())
                ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: ""

        sortiesDoneLabel.text = get(KCAutoStats::sortiesDone).toString()
        sortiesAttemptedLabel.text = get(KCAutoStats::sortiesAttempted).toString()
        sortiesPerHourLabel.text = formatDecimal(get(KCAutoStats::sortiesDone) / hoursSince(startingTime))

        expeditionsSentLabel.text = get(KCAutoStats::expeditionsSent).toString()
        expeditionsReceivedLabel.text = get(KCAutoStats::expeditionsReceived).toString()

        pvpsConductedLabel.text = get(KCAutoStats::pvpsDone).toString()

        questsDoneLabel.text = get(KCAutoStats::questsDone).toString()
        questsStartedLabel.text = get(KCAutoStats::questsStarted).toString()

        repairsLabel.text = get(KCAutoStats::repairs).toString()
        resuppliesLabel.text = get(KCAutoStats::resupplies).toString()
        bucketsUsedLabel.text = get(KCAutoStats::bucketsUsed).toString()
        shipsSwitchedLabel.text = get(KCAutoStats::shipsSwitched).toString()
        crashesLabel.text = crashes.toString()
        recoveriesLabel.text = get(KCAutoStats::recoveries).toString()
    }

    private fun timeDelta(time: Instant?): String {
        val duration = time?.let { Duration.between(it, Instant.now()).abs() }
                ?: return "00:00:00"
        return formatDuration(duration) ?: "00:00:00"
    }

    private fun formatDuration(duration: Duration?) = duration?.seconds?.let {
        String.format("%02d:%02d:%02d", it / 3600, (it % 3600) / 60, it % 60)
    }

    private fun hoursSince(time: Instant?) =
            time?.let { Duration.between(it, Instant.now()).seconds / 3600.0 } ?: 0.0

    private fun formatDecimal(d: Double) = DecimalFormat("0.00").format(d).replace("\uFFFD", "0.00")
}
