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
import com.waicool20.kaga.util.bind
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
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
    }

    private fun createBindings() {
        with(Kaga.PROFILE.sortie) {
            grp1CheckComboBox.bind(lbasGroup1NodesProperty) { updateLBASGroups() }
            grp2CheckComboBox.bind(lbasGroup2NodesProperty) { updateLBASGroups() }
            grp3CheckComboBox.bind(lbasGroup3NodesProperty) { updateLBASGroups() }
        }
        updateLBASGroups()
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
}
