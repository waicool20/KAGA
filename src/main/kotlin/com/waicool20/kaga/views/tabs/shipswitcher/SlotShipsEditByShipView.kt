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

package com.waicool20.kaga.views.tabs.shipswitcher

import com.waicool20.kaga.config.ShipSpecification
import com.waicool20.kaga.config.ShipSpecificationByShip
import tornadofx.*

class SlotShipsEditByShipView : SlotShipsEditByShipClassView<String>() {
    override val spec = super.spec as ShipSpecificationByShip

    init {
        commonLabel.text = "Select Ship:"
        // TODO Scan /kcauto-kai.sikuli/shipswitcher.sikuli for assets and add it to items
        commonComboBox.items.addAll(ShipSpecificationByShip.Submarines.values().map { it.prettyString })
        commonComboBox.selectionModel.select(commonComboBox.items.find { it.equals(spec.ship, true) })
        spec.level?.let { levelSpinner.valueFactory.value = it }
        toggleMap[spec.levelCriteria]?.isSelected = true
        lockComboBox.selectionModel.select(spec.lockCriteria)
        ringComboBox.selectionModel.select(spec.ringCriteria)
    }

    override fun onSave() {
        super.onSave()
        val levelCriteria = levelFilterButton.toggleGroup.selectedToggle.userData as ShipSpecification.LevelCriteria
        val s = ShipSpecificationByShip(
                commonComboBox.selectedItem!!,
                levelCriteria,
                if (levelCriteria == ShipSpecification.LevelCriteria.NONE) null else levelSpinner.value,
                lockComboBox.selectedItem!!,
                ringComboBox.selectedItem!!)
        if (editModel.index != -1) {
            editModel.items.value[editModel.index] = s
        } else {
            editModel.items.value.add(s)
        }
    }
}
