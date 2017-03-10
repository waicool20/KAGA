package com.waicool20.kaga.views.tabs

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.bind
import javafx.fxml.FXML
import javafx.scene.control.*
import tornadofx.*

class GeneralTabView {
    @FXML private lateinit var programTextField: TextField
    @FXML private lateinit var recoveryMethodChoiceBox: ChoiceBox<KancolleAutoProfile.RecoveryMethod>
    @FXML private lateinit var basicRecoveryCheckBox: CheckBox
    @FXML private lateinit var paranoiaSpinner: Spinner<Int>
    @FXML private lateinit var sleepCycleSpinner: Spinner<Int>
    @FXML private lateinit var sleepModifierSpinner: Spinner<Int>

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    fun setValues() {
        recoveryMethodChoiceBox.items.setAll(*KancolleAutoProfile.RecoveryMethod.values())
        paranoiaSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        sleepCycleSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
        sleepModifierSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
    }

    fun createBindings() {
        with(Kaga.PROFILE!!.general) {
            recoveryMethodChoiceBox.bind(recoveryMethodProperty)
            programTextField.bind(programProperty)
            basicRecoveryCheckBox.bind(basicRecoveryProperty)
            paranoiaSpinner.bind(paranoiaProperty)
            sleepCycleSpinner.bind(sleepCycleProperty)
            sleepModifierSpinner.bind(sleepModifierProperty)
        }
    }
}
