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
import com.waicool20.kaga.config.KancolleAutoProfile.EventDifficulty
import com.waicool20.waicoolutils.javafx.addListener
import com.waicool20.waicoolutils.javafx.bind
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.VBox
import tornadofx.*

class EventResetTabView {
    @FXML private lateinit var root: VBox
    @FXML private lateinit var content: VBox
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var frequencySpinner: Spinner<Int>
    @FXML private lateinit var farmDifficultyComboBox: ComboBox<EventDifficulty>
    @FXML private lateinit var resetDifficultyComboBox: ComboBox<EventDifficulty>

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        frequencySpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5)
        farmDifficultyComboBox.items.setAll(EventDifficulty.values().toList())
        resetDifficultyComboBox.items.setAll(EventDifficulty.values().toList())
        root.sceneProperty().addListener("EventResetSceneListener") { newScene ->
            newScene.window.setOnShown { updateRoot() }
        }
    }

    private fun createBindings() {
        Kaga.PROFILE.eventReset.apply {
            enableButton.bind(enabledProperty)
            frequencySpinner.bind(frequencyProperty)
            farmDifficultyComboBox.bind(farmDifficultyProperty)
            resetDifficultyComboBox.bind(resetDifficultyProperty)
        }

        Kaga.PROFILE.sortie.mapProperty.addListener("EventResetMapListener") { _ ->
            updateRoot()
        }

        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }

    private fun updateRoot() {
        if (Kaga.PROFILE.sortie.map.startsWith("E")) {
            root.isDisable = false
        } else {
            enableButton.isSelected = false
            root.isDisable = true
        }
    }
}