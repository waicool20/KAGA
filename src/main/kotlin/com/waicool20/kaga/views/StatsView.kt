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
    private val crashesLabel: Label by fxid()
    private val timer = Timer()

    init {
        with(Kaga.KANCOLLE_AUTO.stats) {
            startingTime.addListener { obs, oldVal, newVal ->
                Platform.runLater {
                    startingTimeLabel.text = newVal?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: ""
                }
            }
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Platform.runLater {
                        if (Kaga.KANCOLLE_AUTO.isRunning()) {
                            timeElapsedLabel.text = if (startingTime.value != null) {
                                val secondsElapsed = Duration.between(startingTime.value, LocalDateTime.now()).seconds
                                String.format("%d:%02d:%02d", secondsElapsed / 3600, (secondsElapsed % 3600) / 60, secondsElapsed % 60)
                            } else "0:00:00"
                        }
                    }
                }
            }, 0L, 1000L)
            sortiesConducted.addListener { obs, oldVal, newVal ->
                Platform.runLater {
                    sortiesConductedLabel.text = newVal.toString()
                    sortiesPerHourLabel.text = DecimalFormat("0.00")
                            .format(newVal.toFloat() / (Duration.between(startingTime.value, LocalDateTime.now()).seconds / 3600.0))
                }
            }
            crashes.addListener { obs, oldVal, newVal ->
                Platform.runLater {
                    crashesLabel.text = newVal.toString()
                }
            }
        }
    }
}
