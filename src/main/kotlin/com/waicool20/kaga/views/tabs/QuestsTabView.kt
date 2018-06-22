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
import com.waicool20.kaga.config.KancolleAutoProfile.QuestGroups
import com.waicool20.waicoolutils.controlsfx.bind
import com.waicool20.waicoolutils.controlsfx.checkAll
import com.waicool20.waicoolutils.javafx.addListener
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox
import tornadofx.*


class QuestsTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var content: VBox
    @FXML private lateinit var questBox: CheckComboBox<QuestGroups>

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        questBox.items.setAll(QuestGroups.values().toList())
        questBox.converter = object : StringConverter<QuestGroups>() {
            override fun toString(engine: QuestGroups) = engine.prettyString
            override fun fromString(string: String) = QuestGroups.fromPrettyString(string)
        }
    }

    private fun createBindings() {
        with(Kaga.PROFILE.quests) {
            enableButton.bind(enabledProperty)
            enableButton.selectedProperty().addListener("QuestsEnableListener") { newVal ->
                if (newVal && questBox.checkModel.checkedItems.isEmpty()) {
                    questBox.checkModel.checkAll(QuestGroups.values().toList())
                }
            }

            if (enabled && questGroups.isEmpty()) questGroups.addAll(QuestGroups.values().toList())

            questBox.bind(questGroupsProperty)
            questBox.checkModel.checkedItems.addListener("QuestGroupsListener") { change ->
                if (change.list.isEmpty()) enableButton.isSelected = false
            }
        }
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }
}
