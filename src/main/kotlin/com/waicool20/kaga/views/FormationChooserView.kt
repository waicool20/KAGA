package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile
import com.waicool20.kaga.util.DeselectableCellFactory
import tornadofx.selectedItem


class FormationChooserView : ListChooser() {

    init {
        title = "KAGA - Formations Chooser"
        with(Kaga.PROFILE!!.sortie) {
            val selections = KancolleAutoProfile.CombatFormation.values().map { formation -> formation.toString() }
            leftListView.items.setAll(
                    if (combinedFleet) {
                        selections
                    } else {
                        selections.filter { formation -> !formation.contains("combined".toRegex(RegexOption.IGNORE_CASE)) }
                    })
            rightListView.items.setAll(formations.map { formation -> formation.toString() })
            rightListView.cellFactory = DeselectableCellFactory<String>()
        }
    }

    override fun toRightButton() {
        with(rightListView) {
            if (leftListView.selectedItem != null) {
                val index = selectionModel.selectedIndex
                items.add(if (index > -1) index else items.size, leftListView.selectedItem)
            }
        }
    }

    override fun toLeftButton() {
        with(rightListView) {
            if (selectedItem != null) items.removeAt(selectionModel.selectedIndex)
        }
    }

    override fun onSaveButton() {
        Kaga.PROFILE!!.sortie.formations.setAll(rightListView.items.map { value ->
            KancolleAutoProfile.CombatFormation.values().find {
                it.toString().equals(value, true)
            }
        })
        closeModal()
    }

    override fun onCancelButton() {
        closeModal()
    }
}
