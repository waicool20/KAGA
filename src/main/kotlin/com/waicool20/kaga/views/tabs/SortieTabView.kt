package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.bind
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import tornadofx.bind


class SortieTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var fleetCompComboBox: ComboBox<Int>
    @FXML private lateinit var areaComboBox: ComboBox<Int>
    @FXML private lateinit var subareaComboBox: ComboBox<Int>
    @FXML private lateinit var combinedFleetCheckBox: CheckBox
    @FXML private lateinit var nodesSpinner: Spinner<Int>
    @FXML private lateinit var nodeSelectsTextField: TextField
    @FXML private lateinit var nightBattlesCheckBox: CheckBox
    @FXML private lateinit var retreatLimitSpinner: Spinner<Int>
    @FXML private lateinit var repairLimitSpinner: Spinner<Int>
    @FXML private lateinit var repairTimeLimitSpinner: Spinner<Int>
    @FXML private lateinit var checkFatigueCheckBox: CheckBox
    @FXML private lateinit var checkPortCheckBox: CheckBox
    @FXML private lateinit var medalStopCheckBox: CheckBox
    @FXML private lateinit var lastNodePushCheckBox: CheckBox

    @FXML private lateinit var content: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        fleetCompComboBox.items.setAll((1..5).toList())
        areaComboBox.items.setAll((1..6).toList())
        subareaComboBox.items.setAll((1..6).toList())
        nodesSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        retreatLimitSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        repairLimitSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        repairTimeLimitSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.sortie) {
            enableButton.bind(enabledProperty)
            fleetCompComboBox.bind(fleetCompProperty)
            areaComboBox.bind(areaProperty)
            subareaComboBox.bind(subareaProperty)
            combinedFleetCheckBox.bind(combinedFleetProperty)
            nodesSpinner.bind(nodesProperty)
            nodeSelectsTextField.bind(nodeSelectsProperty)
            nightBattlesCheckBox.bind(nightBattlesProperty)
            retreatLimitSpinner.bind(retreatLimitProperty)
            repairLimitSpinner.bind(repairLimitProperty)
            repairTimeLimitSpinner.bind(repairTimeLimitProperty)
            checkFatigueCheckBox.bind(checkFatigueProperty)
            checkPortCheckBox.bind(portCheckProperty)
            medalStopCheckBox.bind(medalStopProperty)
            lastNodePushCheckBox.bind(lastNodePushProperty)
        }
        content.visibleProperty().bind(enableButton.selectedProperty())
    }

    @FXML private fun onConfigureFormationsButton() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }
}
