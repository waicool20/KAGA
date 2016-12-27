package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import javafx.beans.value.ObservableValue
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.layout.GridPane
import tornadofx.bind

class LbasTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var group1CheckBox: CheckBox
    @FXML private lateinit var group2CheckBox: CheckBox
    @FXML private lateinit var group3CheckBox: CheckBox

    @FXML private lateinit var content: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        updateGroupCheckBoxes(Kaga.PROFILE!!.lbas.enabledGroups)
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.lbas) {
            enableButton.bind(enabledProperty)
            enabledGroups.addListener(SetChangeListener { change -> updateGroupCheckBoxes(change.set) })
        }
        group1CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 1)})
        group2CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 2)})
        group3CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 3)})
        content.visibleProperty().bind(enableButton.selectedProperty())
    }

    private fun setGroups(newVal: Boolean, group: Int) {
        with(Kaga.PROFILE!!.lbas) {
            if (newVal) {
                enabledGroups.add(3)
            } else {
                enabledGroups.remove(3)
            }
        }
    }

    private fun updateGroupCheckBoxes(set: Set<Int>) {
        group1CheckBox.selectedProperty().value = set.contains(1)
        group2CheckBox.selectedProperty().value = set.contains(2)
        group3CheckBox.selectedProperty().value = set.contains(3)
    }

    @FXML private fun onConfigureGroup1NodesButton() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    @FXML private fun onConfigureGroup2NodesButton() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }

    @FXML private fun onConfigureGroup3NodesButton() {
        throw UnsupportedOperationException("Not Implemented") // TODO Implement this function
    }
}
