package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.config.KancolleAutoProfile.CombatFormation
import com.waicool20.kaga.util.*
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty


class FormationChooserView : SingleListView<String>() {

    init {
        title = "KAGA - Formations Chooser"
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.setWidthRatio(tableView(), 0.25)

        val selections = if (Kaga.PROFILE!!.sortie.combinedFleet) {
            CombatFormation.values().map { it.prettyString }
        } else {
            CombatFormation.values().map({ it.prettyString }).filterNot { it.contains("combined", true) }
        }
        val formationColumn = OptionsColumn("Formation", selections, tableView())
        formationColumn.setWidthRatio(tableView(), 0.75)
        formationColumn.isSortable = false
        formationColumn.setCellValueFactory { data -> SimpleStringProperty(data.value) }

        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(nodeNumColumn, formationColumn)
        tableView().items.addAll(Kaga.PROFILE!!.sortie.formations.map { it.prettyString })
    }

    override fun onSaveButton() {
        with(tableView().items) {
            Kaga.PROFILE!!.sortie.formations.setAll(subList(0, size - 1).map { CombatFormation.fromPrettyString(it) })
        }
        close()
    }
}
