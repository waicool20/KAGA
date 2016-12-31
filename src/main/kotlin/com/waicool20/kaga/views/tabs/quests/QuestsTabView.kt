package com.waicool20.kaga.views.tabs.quests

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.bind
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.GridPane
import tornadofx.bind
import tornadofx.find


class QuestsTabView {
    @FXML private lateinit var enableButton: CheckBox
    @FXML private lateinit var checkScheduleSpinner: Spinner<Int>
    @FXML private lateinit var content: GridPane

    @FXML fun initialize() {
        setValues()
        createBindings()
    }

    private fun setValues() {
        checkScheduleSpinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
    }

    private fun createBindings() {
        with(Kaga.PROFILE!!.quests) {
            enableButton.bind(enabledProperty)
            checkScheduleSpinner.bind(checkScheduleProperty)
        }
        content.disableProperty().bind(Bindings.not(enableButton.selectedProperty()))
    }

    @FXML private fun onConfigureQuestsButton() {
        find(QuestsChooserView::class).openModal(owner = Kaga.ROOT_STAGE.owner)
    }
}
