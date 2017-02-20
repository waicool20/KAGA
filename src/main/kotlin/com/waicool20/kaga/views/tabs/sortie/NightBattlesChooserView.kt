package com.waicool20.kaga.views.tabs.sortie

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.*
import com.waicool20.kaga.views.SingleListView
import javafx.beans.property.SimpleStringProperty


class NightBattlesChooserView : SingleListView<String>() {

    init {
        title = "KAGA - Night Battle Chooser"
        val nodeNumColumn = IndexColumn<String>("Node", 1)
        nodeNumColumn.setWidthRatio(tableView(), 0.25)

        val nightBattleColumn = OptionsColumn("Night Battle?", listOf("Yes", "No"), tableView())
        nightBattleColumn.setWidthRatio(tableView(), 0.75)
        nightBattleColumn.isSortable = false
        nightBattleColumn.setCellValueFactory { data -> SimpleStringProperty(data.value) }

        tableView().lockColumnWidths()
        tableView().disableHeaderMoving()
        tableView().columns.addAll(nodeNumColumn, nightBattleColumn)
        tableView().items.addAll(Kaga.PROFILE!!.sortie.nightBattles.map { if (it) "Yes" else "No" })
    }

    override fun onSaveButton() {
        with(tableView().items) {
            Kaga.PROFILE!!.sortie.nightBattles.setAll(subList(0, size - 1).map { it == "Yes" })
        }
        close()
    }
}
