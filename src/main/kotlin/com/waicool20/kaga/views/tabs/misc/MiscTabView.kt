package com.waicool20.kaga.views.tabs.misc

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.bind
import javafx.beans.binding.Bindings
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.StringConverter
import tornadofx.*

class MiscTabView {
    @FXML private lateinit var enableSubSwitchButton: CheckBox
    @FXML private lateinit var configSubSwitchBtn: Button
    @FXML private lateinit var replaceLimitComboBox: ComboBox<Int>
    @FXML private lateinit var enableLbasButton: CheckBox
    @FXML private lateinit var group1CheckBox: CheckBox
    @FXML private lateinit var group2CheckBox: CheckBox
    @FXML private lateinit var group3CheckBox: CheckBox
    @FXML private lateinit var configGrp1NodesBtn: Button
    @FXML private lateinit var configGrp2NodesBtn: Button
    @FXML private lateinit var configGrp3NodesBtn: Button

    @FXML private lateinit var lbasContent: GridPane
    @FXML private lateinit var subSwitchContent: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        updateGroupCheckBoxes(Kaga.PROFILE!!.lbas.enabledGroups)
        val damageLevels = listOf("Light damage", "Moderate damage", "Critical damage", "Null")
        val damageConverter = object : StringConverter<Int>() {
            override fun toString(int: Int?): String = damageLevels[int ?: 3]

            override fun fromString(string: String?): Int = damageLevels.indexOf(string)
        }
        replaceLimitComboBox.items.setAll((0..2).toList())
        replaceLimitComboBox.converter = damageConverter
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!) {
            enableSubSwitchButton.bind(submarineSwitch.enabledProperty)
            replaceLimitComboBox.bind(submarineSwitch.replaceLimitProperty)
            with(lbas) {
                enableLbasButton.bind(enabledProperty)
                enabledGroups.addListener(SetChangeListener { change -> updateGroupCheckBoxes(change.set) })
            }
        }
        subSwitchContent.disableProperty().bind(Bindings.not(enableSubSwitchButton.selectedProperty()))
        group1CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 1) })
        group2CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 2) })
        group3CheckBox.selectedProperty().addListener({ obs, oldVal, newVal -> setGroups(newVal, 3) })
        lbasContent.disableProperty().bind(Bindings.not(enableLbasButton.selectedProperty()))
        configGrp1NodesBtn.disableProperty().bind(Bindings.not(group1CheckBox.selectedProperty()))
        configGrp2NodesBtn.disableProperty().bind(Bindings.not(group2CheckBox.selectedProperty()))
        configGrp3NodesBtn.disableProperty().bind(Bindings.not(group3CheckBox.selectedProperty()))
        configSubSwitchBtn.disableProperty().bind(Bindings.not(enableSubSwitchButton.selectedProperty()))
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

    @FXML private fun onConfigureGroup1NodesButton() = configureNode(1)

    @FXML private fun onConfigureGroup2NodesButton() = configureNode(2)

    @FXML private fun onConfigureGroup3NodesButton() = configureNode(3)

    @FXML private fun onConfigureSubSwitchButton() {
        find(SubSwitchChooserView::class).openModal()
    }

    private fun configureNode(group: Int) {
        val loader = FXMLLoader(Kaga::class.java.classLoader.getResource("views/single-list.fxml"))
        loader.setController(LbasNodeChooserView(group))
        val scene = Scene(loader.load())
        with(Stage()) {
            this.scene = scene
            title = "KAGA - LBAS Nodes Configuration"
            initOwner(Kaga.ROOT_STAGE.owner)
            initModality(Modality.WINDOW_MODAL)
            show()
            minHeight = height + 25
            minWidth = width + 25
        }
    }
}
