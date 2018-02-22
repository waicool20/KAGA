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
import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.layout.GridPane
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox
import tornadofx.*


class ExpeditionsTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var fleet2CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var fleet3CheckComboBox: CheckComboBox<String>
    @FXML private lateinit var fleet4CheckComboBox: CheckComboBox<String>

    @FXML private lateinit var content: GridPane

    private val specialExpediions = mapOf(
            "9998" to "Pre-Boss Node Support",
            "9999" to "Boss Node Support"
    )

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        (1..41).map(Int::toString).plus(specialExpediions.keys).let {
            fleet2CheckComboBox.items.addAll(it)
            fleet3CheckComboBox.items.addAll(it)
            fleet4CheckComboBox.items.addAll(it)
        }
        val converter = object : StringConverter<String>() {
            override fun toString(string: String?): String {
                return specialExpediions.getOrElse(string ?: "") { string ?: "" }
            }

            override fun fromString(string: String?): String = ""
        }
        fleet2CheckComboBox.converter = converter
        fleet3CheckComboBox.converter = converter
        fleet4CheckComboBox.converter = converter
        with(Kaga.PROFILE.expeditions) {
            fleet2.forEach { exp -> fleet2CheckComboBox.checkModel.let { if (!it.isChecked(exp)) it.check(exp) } }
            fleet3.forEach { exp -> fleet3CheckComboBox.checkModel.let { if (!it.isChecked(exp)) it.check(exp) } }
            fleet4.forEach { exp -> fleet4CheckComboBox.checkModel.let { if (!it.isChecked(exp)) it.check(exp) } }
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE.expeditions) {
            enableButton.bind(enabledProperty)
            fleet2CheckComboBox.checkModel.checkedItems.addListener { change: ListChangeListener.Change<out String> ->
                fleet2.setAll(change.list.distinct())
            }
            fleet3CheckComboBox.checkModel.checkedItems.addListener { change: ListChangeListener.Change<out String> ->
                fleet3.setAll(change.list.distinct())
            }
            fleet4CheckComboBox.checkModel.checkedItems.addListener { change: ListChangeListener.Change<out String> ->
                fleet4.setAll(change.list.distinct())
            }
        }
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }
}
