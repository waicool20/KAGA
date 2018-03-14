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
import javafx.fxml.FXML
import javafx.scene.control.TextField
import tornadofx.*

class GeneralTabView {
    @FXML private lateinit var programTextField: TextField
    /* TODO Disabled temporarily till kcauto-kai is finalized
    @FXML private lateinit var recoveryMethodChoiceBox: ChoiceBox<KancolleAutoProfile.RecoveryMethod>
    @FXML private lateinit var basicRecoveryCheckBox: CheckBox
    @FXML private lateinit var paranoiaSpinner: Spinner<Int>
    @FXML private lateinit var sleepCycleSpinner: Spinner<Int>
    @FXML private lateinit var sleepModifierSpinner: Spinner<Int>*/

    @FXML
    fun initialize() {
        setValues()
        createBindings()
    }

    fun setValues() {
        /* TODO Disabled temporarily till kcauto-kai is finalized
        recoveryMethodChoiceBox.items.setAll(*KancolleAutoProfile.RecoveryMethod.values())
        paranoiaSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        sleepCycleSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        sleepModifierSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)*/
    }

    fun createBindings() {
        with(Kaga.PROFILE.general) {
            programTextField.bind(programProperty)
            /* TODO Disabled temporarily till kcauto-kai is finalized
            recoveryMethodChoiceBox.bind(recoveryMethodProperty)
            basicRecoveryCheckBox.bind(basicRecoveryProperty)
            paranoiaSpinner.bind(paranoiaProperty)
            sleepCycleSpinner.bind(sleepCycleProperty)
            sleepModifierSpinner.bind(sleepModifierProperty)*/
        }
    }
}
