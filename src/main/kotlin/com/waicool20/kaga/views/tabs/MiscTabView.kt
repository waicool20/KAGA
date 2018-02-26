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

package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.bind
import com.waicool20.kaga.util.persist
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.Label
import org.controlsfx.control.CheckComboBox
import kotlin.reflect.KMutableProperty0

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
            createLbasBinding(::grp1CheckComboBox, lbasGroup1NodesProperty, grp1NodesWarnLabel)
            createLbasBinding(::grp2CheckComboBox, lbasGroup2NodesProperty, grp2NodesWarnLabel)
            createLbasBinding(::grp3CheckComboBox, lbasGroup3NodesProperty, grp3NodesWarnLabel)
        }
    }

    private fun createLbasBinding(boxProp: KMutableProperty0<CheckComboBox<String>>, list: SimpleListProperty<String>, label: Label) {
        val box = boxProp.get()
        box.bind(list)
        list.sizeProperty().isEqualTo(2).persist().addListener { _, _, newVal ->
            label.isVisible = !newVal
            val group = boxProp.name.filter { it.isDigit() }
            if (newVal) {
                Kaga.PROFILE.sortie.lbasGroups.add(group)
            } else {
                Kaga.PROFILE.sortie.lbasGroups.remove(group)
            }
        }
    }
}
