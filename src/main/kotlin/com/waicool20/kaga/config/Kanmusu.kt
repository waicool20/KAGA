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

package com.waicool20.kaga.config

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

data class Kanmusu(
        val dropId: Int,
        val name: String,
        val level: Int,
        val shipClass: String,
        val fleet: FLEET
) {
    companion object {
        fun parseFromKc3ShipList(string: String) = Jsoup.parse(string)
                .selectFirst(".ship_list")?.select(".ship_item")?.map {
                    val nameNode = it.selectFirst(".ship_name")
                    val dropId = it.selectFirst(".ship_id").text().toInt()
                    val name = nameNode.text()
                    val level = it.selectFirst(".ship_lv").selectFirst(".value").text().toInt()
                    val shipClass = it.selectFirst(".ship_type").text()
                    val fleet = determineFleet(nameNode)
                    Kanmusu(dropId, name, level, shipClass, fleet)
                }?.sortedBy { it.dropId }?.toList() ?: emptyList()

        private fun determineFleet(element: Element) = when {
            element.hasClass("ship_onfleet-color1") -> Kanmusu.FLEET.FLEET1
            element.hasClass("ship_onfleet-color2") -> Kanmusu.FLEET.FLEET2
            element.hasClass("ship_onfleet-color3") -> Kanmusu.FLEET.FLEET3
            element.hasClass("ship_onfleet-color4") -> Kanmusu.FLEET.FLEET4
            else -> Kanmusu.FLEET.NONE
        }
    }

    enum class FLEET {
        FLEET1, FLEET2, FLEET3, FLEET4, NONE;
    }
}
