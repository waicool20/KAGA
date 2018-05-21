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

import com.waicool20.kaga.config.KancolleAutoProfile.ScheduledStopMode
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Spinner
import javafx.scene.layout.HBox

class StopTabView {
    @FXML private lateinit var enableScriptStopCheckBox: CheckBox
    @FXML private lateinit var scriptStopCountSpinner: Spinner<Int>
    @FXML private lateinit var scriptStopTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var scriptStopTimeMinSpinner: Spinner<Int>

    @FXML private lateinit var enableExpStopCheckBox: CheckBox
    @FXML private lateinit var expStopModeComboBox: ComboBox<ScheduledStopMode>
    @FXML private lateinit var expStopCountSpinner: Spinner<Int>
    @FXML private lateinit var expStopTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var expStopTimeMinSpinner: Spinner<Int>

    @FXML private lateinit var enableSortieStopCheckBox: CheckBox
    @FXML private lateinit var sortieStopModeComboBox: ComboBox<ScheduledStopMode>
    @FXML private lateinit var sortieStopCountSpinner: Spinner<Int>
    @FXML private lateinit var sortieStopTimeHourSpinner: Spinner<Int>
    @FXML private lateinit var sortieStopTimeMinSpinner: Spinner<Int>

    @FXML private lateinit var scriptStopContent: HBox
    @FXML private lateinit var expStopContent: HBox
    @FXML private lateinit var sortieStopContent: HBox

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {

    }

    private fun createBindings() {

    }
}
