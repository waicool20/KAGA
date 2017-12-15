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
    @FXML private lateinit var fatigueSwitchCheckBox: CheckBox
    @FXML private lateinit var useBucketsCheckBox: CheckBox
    @FXML private lateinit var enableLbasButton: CheckBox
    @FXML private lateinit var group1CheckBox: CheckBox
    @FXML private lateinit var group2CheckBox: CheckBox
    @FXML private lateinit var group3CheckBox: CheckBox
    @FXML private lateinit var configGrp1NodesBtn: Button
    @FXML private lateinit var configGrp2NodesBtn: Button
    @FXML private lateinit var configGrp3NodesBtn: Button

    @FXML private lateinit var lbasContent: GridPane
    @FXML private lateinit var subSwitchContent: GridPane

    fun initialize() = Unit
    /* TODO Disabled temporarily till kcauto-kai is finalized
    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        updateGroupCheckBoxes(Kaga.PROFILE.lbas.enabledGroups)
        val damageLevels = listOf("Light damage", "Moderate damage", "Critical damage", "Null")
        val damageConverter = object : StringConverter<Int>() {
            override fun toString(int: Int?): String = damageLevels[int ?: 3]

            override fun fromString(string: String?): Int = damageLevels.indexOf(string)
        }
        replaceLimitComboBox.items.setAll((0..2).toList())
        replaceLimitComboBox.converter = damageConverter
    }

    private fun createBindings() {
        with(Kaga.PROFILE) {
            with(submarineSwitch) {
                enableSubSwitchButton.bind(enabledProperty)
                replaceLimitComboBox.bind(replaceLimitProperty)
                fatigueSwitchCheckBox.bind(fatigueSwitchProperty)
                useBucketsCheckBox.bind(useBucketsProperty)
            }
            with(lbas) {
                enableLbasButton.bind(enabledProperty)
                enabledGroups.addListener(SetChangeListener { change -> updateGroupCheckBoxes(change.set) })
            }
        }
        subSwitchContent.disableProperty().bind(Bindings.not(enableSubSwitchButton.selectedProperty()))
        group1CheckBox.selectedProperty().addListener({ _, _, newVal -> setGroups(newVal, 1) })
        group2CheckBox.selectedProperty().addListener({ _, _, newVal -> setGroups(newVal, 2) })
        group3CheckBox.selectedProperty().addListener({ _, _, newVal -> setGroups(newVal, 3) })
        lbasContent.disableProperty().bind(Bindings.not(enableLbasButton.selectedProperty()))
        configGrp1NodesBtn.disableProperty().bind(Bindings.not(group1CheckBox.selectedProperty()))
        configGrp2NodesBtn.disableProperty().bind(Bindings.not(group2CheckBox.selectedProperty()))
        configGrp3NodesBtn.disableProperty().bind(Bindings.not(group3CheckBox.selectedProperty()))
        configSubSwitchBtn.disableProperty().bind(Bindings.not(enableSubSwitchButton.selectedProperty()))
    }

    private fun setGroups(newVal: Boolean, group: Int) {
        with(Kaga.PROFILE.lbas) {
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


    @FXML private fun onConfigureSubSwitchButton() =
            find(SubSwitchChooserView::class).openModal()


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
    */
}
