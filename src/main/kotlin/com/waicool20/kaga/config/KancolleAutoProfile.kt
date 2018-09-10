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

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.IniConfig
import com.waicool20.kaga.util.fromObject
import com.waicool20.kaga.util.toObject
import com.waicool20.waicoolutils.javafx.json.fxJacksonObjectMapper
import com.waicool20.waicoolutils.javafx.toProperty
import org.ini4j.Wini
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.concurrent.thread

data class KancolleAutoProfile(
        val general: General = General(),
        val scheduledSleep: ScheduledSleep = ScheduledSleep(),
        val scheduledStop: ScheduledStop = ScheduledStop(),
        val expeditions: Expeditions = Expeditions(),
        val pvp: Pvp = Pvp(),
        val sortie: Sortie = Sortie(),
        val eventReset: EventReset = EventReset(),
        val shipSwitcher: ShipSwitcher = ShipSwitcher(),
        val quests: Quests = Quests()
) {
    constructor(
            name: String,
            general: General = General(),
            scheduledSleep: ScheduledSleep = ScheduledSleep(),
            scheduledStop: ScheduledStop = ScheduledStop(),
            expeditions: Expeditions = Expeditions(),
            pvp: Pvp = Pvp(),
            sortie: Sortie = Sortie(),
            eventReset: EventReset,
            shipSwitcher: ShipSwitcher = ShipSwitcher(),
            quests: Quests = Quests()
    ) : this(general, scheduledSleep, scheduledStop, expeditions, pvp, sortie, eventReset, shipSwitcher, quests) {
        this.name = name
    }

    init {
        general.pauseProperty.addListener { _, _, newVal ->
            thread {
                val text = path().toFile().readText().replace(Regex("Pause.+"), "Pause = ${newVal.toString().capitalize()}").toByteArray()
                Files.write(path(), text, StandardOpenOption.TRUNCATE_EXISTING)
            }
        }
    }

    private val logger = LoggerFactory.getLogger(KancolleAutoProfile::class.java)
    var nameProperty = KancolleAutoProfile.DEFAULT_NAME.toProperty()
    var name by nameProperty

    fun path(): Path = Kaga.CONFIG_DIR.resolve("$name-config.ini")

    fun save(path: Path = path()) {
        logger.info("Saving KancolleAuto profile")
        logger.debug("Saving to $path")
        if (Files.notExists(path)) {
            logger.debug("Profile not found, creating file $path")
            Files.createDirectories(path.parent)
            Files.createFile(path)
        }
        val config = asIniString().replace("true", "True").replace("false", "False")
        val header = "# Configuration automatically generated by KAGA\n"
        Files.write(path, (header + config).toByteArray(), StandardOpenOption.TRUNCATE_EXISTING)
        logger.info("Saving KancolleAuto profile was successful")
        logger.debug("Saved $this to $path")
    }

    fun asIniString() = StringWriter().also { getIni().store(it) }.toString()

    private fun getIni() = Wini().apply {
        add("General").fromObject(general)
        add("ScheduledSleep").fromObject(scheduledSleep)
        add("ScheduledStop").fromObject(scheduledStop)
        add("Expeditions").fromObject(expeditions)
        add("PvP").fromObject(pvp)
        add("Combat").fromObject(sortie)
        add("EventReset").fromObject(eventReset)
        add("ShipSwitcher").fromObject(shipSwitcher)
        add("Quests").fromObject(quests)
    }

    fun delete(): Boolean {
        with(path()) {
            return if (Files.exists(this)) {
                Files.delete(this)
                logger.info("Deleted profile")
                logger.debug("Deleted ${this@KancolleAutoProfile} from $this")
                true
            } else {
                logger.warn("File doesn't exist, can't delete!")
                logger.debug("Couldn't delete $this")
                false
            }
        }
    }

    companion object Loader {
        private val loaderLogger = LoggerFactory.getLogger(KancolleAutoProfile.Loader::class.java)
        const val DEFAULT_NAME = "[Current Profile]"
        val VALID_NODES = (1..12).map { it.toString() }
                .plus("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("").filter { it.isNotEmpty() })
                .plus(listOf("Z1", "Z2", "Z3", "Z4", "Z5", "Z6", "Z7", "Z8", "Z9", "ZZ1", "ZZ2", "ZZ3"))
                .toProperty()

        fun load(path: Path = Kaga.CONFIG.kcaRootDirPath.resolve("config.ini")): KancolleAutoProfile {
            if (Files.exists(path)) {
                loaderLogger.info("Attempting to load KancolleAuto Profile")
                loaderLogger.debug("Loading KancolleAuto Profile from $path")
                val name = Regex("(.+?)-config\\.ini").matchEntire("${path.fileName}")?.groupValues?.get(1)
                        ?: run {
                            val backupPath = (0..999).asSequence()
                                    .map { "config.ini.bak$it" }
                                    .map { path.resolveSibling(it) }
                                    .first { Files.notExists(it) }
                            loaderLogger.info("Copied backup of existing configuration to $backupPath")
                            Files.copy(path, backupPath)
                            DEFAULT_NAME
                        }
                val ini = Wini(path.toFile())

                return KancolleAutoProfile(
                        name,
                        general = loadSection(ini),
                        scheduledSleep = loadSection(ini),
                        scheduledStop = loadSection(ini),
                        expeditions = loadSection(ini),
                        pvp = loadSection(ini, "PvP"),
                        sortie = loadSection(ini, "Combat"),
                        eventReset = loadSection(ini),
                        shipSwitcher = loadSection(ini),
                        quests = loadSection(ini)
                ).apply {
                    loaderLogger.info("Loading KancolleAuto profile was successful")
                    loaderLogger.debug("Loaded $this")
                }
            } else {
                loaderLogger.debug("Config at $path not found, falling back to config.ini in kancolle-auto root")
                check(Kaga.CONFIG.isValid())
                return load()
            }
        }

        private inline fun <reified T> loadSection(ini: Wini, section: String? = null): T {
            val name = T::class.simpleName
            return ini[section ?: name]?.toObject() ?: run {
                runLater { error("Could not parse $name section! Using defaults for it!") }
            }.let { T::class.java.newInstance() }
        }
    }

    enum class RecoveryMethod { BROWSER, KC3, KCV, KCT, EO, NONE }

    enum class ScheduledStopMode {
        MODULE, SCRIPT;

        val prettyString = name.toLowerCase().capitalize()

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = name.toLowerCase()
    }

    enum class CombatFormation(val prettyString: String) {
        LINE_AHEAD("Line Ahead"), DOUBLE_LINE("Double Line"), DIAMOND("Diamond"),
        ECHELON("Echelon"), LINE_ABREAST("Line Abreast"), VANGUARD("Vanguard"),
        COMBINEDFLEET_1("Cruising Formation 1 (Anti-Sub)"), COMBINEDFLEET_2("Cruising Formation 2 (Forward)"),
        COMBINEDFLEET_3("Cruising Formation 3 (Ring)"), COMBINEDFLEET_4("Cruising Formation 4 (Battle)");

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString(): String = name.toLowerCase()
    }

    enum class Engine(val prettyString: String) {
        LEGACY("Legacy"), LIVE("Live / Dynamic");

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = name.toLowerCase()
    }

    enum class FleetMode(val prettyString: String, val value: String) {
        STANDARD("Standard", ""),
        CTF("Carrier Task Force", "ctf"),
        STF("Strike Task Force", "stf"),
        TRANSPORT("Transport Escort", "transport"),
        STRIKING("Striking Fleet", "striking");

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = value
    }

    enum class DamageLevel(val prettyString: String, val value: String) {
        LIGHT("Light Damage", "minor"),
        MODERATE("Moderate Damage", "moderate"),
        CRITICAL("Critical Damage", "heavy");

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = value
    }

    enum class SortieOptions(val value: String) {
        CHECK_FATIGUE("CheckFatigue"),
        RESERVE_DOCKS("ReserveDocks"),
        PORT_CHECK("PortCheck"),
        CLEAR_STOP("ClearStop");

        override fun toString() = value
    }

    enum class EventDifficulty {
        CASUAL, EASY, MEDIUM, HARD;

        val prettyString = name.toLowerCase().capitalize()

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = name.toLowerCase()
    }

    enum class SwitchCriteria {
        FATIGUE, DAMAGE, SPARKLE;

        val prettyString = name.toLowerCase().capitalize()

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = name.toLowerCase()
    }

    enum class QuestGroups {
        DAILY, WEEKLY, MONTHLY, OTHERS;

        val prettyString = name.toLowerCase().capitalize()

        companion object {
            fun fromPrettyString(string: String) = values().first { it.prettyString.equals(string, true) }
        }

        override fun toString() = name.toLowerCase()
    }

    class General(
            program: String = "Chrome",
            pause: Boolean = false
    ) {
        @IniConfig(key = "Program")
        val programProperty = program.toProperty()
        @IniConfig(key = "JSTOffset", shouldRead = false)
        val jstOffsetProperty = ((TimeZone.getDefault().rawOffset - TimeZone.getTimeZone("Japan").rawOffset) / 3600000).toProperty()
        @IniConfig(key = "Pause")
        val pauseProperty = pause.toProperty()

        var program by programProperty
        var jstOffset by jstOffsetProperty
        var pause by pauseProperty
    }

    class ScheduledSleep(
            scriptSleepEnabled: Boolean = false,
            scriptSleepStartTime: String = "0030",
            scriptSleepLength: Double = 3.5,
            expSleepEnabled: Boolean = false,
            expSleepStartTime: String = "0030",
            expSleepLength: Double = 3.5,
            sortieSleepEnabled: Boolean = false,
            sortieSleepStartTime: String = "0030",
            sortieSleepLength: Double = 3.5
    ) {
        @IniConfig(key = "ScriptSleepEnabled")
        val scriptSleepEnabledProperty = scriptSleepEnabled.toProperty()
        @IniConfig(key = "ScriptSleepStartTime")
        val scriptSleepStartTimeProperty = scriptSleepStartTime.toProperty()
        @IniConfig(key = "ScriptSleepLength")
        val scriptSleepLengthProperty = scriptSleepLength.toProperty()

        @IniConfig(key = "ExpeditionSleepEnabled")
        val expSleepEnabledProperty = expSleepEnabled.toProperty()
        @IniConfig(key = "ExpeditionSleepStartTime")
        val expSleepStartTimeProperty = expSleepStartTime.toProperty()
        @IniConfig(key = "ExpeditionSleepLength")
        val expSleepLengthProperty = expSleepLength.toProperty()

        @IniConfig(key = "CombatSleepEnabled")
        val sortieSleepEnabledProperty = sortieSleepEnabled.toProperty()
        @IniConfig(key = "CombatSleepStartTime")
        val sortieSleepStartTimeProperty = sortieSleepStartTime.toProperty()
        @IniConfig(key = "CombatSleepLength")
        val sortieSleepLengthProperty = sortieSleepLength.toProperty()

        var scriptSleepEnabled by scriptSleepEnabledProperty
        var scriptSleepStartTime by scriptSleepStartTimeProperty
        var scriptSleepLength by scriptSleepLengthProperty

        var expSleepEnabled by expSleepEnabledProperty
        var expSleepStartTime by expSleepStartTimeProperty
        var expSleepLength by expSleepLengthProperty

        var sortieSleepEnabled by sortieSleepEnabledProperty
        var sortieSleepStartTime by sortieSleepStartTimeProperty
        var sortieSleepLength by sortieSleepLengthProperty
    }

    class ScheduledStop(
            scriptStopEnabled: Boolean = false,
            scriptStopCount: String = "",
            scriptStopTime: String = "",
            expStopEnabled: Boolean = false,
            expStopMode: ScheduledStopMode = ScheduledStopMode.MODULE,
            expStopCount: String = "",
            expStopTime: String = "",
            sortieStopEnabled: Boolean = false,
            sortieStopMode: ScheduledStopMode = ScheduledStopMode.MODULE,
            sortieStopCount: String = "",
            sortieStopTime: String = ""
    ) {
        @IniConfig(key = "ScriptStopEnabled")
        val scriptStopEnabledProperty = scriptStopEnabled.toProperty()
        @IniConfig(key = "ScriptStopCount")
        val scriptStopCountProperty = scriptStopCount.toProperty()
        @IniConfig(key = "ScriptStopTime")
        val scriptStopTimeProperty = scriptStopTime.toProperty()

        @IniConfig(key = "ExpeditionStopEnabled")
        val expStopEnabledProperty = expStopEnabled.toProperty()
        @IniConfig(key = "ExpeditionStopMode")
        val expStopModeProperty = expStopMode.toProperty()
        @IniConfig(key = "ExpeditionStopCount")
        val expStopCountProperty = expStopCount.toProperty()
        @IniConfig(key = "ExpeditionStopTime")
        val expStopTimeProperty = expStopTime.toProperty()

        @IniConfig(key = "CombatStopEnabled")
        val sortieStopEnabledProperty = sortieStopEnabled.toProperty()
        @IniConfig(key = "CombatStopMode")
        val sortieStopModeProperty = sortieStopMode.toProperty()
        @IniConfig(key = "CombatStopCount")
        val sortieStopCountProperty = sortieStopCount.toProperty()
        @IniConfig(key = "CombatStopTime")
        val sortieStopTimeProperty = sortieStopTime.toProperty()

        var scriptStopEnabled by scriptStopEnabledProperty
        var scriptStopCount by scriptStopCountProperty
        var scriptStopTime by scriptStopTimeProperty

        var expStopEnabled by expStopEnabledProperty
        var expStopMode by expStopModeProperty
        var expStopCount by expStopCountProperty
        var expStopTime by expStopTimeProperty

        var sortieStopEnabled by sortieStopEnabledProperty
        var sortieStopMode by sortieStopModeProperty
        var sortieStopCount by sortieStopCountProperty
        var sortieStopTime by sortieStopTimeProperty
    }

    class Expeditions(
            enabled: Boolean = true,
            fleet2: List<String> = mutableListOf("2"),
            fleet3: List<String> = mutableListOf("5"),
            fleet4: List<String> = mutableListOf("21")
    ) {
        @IniConfig(key = "Enabled")
        val enabledProperty = enabled.toProperty()
        @IniConfig(key = "Fleet2")
        val fleet2Property = fleet2.toProperty()
        @IniConfig(key = "Fleet3")
        val fleet3Property = fleet3.toProperty()
        @IniConfig(key = "Fleet4")
        val fleet4Property = fleet4.toProperty()

        var enabled by enabledProperty
        var fleet2 by fleet2Property
        var fleet3 by fleet3Property
        var fleet4 by fleet4Property
    }

    class Pvp(
            enabled: Boolean = false,
            fleet: String = ""
    ) {
        @IniConfig(key = "Enabled")
        val enabledProperty = enabled.toProperty()
        @IniConfig(key = "Fleet")
        val fleetProperty = fleet.toProperty()

        var enabled by enabledProperty
        var fleet by fleetProperty
    }

    class Sortie(
            enabled: Boolean = false,
            engine: Engine = Engine.LEGACY,
            map: String = "1-1",
            retreatNodes: List<String> = mutableListOf("1"),
            fleets: List<Int> = mutableListOf(),
            fleetMode: FleetMode = FleetMode.STANDARD,
            nodeSelects: List<String> = mutableListOf(),
            formations: List<String> = mutableListOf(),
            nightBattles: List<String> = mutableListOf(),
            retreatLimit: DamageLevel = DamageLevel.CRITICAL,
            repairLimit: DamageLevel = DamageLevel.MODERATE,
            repairTimeLimit: String = "0030",
            lbasGroups: Set<String> = mutableSetOf(),
            lbasGroup1Nodes: List<String> = mutableListOf(),
            lbasGroup2Nodes: List<String> = mutableListOf(),
            lbasGroup3Nodes: List<String> = mutableListOf(),
            miscOptions: Set<SortieOptions> = mutableSetOf()
    ) {
        @IniConfig(key = "Enabled")
        val enabledProperty = enabled.toProperty()
        @IniConfig(key = "Engine")
        val engineProperty = engine.toProperty()
        @IniConfig(key = "Map")
        val mapProperty = map.toProperty()
        @IniConfig(key = "RetreatNodes")
        val retreatNodesProperty = retreatNodes.toProperty()
        @IniConfig(key = "Fleets")
        val fleetsProperty = fleets.toProperty()
        @IniConfig(key = "FleetMode")
        val fleetModeProperty = fleetMode.toProperty()
        @IniConfig(key = "NodeSelects")
        val nodeSelectsProperty = nodeSelects.toProperty()
        @IniConfig(key = "Formations")
        val formationsProperty = formations.toProperty()
        @IniConfig(key = "NightBattles")
        val nightBattlesProperty = nightBattles.toProperty()
        @IniConfig(key = "RetreatLimit")
        val retreatLimitProperty = retreatLimit.toProperty()
        @IniConfig(key = "RepairLimit")
        val repairLimitProperty = repairLimit.toProperty()
        @IniConfig(key = "RepairTimeLimit")
        val repairTimeLimitProperty = repairTimeLimit.toProperty()
        @IniConfig(key = "LBASGroups")
        val lbasGroupsProperty = lbasGroups.toProperty()
        @IniConfig(key = "LBASGroup1Nodes")
        val lbasGroup1NodesProperty = lbasGroup1Nodes.toProperty()
        @IniConfig(key = "LBASGroup2Nodes")
        val lbasGroup2NodesProperty = lbasGroup2Nodes.toProperty()
        @IniConfig(key = "LBASGroup3Nodes")
        val lbasGroup3NodesProperty = lbasGroup3Nodes.toProperty()
        @IniConfig(key = "MiscOptions")
        val miscOptionsProperty = miscOptions.toProperty()

        var enabled by enabledProperty
        var engine by engineProperty
        var map by mapProperty
        var retreatNodes by retreatNodesProperty
        var fleets by fleetsProperty
        var fleetMode by fleetModeProperty
        var nodeSelects by nodeSelectsProperty
        var formations by formationsProperty
        var nightBattles by nightBattlesProperty
        var retreatLimit by retreatLimitProperty
        var repairLimit by repairLimitProperty
        var repairTimeLimit by repairTimeLimitProperty
        var lbasGroups by lbasGroupsProperty
        var lbasGroup1Nodes by lbasGroup1NodesProperty
        var lbasGroup2Nodes by lbasGroup2NodesProperty
        var lbasGroup3Nodes by lbasGroup3NodesProperty
        var miscOptions by miscOptionsProperty
    }

    class EventReset(
            enabled: Boolean = false,
            frequency: Int = 3,
            farmDifficulty: EventDifficulty = EventDifficulty.EASY,
            resetDifficulty: EventDifficulty = EventDifficulty.MEDIUM
    ) {
        @IniConfig("Enabled")
        val enabledProperty = enabled.toProperty()
        @IniConfig("Frequency")
        val frequencyProperty = frequency.toProperty()
        @IniConfig("FarmDifficulty")
        val farmDifficultyProperty = farmDifficulty.toProperty()
        @IniConfig("ResetDifficulty")
        val resetDifficultyProperty = resetDifficulty.toProperty()

        var enabled by enabledProperty
        var frequency by frequencyProperty
        var farmDifficulty by farmDifficultyProperty
        var resetDifficulty by resetDifficultyProperty
    }

    class ShipSwitcher(
            enabled: Boolean = true,
            slot1Criteria: List<SwitchCriteria> = mutableListOf(),
            slot1Ships: List<String> = mutableListOf(),
            slot2Criteria: List<SwitchCriteria> = mutableListOf(),
            slot2Ships: List<String> = mutableListOf(),
            slot3Criteria: List<SwitchCriteria> = mutableListOf(),
            slot3Ships: List<String> = mutableListOf(),
            slot4Criteria: List<SwitchCriteria> = mutableListOf(),
            slot4Ships: List<String> = mutableListOf(),
            slot5Criteria: List<SwitchCriteria> = mutableListOf(),
            slot5Ships: List<String> = mutableListOf(),
            slot6Criteria: List<SwitchCriteria> = mutableListOf(),
            slot6Ships: List<String> = mutableListOf()
    ) {
        @IniConfig("Enabled")
        val enabledProperty = enabled.toProperty()
        @IniConfig("Slot1Criteria")
        val slot1CriteriaProperty = slot1Criteria.toProperty()
        @IniConfig("Slot1Ships")
        val slot1ShipsProperty = slot1Ships.toProperty()
        @IniConfig("Slot2Criteria")
        val slot2CriteriaProperty = slot2Criteria.toProperty()
        @IniConfig("Slot2Ships")
        val slot2ShipsProperty = slot2Ships.toProperty()
        @IniConfig("Slot3Criteria")
        val slot3CriteriaProperty = slot3Criteria.toProperty()
        @IniConfig("Slot3Ships")
        val slot3ShipsProperty = slot3Ships.toProperty()
        @IniConfig("Slot4Criteria")
        val slot4CriteriaProperty = slot4Criteria.toProperty()
        @IniConfig("Slot4Ships")
        val slot4ShipsProperty = slot4Ships.toProperty()
        @IniConfig("Slot5Criteria")
        val slot5CriteriaProperty = slot5Criteria.toProperty()
        @IniConfig("Slot5Ships")
        val slot5ShipsProperty = slot5Ships.toProperty()
        @IniConfig("Slot6Criteria")
        val slot6CriteriaProperty = slot6Criteria.toProperty()
        @IniConfig("Slot6Ships")
        val slot6ShipsProperty = slot6Ships.toProperty()

        var enabled by enabledProperty
        var slot1Criteria by slot1CriteriaProperty
        var slot1Ships by slot1ShipsProperty
        var slot2Criteria by slot2CriteriaProperty
        var slot2Ships by slot2ShipsProperty
        var slot3Criteria by slot3CriteriaProperty
        var slot3Ships by slot3ShipsProperty
        var slot4Criteria by slot4CriteriaProperty
        var slot4Ships by slot4ShipsProperty
        var slot5Criteria by slot5CriteriaProperty
        var slot5Ships by slot5ShipsProperty
        var slot6Criteria by slot6CriteriaProperty
        var slot6Ships by slot6ShipsProperty
    }

    class Quests(
            enabled: Boolean = true,
            questGroups: List<QuestGroups> = mutableListOf(QuestGroups.DAILY, QuestGroups.WEEKLY, QuestGroups.MONTHLY)
    ) {
        @IniConfig(key = "Enabled")
        val enabledProperty = enabled.toProperty()
        @IniConfig(key = "QuestGroups")
        val questGroupsProperty = questGroups.toProperty()

        var enabled by enabledProperty
        var questGroups by questGroupsProperty
    }

    override fun toString(): String = fxJacksonObjectMapper().writeValueAsString(this)
}
