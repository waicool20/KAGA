package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.asTimeSpinner
import com.waicool20.kaga.util.bind
import com.waicool20.kaga.util.updateOtherSpinnerOnWrap
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.GridPane
import tornadofx.bind
import java.util.concurrent.TimeUnit


class SchedulingTabView {
    @FXML private lateinit var enableSleepButton: CheckBox
    @FXML private lateinit var startTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var startTimeMinSpinner: Spinner<Int>
    @FXML private lateinit var sleepLengthSpinner: Spinner<Double>

    @FXML private lateinit var enableAutoStopButton: CheckBox
    @FXML private lateinit var modeChoiceBox: ChoiceBox<KancolleAutoProfile.ScheduledStopMode>
    @FXML private lateinit var countSpinner: Spinner<Int>

    @FXML private lateinit var sleepContent: GridPane
    @FXML private lateinit var stopContent: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }


    private fun setValues() {
        startTimeHourSpinner.asTimeSpinner(TimeUnit.HOURS)
        startTimeMinSpinner.asTimeSpinner(TimeUnit.MINUTES)
        with(String.format("%04d", Kaga.PROFILE!!.scheduledSleep.startTime.toInt())) {
            startTimeHourSpinner.valueFactory.value = this.substring(0, 2).toInt()
            startTimeMinSpinner.valueFactory.value = this.substring(2, 4).toInt()
        }
        startTimeMinSpinner.updateOtherSpinnerOnWrap(startTimeHourSpinner, 0, 59)
        sleepLengthSpinner.valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, java.lang.Double.MAX_VALUE, 0.0, 0.1)
        modeChoiceBox.items.setAll(*KancolleAutoProfile.ScheduledStopMode.values())
        countSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.scheduledSleep) {
            enableSleepButton.bind(enabledProperty)
            val binding = Bindings.concat(startTimeHourSpinner.valueProperty().asString("%02d"),
                    startTimeMinSpinner.valueProperty().asString("%02d"))
            startTimeProperty.bind(binding)
            sleepLengthSpinner.bind(lengthProperty)
        }
        with(Kaga.PROFILE!!.scheduledStop) {
            enableAutoStopButton.bind(enabledProperty)
            modeChoiceBox.bind(modeProperty)
            countSpinner.bind(countProperty)
        }
        sleepContent.disableProperty().bind(Bindings.not(enableSleepButton.selectedProperty()))
        stopContent.disableProperty().bind(Bindings.not(enableAutoStopButton.selectedProperty()))
    }
}
