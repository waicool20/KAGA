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

sealed class ShipSpecification {
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
        private val shipRegex = Regex("S:(.+?):([><_])(\\d{0,2}):([_LN]):([_RN])")
        private val classRegex = Regex("C:(.+?):([><_])(\\d{0,2}):([_LN]):([_RN])")
        val values = mapOf(
                ShipSpecificationByPosition::class to "Position",
                ShipSpecificationByShip::class to "Ship/Custom",
                ShipSpecificationByClass::class to "Class"
        )

        fun parse(string: String): ShipSpecification {
            return when {
                string.matches(positionRegex) -> {
                    positionRegex.matchEntire(string)!!.destructured.let { (sortCriteriaString, orderString, offset) ->
                        val criteria = ShipSpecificationByPosition.SortCriteria.values().find { it.value == sortCriteriaString }!!
                        val order = ShipSpecificationByPosition.Order.values().find { it.value == orderString }!!
                        ShipSpecificationByPosition(criteria, order, offset.toInt())
                    }
                }
                string.matches(shipRegex) -> {
                    shipRegex.matchEntire(string)!!.destructured.let { (ship, levelCriteriaString, level, lockCriteriaString, ringCriteriaString) ->
                        val levelCriteria = LevelCriteria.values().find { it.value == levelCriteriaString }!!
                        val lockCriteria = LockCriteria.values().find { it.value == lockCriteriaString }!!
                        val ringCriteria = RingCriteria.values().find { it.value == ringCriteriaString }!!
                        ShipSpecificationByShip(ship, levelCriteria, level.toIntOrNull(), lockCriteria, ringCriteria)
                    }
                }
                string.matches(classRegex) -> {
                    classRegex.matchEntire(string)!!.destructured.let { (shipClassString, levelCriteriaString, level, lockCriteriaString, ringCriteriaString) ->
                        val shipClass = ShipSpecificationByClass.ShipClass.valueOf(shipClassString)
                        val levelCriteria = LevelCriteria.values().find { it.value == levelCriteriaString }!!
                        val lockCriteria = LockCriteria.values().find { it.value == lockCriteriaString }!!
                        val ringCriteria = RingCriteria.values().find { it.value == ringCriteriaString }!!
                        ShipSpecificationByClass(shipClass, levelCriteria, level.toIntOrNull(), lockCriteria, ringCriteria)
                    }
                }
                else -> kotlin.error("Could not parse ship specification: $string")
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

data class ShipSpecificationByShip(
        var ship: String = "U-511",
        var levelCriteria: LevelCriteria = LevelCriteria.NONE,
        var level: Int? = null,
        var lockCriteria: LockCriteria = LockCriteria.IGNORE_LOCKS,
        var ringCriteria: RingCriteria = RingCriteria.IGNORE_RINGS
) : ShipSpecification() {
    enum class Submarines(val prettyString: String, val isSSV: Boolean) {
        I_8("I-8", false), I_8_KAI("I-8 Kai", true),
        I_13("I-13", true), I_14("I-14", true),
        I_19("I-19", false), I_19_KAI("I-19 Kai", true),
        I_26("I-26", false), I_26_KAI("I-26 Kai", true),
        I_58("I-58", false), I_58_KAI("I-58 Kai", true),
        I_168("I-168", false), I_401("I-401", true),
        MARUYU("Maruyu", false), RO_500("Ro-500", false),
        U_511("U-511", false), LUIGI("Luigi", false),
        UIT_25("UIT-25", false), I_504("I-504", false);

        override fun toString() = prettyString.toLowerCase().replace(" ", "-")
    }

    override fun asConfigString() = "S:$ship:${levelCriteria.value}${level ?: ""}:${lockCriteria.value}:${ringCriteria.value}"
}

data class ShipSpecificationByClass(
        var shipClass: ShipClass = ShipClass.SS,
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

    override fun asConfigString() = "C:$shipClass:${levelCriteria.value}${level ?: ""}:${lockCriteria.value}:${ringCriteria.value}"
}



