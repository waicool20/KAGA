package com.waicool20.kaga.views

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.containsIgnoreCase
import com.waicool20.kaga.util.getIgnoreCase
import tornadofx.selectedItem
import java.io.BufferedReader
import java.io.InputStreamReader

class QuestsChooserView : ListChooser() {
    val questsMap = run {
        val stream = Kaga::class.java.classLoader.getResourceAsStream("valid_quests.txt")
        BufferedReader(InputStreamReader(stream)).readLines().mapIndexed({ index, string -> string to index }).toMap()
    }


    init {
        title = "Kaga - Quests Chooser"
        with(Kaga.PROFILE!!.quests) {
            leftListView.items.setAll(questsMap.keys.filter { string -> !quests.containsIgnoreCase(string) })
            rightListView.items.setAll(quests
                    .map({ string -> questsMap.keys.getIgnoreCase(string) })
                    .sortedBy({ string -> questsMap[string] })
            )
        }
    }

    override fun toRightButton() {
        with(leftListView) {
            rightListView.items.add(selectedItem)
            rightListView.items.sortBy { string -> questsMap[string] }
            items.removeAt(selectionModel.selectedIndex)
        }
    }

    override fun toLeftButton() {
        with(rightListView) {
            leftListView.items.add(selectedItem)
            leftListView.items.sortBy { string -> questsMap[string] }
            items.removeAt(selectionModel.selectedIndex)
        }

    }

    override fun onSaveButton() {
        Kaga.PROFILE!!.quests.quests.setAll(rightListView.items)
        closeModal()
    }

    override fun onCancelButton() {
        closeModal()
    }

}
