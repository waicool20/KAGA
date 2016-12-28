package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.views.NodeChooserView
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage
import tornadofx.bind
import tornadofx.find

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
        group1CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 1) })
        group2CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 2) })
        group3CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 3) })
        content.visibleProperty().bind(enableButton.selectedProperty())
    }

    private fun setGroups(newVal: Boolean, group: Int) {
        with(Kaga.PROFILE!!.lbas) {
            if (newVal) {
                enabledGroups.add(group)
            } else {
                enabledGroups.remove(group)
            }
        }
    }

    private fun updateGroupCheckBoxes(set: Set<Int>) {
        group1CheckBox.selectedProperty().value = set.contains(1)
        group2CheckBox.selectedProperty().value = set.contains(2)
        group3CheckBox.selectedProperty().value = set.contains(3)
    }

    @FXML private fun onConfigureGroup1NodesButton() {
        configureNode(1)
    }

    @FXML private fun onConfigureGroup2NodesButton() {
        configureNode(2)
    }

    @FXML private fun onConfigureGroup3NodesButton() {
        configureNode(3)
    }

    private fun configureNode(group: Int) {
        val loader = FXMLLoader(Kaga::class.java.classLoader.getResource("views/node-chooser.fxml"))
        loader.setController(NodeChooserView(group))
        val scene = Scene(loader.load())
        with(Stage()) {
            this.scene = scene
            title = "KAGA - Group Nodes Configuration"
            initOwner(Kaga.ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            show()
            minHeight = height + 25
            minWidth = width + 25
        }
    }
}
