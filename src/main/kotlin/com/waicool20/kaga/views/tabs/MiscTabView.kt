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
import org.controlsfx.control.PrefixSelectionChoiceBox
import org.controlsfx.control.PrefixSelectionComboBox
import tornadofx.*
import kotlin.reflect.KMutableProperty0

class MiscTabView {

    private val VALID_NODES = KancolleAutoProfile.VALID_NODES
            .filterNot { it.matches("^\\d+".toRegex()) }
            .let { FXCollections.observableList(listOf("") + it) }

    @FXML private lateinit var grp1choice1ComboBox: PrefixSelectionComboBox<String>
    @FXML private lateinit var grp1choice2ComboBox: PrefixSelectionComboBox<String>
    @FXML private lateinit var grp2choice1ComboBox: PrefixSelectionComboBox<String>
    @FXML private lateinit var grp2choice2ComboBox: PrefixSelectionComboBox<String>
    @FXML private lateinit var grp3choice1ComboBox: PrefixSelectionComboBox<String>
    @FXML private lateinit var grp3choice2ComboBox: PrefixSelectionComboBox<String>
    @FXML private lateinit var grp1NodesWarnLabel: Label
    @FXML private lateinit var grp2NodesWarnLabel: Label
    @FXML private lateinit var grp3NodesWarnLabel: Label

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        grp1choice1ComboBox.items.setAll(VALID_NODES)
        grp1choice2ComboBox.items.setAll(VALID_NODES)
        grp2choice1ComboBox.items.setAll(VALID_NODES)
        grp2choice2ComboBox.items.setAll(VALID_NODES)
        grp3choice1ComboBox.items.setAll(VALID_NODES)
        grp3choice2ComboBox.items.setAll(VALID_NODES)
    }

    private fun createBindings() {
        with(Kaga.PROFILE.sortie) {
            createLbasBinding(::grp1choice1ComboBox, ::grp1choice2ComboBox, lbasGroup1NodesProperty, grp1NodesWarnLabel)
            createLbasBinding(::grp2choice1ComboBox, ::grp2choice2ComboBox, lbasGroup2NodesProperty, grp2NodesWarnLabel)
            createLbasBinding(::grp3choice1ComboBox, ::grp3choice2ComboBox, lbasGroup3NodesProperty, grp3NodesWarnLabel)
        }
    }

    private fun createLbasBinding(
            choice1Prop: KMutableProperty0<PrefixSelectionComboBox<String>>,
            choice2Prop: KMutableProperty0<PrefixSelectionComboBox<String>>,
            list: SimpleListProperty<String>,
            label: Label
    ) {
        val box1 = choice1Prop.get()
        val box2 = choice2Prop.get()
        val choice1 = list.getOrNull(0) ?: ""
        val choice2 = list.getOrNull(1) ?: ""

        box1.selectionModel.select(choice1)
        box2.selectionModel.select(choice2)

        val update: (Boolean) -> Unit = { updateList ->
            val items = listOf(box1.selectedItem, box2.selectedItem).filterNot { it.isNullOrBlank() }
            val sizeIsOk = items.size == 2
            label.isVisible = !sizeIsOk
            val group = choice1Prop.name.first { it.isDigit() }.toString()
            if (sizeIsOk) {
                Kaga.PROFILE.sortie.lbasGroups.add(group)
            } else {
                Kaga.PROFILE.sortie.lbasGroups.remove(group)
            }
            if (updateList) list.setAll(items)
        }

        box1.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            update(true)
        }

        box2.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            update(true)
        }
        update(false)
    }
}
