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
import com.waicool20.kaga.config.KancolleAutoProfile
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.Label
import org.controlsfx.control.CheckComboBox

class MiscTabView {

    private val VALID_NODES = KancolleAutoProfile.VALID_NODES.filterNot { it.matches("^\\d+".toRegex()) }.let {
        FXCollections.observableList(it)
    }

    @FXML private lateinit var grp1CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var grp2CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var grp3CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var grp1NodesWarnLabel: Label
    @FXML private lateinit var grp2NodesWarnLabel: Label
    @FXML private lateinit var grp3NodesWarnLabel: Label

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        grp1CheckComboBox.items.setAll(VALID_NODES)
        grp2CheckComboBox.items.setAll(VALID_NODES)
        grp3CheckComboBox.items.setAll(VALID_NODES)

        with(Kaga.PROFILE.sortie) {
            lbasGroup1Nodes.forEach { grp1CheckComboBox.checkModel.check(it) }
            lbasGroup2Nodes.forEach { grp2CheckComboBox.checkModel.check(it) }
            lbasGroup3Nodes.forEach { grp3CheckComboBox.checkModel.check(it) }
        }
        updateLBASGroups()
    }

    private fun createBindings() {
        with(Kaga.PROFILE.sortie) {
            grp1CheckComboBox.checkModel.checkedItems.addListener { change: ListChangeListener.Change<out String> ->
                lbasGroup1Nodes.setAll(change.list)
                updateLBASGroups()
            }
            grp2CheckComboBox.checkModel.checkedItems.addListener { change: ListChangeListener.Change<out String> ->
                lbasGroup2Nodes.setAll(change.list)
                updateLBASGroups()
            }
            grp3CheckComboBox.checkModel.checkedItems.addListener { change: ListChangeListener.Change<out String> ->
                lbasGroup3Nodes.setAll(change.list)
                updateLBASGroups()
            }
        }
    }

    private fun updateLBASGroups() {
        with(Kaga.PROFILE.sortie) {
            if (lbasGroup1Nodes.size == 2) {
                lbasGroups.add("1")
                grp1NodesWarnLabel.isVisible = false
            } else {
                lbasGroups.remove("1")
                grp1NodesWarnLabel.isVisible = true
            }
            if (lbasGroup2Nodes.size == 2) {
                lbasGroups.add("2")
                grp2NodesWarnLabel.isVisible = false
            } else {
                lbasGroups.remove("2")
                grp2NodesWarnLabel.isVisible = true
            }
            if (lbasGroup3Nodes.size == 2) {
                lbasGroups.add("3")
                grp3NodesWarnLabel.isVisible = false
            } else {
                lbasGroups.remove("3")
                grp3NodesWarnLabel.isVisible = true
            }
        }
    }

    /* TODO Disabled temporarily till kcauto-kai is finalized
    @FXML private lateinit var enableSubSwitchButton: CheckBox
    @FXML private lateinit var configSubSwitchBtn: Button
    @FXML private lateinit var replaceLimitComboBox: ComboBox<Int>
    @FXML private lateinit var fatigueSwitchCheckBox: CheckBox
    @FXML private lateinit var useBucketsCheckBox: CheckBox

    @FXML private lateinit var subSwitchContent: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
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
        }
        subSwitchContent.disableProperty().bind(Bindings.not(enableSubSwitchButton.selectedProperty()))
    }

    @FXML private fun onConfigureSubSwitchButton() =
            find(SubSwitchChooserView::class).openModal()
    */
}
