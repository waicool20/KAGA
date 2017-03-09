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
import java.util.*


class StatsView : View() {
    override val root: GridPane by fxml("/views/stats.fxml", hasControllerAttribute = true)
    private val startingTimeLabel: Label by fxid()
    private val timeElapsedLabel: Label by fxid()
    private val sortiesConductedLabel: Label by fxid()
    private val sortiesPerHourLabel: Label by fxid()
    private val expeditionsConductedLabel: Label by fxid()
    private val expeditionsPerHourLabel: Label by fxid()
    private val pvpsConductedLabel: Label by fxid()
    private val pvpsPerHourLabel: Label by fxid()
    private val submarinesSwitchedLabel: Label by fxid()
    private val crashesLabel: Label by fxid()
    private val timer = Timer()

    init {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (Kaga.KANCOLLE_AUTO.isRunning()) {
                    Platform.runLater { updateStats() }
                }
            }
        }, 0L, 1000L)
    }

    private fun updateStats() {
        with(Kaga.KANCOLLE_AUTO.statsTracker) {
            timeElapsedLabel.text = elapsedTimeSince(startingTime)
            startingTimeLabel.text = startingTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: ""
            sortiesConductedLabel.text = sortiesConductedTotal().toString()
            sortiesPerHourLabel.text = formatDecimal(sortiesConductedTotal() / hoursSince(startingTime))
            expeditionsConductedLabel.text = expeditionsConductedTotal().toString()
            expeditionsPerHourLabel.text = formatDecimal(expeditionsConductedTotal() / hoursSince(startingTime))
            pvpsConductedLabel.text = pvpsConductedTotal().toString()
            pvpsPerHourLabel.text = formatDecimal(pvpsConductedTotal() / hoursSince(startingTime))
            submarinesSwitchedLabel.text = submarinesSwitchedTotal().toString()
            crashesLabel.text = crashes.toString()
        }
    }

    private fun elapsedTimeSince(time: LocalDateTime?): String {
        if (time == null) return "0:00:00"
        with(Duration.between(time, LocalDateTime.now()).seconds) {
            return String.format("%d:%02d:%02d", this / 3600, (this % 3600) / 60, this % 60)
        }
    }

    private fun hoursSince(time: LocalDateTime?): Double =
            (Duration.between(time, LocalDateTime.now()).seconds / 3600.0)

    private fun formatDecimal(d: Double) = DecimalFormat("0.00").format(d).replace("\uFFFD", "0.00")
}
