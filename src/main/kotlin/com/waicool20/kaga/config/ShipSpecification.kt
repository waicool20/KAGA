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

import com.waicool20.waicoolutils.logging.loggerFor
import kotlin.concurrent.thread

sealed class ShipSpecification {
    private val logger = loggerFor<ShipSpecification>()

    enum class LockCriteria(val value: String) {
        IGNORE_LOCKS("_"),
        ONLY_LOCKED("L"),
        ONLY_UNLOCKED("N")
    }

    enum class RingCriteria(val value: String) {
        IGNORE_RINGS("_"),
        ONLY_RINGED("R"),
        ONLY_NOT_RINGED("N")
    }

    enum class LevelCriteria(val value: String) {
        NONE("_"),
        GREATHER_THAN(">"),
        LESS_THAN("<")
    }

    companion object {
        private val positionRegex = Regex("P:([NCL]):([SE]):(\\d{1,3})")
        private val assetRegex = Regex("A:(.+?):([><_])(\\d{0,3}):([_LN]):([_RN])")
        val values = mapOf(
                ShipSpecificationByPosition::class to "Position",
                ShipSpecificationByAsset::class to "Asset"
        )

        fun parse(string: String): ShipSpecification? {
            return when {
                string.matches(positionRegex) -> {
                    positionRegex.matchEntire(string)!!.destructured.let { (sortCriteriaString, orderString, offset) ->
                        val criteria = ShipSpecificationByPosition.SortCriteria.values().find { it.value == sortCriteriaString }!!
                        val order = ShipSpecificationByPosition.Order.values().find { it.value == orderString }!!
                        ShipSpecificationByPosition(criteria, order, offset.toInt())
                    }
                }
                string.matches(assetRegex) -> {
                    assetRegex.matchEntire(string)!!.destructured.let { (asset, levelCriteriaString, level, lockCriteriaString, ringCriteriaString) ->
                        val levelCriteria = LevelCriteria.values().find { it.value == levelCriteriaString }!!
                        val lockCriteria = LockCriteria.values().find { it.value == lockCriteriaString }!!
                        val ringCriteria = RingCriteria.values().find { it.value == ringCriteriaString }!!
                        ShipSpecificationByAsset(asset, levelCriteria, level.toIntOrNull(), lockCriteria, ringCriteria)
                    }
                }
                else -> {
                    thread { kotlin.error("Could not parse ship specification: $string, it will be ignored") }
                    null
                }
            }
        }
    }

    abstract fun asConfigString(): String
    override fun toString() = asConfigString()
}

data class ShipSpecificationByPosition(
        var sortBy: SortCriteria = SortCriteria.DATE_ACQUIRED,
        var order: Order = Order.START_OF_LIST,
        var offset: Int = 1

) : ShipSpecification() {
    enum class SortCriteria(val value: String) {
        DATE_ACQUIRED("N"),
        CLASS("C"),
        LEVEL("L")
    }

    enum class Order(val value: String) {
        START_OF_LIST("S"),
        END_OF_LIST("E")
    }

    override fun asConfigString() = "P:${sortBy.value}:${order.value}:$offset"
}

data class ShipSpecificationByAsset(
        var asset: String = "SS",
        var levelCriteria: LevelCriteria = LevelCriteria.NONE,
        var level: Int? = null,
        var lockCriteria: LockCriteria = LockCriteria.IGNORE_LOCKS,
        var ringCriteria: RingCriteria = RingCriteria.IGNORE_RINGS
) : ShipSpecification() {
    enum class ShipClass {
        AO, AR, AS, AV, BB,
        BBV, CA, CAV, CL, CLT,
        CT, CV, CVB, CVL, DD,
        DE, LHA, SS, SSV
    }

    override fun asConfigString() = "A:$asset:${levelCriteria.value}${level
            ?: ""}:${lockCriteria.value}:${ringCriteria.value}"
}



