package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.bind
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.util.StringConverter
import tornadofx.bind


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
        startTimeHourSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23)
        startTimeMinSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59)
        startTimeHourSpinner.valueFactory.isWrapAround = true
        startTimeMinSpinner.valueFactory.isWrapAround = true
        startTimeHourSpinner.editor.alignment = Pos.CENTER
        startTimeMinSpinner.editor.alignment = Pos.CENTER
        val formatter = object : StringConverter<Int>() {
            override fun toString(integer: Int?): String =
                    if (integer == null) "00" else String.format("%02d", integer)

            override fun fromString(s: String): Int = s.toInt()
        }
        startTimeHourSpinner.editor.textFormatter = TextFormatter(formatter)
        startTimeMinSpinner.editor.textFormatter = TextFormatter(formatter)
        val startTime = Kaga.PROFILE!!.scheduledSleep.startTime
        startTimeHourSpinner.valueFactory.value = startTime.substring(0, 2).toInt()
        startTimeMinSpinner.valueFactory.value = startTime.substring(2, 4).toInt()
        sleepLengthSpinner.valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, java.lang.Double.MAX_VALUE, 0.0, 0.1)
        modeChoiceBox.items.setAll(*KancolleAutoProfile.ScheduledStopMode.values())
        countSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.scheduledSleep) {
            enableSleepButton.bind(enabledProperty)
            val binding = Bindings.concat(startTimeHourSpinner.valueFactory.valueProperty().asString("%02d"),
                    startTimeMinSpinner.valueFactory.valueProperty().asString("%02d"))
            startTimeProperty.bind(binding)
            sleepLengthSpinner.bind(lengthProperty)
        }
        with(Kaga.PROFILE!!.scheduledStop) {
            enableAutoStopButton.bind(enabledProperty)
            modeChoiceBox.bind(modeProperty)
            countSpinner.bind(countProperty)
        }
        sleepContent.visibleProperty().bind(enableSleepButton.selectedProperty())
        stopContent.visibleProperty().bind(enableAutoStopButton.selectedProperty())
    }
}
