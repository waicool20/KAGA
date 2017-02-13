package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.NoneSelectableCellFactory
import com.waicool20.kaga.util.bind
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.util.StringConverter
import tornadofx.bind
import tornadofx.find


class SortieTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var eventCheckbox: CheckBox
    @FXML private lateinit var fleetCompComboBox: ComboBox<Int>
    @FXML private lateinit var areaComboBox: ComboBox<String>
    @FXML private lateinit var combinedFleetCheckBox: CheckBox
    @FXML private lateinit var nodesSpinner: Spinner<Int>
    @FXML private lateinit var nodeSelectsTextField: TextField
    @FXML private lateinit var retreatLimitComboBox: ComboBox<Int>
    @FXML private lateinit var repairLimitComboBox: ComboBox<Int>
    @FXML private lateinit var repairTimeLimitSpinner: Spinner<Int>
    @FXML private lateinit var checkFatigueCheckBox: CheckBox
    @FXML private lateinit var checkPortCheckBox: CheckBox
    @FXML private lateinit var medalStopCheckBox: CheckBox
    @FXML private lateinit var lastNodePushCheckBox: CheckBox

    @FXML private lateinit var content: GridPane

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
            "6-1", "6-2", "6-3", "6-4", "6-5"
    )

    private val eventMaps = setOf(
            "-- Page 1 --",
            "1-1", "1-2",
            "-- Page 2 --",
            "2-1", "2-2",
            "-- Page 3 --",
            "3-1", "3-2",
            "-- Page 4 --",
            "4-1", "4-2",
            "-- Page 5 --",
            "5-1", "5-2",
            "-- Page 6 --",
            "6-1", "6-2",
            "-- Page 7 --",
            "7-1", "7-2",
            "-- Page 8 --",
            "8-1", "8-2")

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        fleetCompComboBox.items.setAll((1..5).toList())
        areaComboBox.items.setAll(maps)
        areaComboBox.cellFactory = NoneSelectableCellFactory("--.+?--".toRegex())
        with(Kaga.PROFILE!!.sortie) {
            if (area == "E") {
                eventCheckbox.isSelected = true
                areaComboBox.items.setAll(eventMaps)
                areaComboBox.value = subarea
            } else {
                eventCheckbox.isSelected = false
                areaComboBox.items.setAll(maps)
                areaComboBox.value = "$area-$subarea"
            }
        }
        nodesSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE)
        val damageLevels = listOf("Light damage", "Moderate damage", "Critical damage", "Null")
        val damageConverter = object : StringConverter<Int>() {
            override fun toString(int: Int?): String = damageLevels[int ?: 3]

            override fun fromString(string: String?): Int = damageLevels.indexOf(string)
        }
        retreatLimitComboBox.items.setAll((0..2).toList())
        repairLimitComboBox.items.setAll((0..2).toList())
        retreatLimitComboBox.converter = damageConverter
        repairLimitComboBox.converter = damageConverter
        repairTimeLimitSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.sortie) {
            enableButton.bind(enabledProperty)
            fleetCompComboBox.bind(fleetCompProperty)
            areaComboBox.valueProperty().addListener { observableValue, oldVal, newVal ->
                setProfileArea(newVal)
            }
            eventCheckbox.selectedProperty().addListener { obs, oldVal, newVal ->
                areaComboBox.items.setAll(if (newVal) eventMaps else maps)
                areaComboBox.value = areaComboBox.items.find { !it.matches("--.+?--".toRegex()) }
                setProfileArea(areaComboBox.selectionModel.selectedItem)
            }
            combinedFleetCheckBox.bind(combinedFleetProperty)
            nodesSpinner.bind(nodesProperty)
            nodeSelectsTextField.bind(nodeSelectsProperty)
            retreatLimitComboBox.bind(retreatLimitProperty)
            repairLimitComboBox.bind(repairLimitProperty)
            repairTimeLimitSpinner.bind(repairTimeLimitProperty)
            checkFatigueCheckBox.bind(checkFatigueProperty)
            checkPortCheckBox.bind(portCheckProperty)
            medalStopCheckBox.bind(medalStopProperty)
            lastNodePushCheckBox.bind(lastNodePushProperty)
        }
        combinedFleetCheckBox.visibleProperty().bind(eventCheckbox.selectedProperty())
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
        eventCheckbox.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }

    @FXML private fun onConfigureFormationsButton() =
            find(FormationChooserView::class).openModal(owner = Kaga.ROOT_STAGE.owner)

    @FXML private fun onConfigureNightBattlesButton() =
            find(NightBattlesChooserView::class).openModal(owner = Kaga.ROOT_STAGE.owner)

    private fun setProfileArea(map: String?) {
        if (map == null) return
        with(Kaga.PROFILE!!.sortie) {
            if (eventCheckbox.isSelected) {
                area = "E"
                subarea = map
            } else {
                area = map[0].toString()
                subarea = map[2].toString()
            }
        }
    }
}
