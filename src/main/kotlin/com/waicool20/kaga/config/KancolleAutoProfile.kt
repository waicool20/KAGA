package com.waicool20.kaga.config

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.IniConfig
import com.waicool20.kaga.util.fromObject
import com.waicool20.kaga.util.toObject
import javafx.beans.property.*
import javafx.collections.FXCollections
import org.ini4j.Wini
import tornadofx.getValue
import tornadofx.setValue
import java.nio.file.*
import java.util.*
import java.util.regex.Pattern
import kotlin.io.FileAlreadyExistsException


class KancolleAutoProfile(
        name: String, val general: General, val scheduledSleep: ScheduledSleep,
        val scheduledStop: ScheduledStop, val expeditions: Expeditions,
        val pvp: Pvp, val sortie: Sortie, val submarineSwitch: SubmarineSwitch,
        val lbas: Lbas, val quests: Quests
) {
    var nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    fun path(): Path = Paths.get(Kaga.CONFIG_DIR.toString(), "$name-config.ini")

    fun save(path: Path = path()) {
        if (Files.notExists(path)) {
            Files.createDirectories(path.parent)
            Files.createFile(path)
        }
        Files.write(path, ByteArray(0), StandardOpenOption.TRUNCATE_EXISTING)
        val ini = Wini()
        ini.add("General").fromObject(general)
        ini.add("ScheduledSleep").fromObject(scheduledSleep)
        ini.add("ScheduledStop").fromObject(scheduledStop)
        ini.add("Expeditions").fromObject(expeditions)
        ini.add("PvP").fromObject(pvp)
        ini.add("Combat").fromObject(sortie)
        ini.add("SubmarineSwitch").fromObject(submarineSwitch)
        ini.add("LBAS").fromObject(lbas)
        ini.add("Quests").fromObject(quests)
        ini.store(path.toFile())
    }

    fun delete() {
        with(path()) {
            if (Files.exists(this)) {
                Files.delete(this)
            }
        }
    }

    companion object Loader {
        @JvmStatic fun load(path: Path = Paths.get(Kaga.CONFIG.kancolleAutoRootDirPath.toString(), "config.ini")): KancolleAutoProfile? {
            if (Files.exists(path)) {
                val matcher = Pattern.compile("(.+?)-config\\.ini").matcher(path.fileName.toString())
                val name = run {
                    if (matcher.matches()) {
                        matcher.group(1)
                    } else {
                        var backupPath = Paths.get(path.parent.toString(), "config.ini.bak")
                        var index = 0
                        while (Files.exists(backupPath)) {
                            backupPath = Paths.get(path.parent.toString(), "config.ini.bak${index++}")
                        }
                        Files.copy(path, backupPath)
                        "<Current Profile>"
                    }
                }
                val ini = Wini(path.toFile())

                val general = ini["General"]?.toObject(General::class.java)
                val scheduledSleep = ini["ScheduledSleep"]?.toObject(ScheduledSleep::class.java)
                val scheduledStop = ini["ScheduledStop"]?.toObject(ScheduledStop::class.java)
                val expeditions = ini["Expeditions"]?.toObject(Expeditions::class.java)
                val pvp = ini["PvP"]?.toObject(Pvp::class.java)
                val sortie = ini["Combat"]?.toObject(Sortie::class.java)
                val submarineSwitch = ini["SubmarineSwitch"]?.toObject(SubmarineSwitch::class.java)
                val lbas = ini["LBAS"]?.toObject(Lbas::class.java)
                val quests = ini["Quests"]?.toObject(Quests::class.java)

                if (general == null || scheduledSleep == null || scheduledStop == null ||
                        expeditions == null || pvp == null || sortie == null ||
                        submarineSwitch == null || lbas == null || quests == null) {
                    return null
                }

                return KancolleAutoProfile(name, general, scheduledSleep, scheduledStop,
                        expeditions, pvp, sortie, submarineSwitch, lbas, quests)
            }
            return null
        }
    }

    enum class RecoveryMethod {BROWSER, KC3, KCV, KCT, E0, NONE }

    enum class ScheduledStopMode {TIME, EXPEDITION, SORTIE, PVP }

    enum class CombatFormation {
        LINE_AHEAD, DOUBLE_LINE, DIAMOND, ECHELON, LINE_ABREAST, COMBINEDFLEET_1, COMBINEDFLEET_2, COMBINEDFLEET_3, COMBINEDFLEET_4;

        override fun toString(): String {
            return this.name.toLowerCase()
        }
    }

    enum class Submarines(val prettyString: String) {
        ALL("All"), I_8("I-8"), I_19("I-19"), I_26("I-26"),
        I_58("I-58"), I_168("I-168"), MARUYU("Maruyu"),
        RO_500("Ro-500"), U_511("U-511");

        override fun toString() = prettyString.toLowerCase()
    }

    class General(
            program: String, recoveryMethod: RecoveryMethod, basicRecovery: Boolean,
            sleepCycle: Int, paranoia: Int, sleepModifier: Int
    ) {
        @IniConfig(key = "Program") val programProperty = SimpleStringProperty(program)
        @IniConfig(key = "RecoveryMethod") val recoveryMethodProperty = SimpleObjectProperty<RecoveryMethod>(recoveryMethod)
        @IniConfig(key = "BasicRecovery") val basicRecoveryProperty: BooleanProperty = SimpleBooleanProperty(basicRecovery)
        val offset = (TimeZone.getDefault().rawOffset - TimeZone.getTimeZone("Japan")
                .rawOffset) / 3600000
        @IniConfig(key = "JSTOffset", read = false) val jstOffsetProperty: IntegerProperty = SimpleIntegerProperty(offset)
        @IniConfig(key = "SleepCycle") val sleepCycleProperty: IntegerProperty = SimpleIntegerProperty(sleepCycle)
        @IniConfig(key = "Paranoia") val paranoiaProperty: IntegerProperty = SimpleIntegerProperty(paranoia)
        @IniConfig(key = "SleepModifier") val sleepModifierProperty: IntegerProperty = SimpleIntegerProperty(sleepModifier)

        var program by programProperty
        var recoveryMethod by recoveryMethodProperty
        var basicRecovery by basicRecoveryProperty
        var jstOffset by jstOffsetProperty
        var sleepCycle by sleepCycleProperty
        var paranoia by paranoiaProperty
        var sleepModifier by sleepModifierProperty
    }

    class ScheduledSleep(enabled: Boolean, startTime: String, length: Double) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "StartTime") val startTimeProperty = SimpleStringProperty(startTime)
        @IniConfig(key = "SleepLength") val lengthProperty = SimpleDoubleProperty(length)

        var enabled by enabledProperty
        var startTime by startTimeProperty
        var length by lengthProperty
    }

    class ScheduledStop(enabled: Boolean, mode: ScheduledStopMode, count: Int) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "Mode") val modeProperty = SimpleObjectProperty(mode)
        @IniConfig(key = "Count") val countProperty = SimpleIntegerProperty(count)

        var enabled by enabledProperty
        var mode by modeProperty
        var count by countProperty
    }

    class Expeditions(enabled: Boolean, fleet2: String, fleet3: String, fleet4: String) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "Fleet2") val fleet2Property = SimpleStringProperty(fleet2)
        @IniConfig(key = "Fleet3") val fleet3Property = SimpleStringProperty(fleet3)
        @IniConfig(key = "Fleet4") val fleet4Property = SimpleStringProperty(fleet4)

        var enabled by enabledProperty
        var fleet2 by fleet2Property
        var fleet3 by fleet3Property
        var fleet4 by fleet4Property
    }

    class Pvp(enabled: Boolean, fleetComp: Int) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "FleetComp") val fleetCompProperty = SimpleIntegerProperty(fleetComp)

        var enabled by enabledProperty
        var fleetComp by fleetCompProperty
    }

    class Sortie(
            enabled: Boolean, fleetComp: Int, area: Int, subarea: Int, combinedFleet: Boolean,
            nodes: Int, nodeSelects: String, formations: List<CombatFormation>,
            nightBattles: List<Boolean>, retreatLimit: Int, repairLimit: Int, repairTimeLimit: Int,
            checkFatigue: Boolean, portCheck: Boolean, medalStop: Boolean, lastNodePush: Boolean
    ) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "FleetComp") val fleetCompProperty = SimpleIntegerProperty(fleetComp)
        @IniConfig(key = "Area") val areaProperty = SimpleIntegerProperty(area)
        @IniConfig(key = "Subarea") val subareaProperty = SimpleIntegerProperty(subarea)
        @IniConfig(key = "CombinedFleet") val combinedFleetProperty = SimpleBooleanProperty(combinedFleet)
        @IniConfig(key = "Nodes") val nodesProperty = SimpleIntegerProperty(nodes)
        @IniConfig(key = "NodeSelects") val nodeSelectsProperty = SimpleStringProperty(nodeSelects)
        @IniConfig(key = "Formations") val formationsProperty = SimpleListProperty(FXCollections.observableArrayList(formations))
        @IniConfig(key = "NightBattles") val nightBattlesProperty = SimpleListProperty(FXCollections.observableArrayList(nightBattles))
        @IniConfig(key = "RetreatLimit") val retreatLimitProperty = SimpleIntegerProperty(retreatLimit)
        @IniConfig(key = "RepairLimit") val repairLimitProperty = SimpleIntegerProperty(repairLimit)
        @IniConfig(key = "RepairTimeLimit") val repairTimeLimitProperty = SimpleIntegerProperty(repairTimeLimit)
        @IniConfig(key = "CheckFatigue") val checkFatigueProperty = SimpleBooleanProperty(checkFatigue)
        @IniConfig(key = "PortCheck") val portCheckProperty = SimpleBooleanProperty(portCheck)
        @IniConfig(key = "MedalStop") val medalStopProperty = SimpleBooleanProperty(medalStop)
        @IniConfig(key = "LastNodePush") val lastNodePushProperty = SimpleBooleanProperty(lastNodePush)

        var enabled by enabledProperty
        var fleetComp by fleetCompProperty
        var area by areaProperty
        var subarea by subareaProperty
        var combinedFleet by combinedFleetProperty
        var nodes by nodesProperty
        var nodeSelects by nodeSelectsProperty
        var formations by formationsProperty
        var nightBattles by nightBattlesProperty
        var retreatLimit by retreatLimitProperty
        var repairLimit by repairLimitProperty
        var repairTimeLimit by repairTimeLimitProperty
        var checkFatigue by checkFatigueProperty
        var portCheck by portCheckProperty
        var medalStop by medalStopProperty
        var lastNodePush by lastNodePushProperty
    }

    class SubmarineSwitch(enabled: Boolean, enabledSubs: List<Submarines>) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "EnabledSubs") val enabledSubsProperty = SimpleListProperty(FXCollections.observableArrayList(enabledSubs))

        var enabled by enabledProperty
        var enabledSubs by enabledSubsProperty
    }

    class Lbas(
            enabled: Boolean, enabledGroups: Set<Int>, group1Nodes: List<String>,
            group2Nodes: List<String>, group3Nodes: List<String>
    ) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "EnabledGroups") val enabledGroupsProperty = SimpleSetProperty(FXCollections.observableSet(enabledGroups))
        @IniConfig(key = "Group1Nodes") val group1NodesProperty = SimpleListProperty(FXCollections.observableArrayList(group1Nodes))
        @IniConfig(key = "Group2Nodes") val group2NodesProperty = SimpleListProperty(FXCollections.observableArrayList(group2Nodes))
        @IniConfig(key = "Group3Nodes") val group3NodesProperty = SimpleListProperty(FXCollections.observableArrayList(group3Nodes))

        var enabled by enabledProperty
        var enabledGroups by enabledGroupsProperty
        var group1Nodes by group1NodesProperty
        var group2Nodes by group2NodesProperty
        var group3Nodes by group3NodesProperty
    }

    class Quests(enabled: Boolean, quests: List<String>, checkSchedule: Int) {
        @IniConfig(key = "Enabled") val enabledProperty = SimpleBooleanProperty(enabled)
        @IniConfig(key = "Quests") val questsProperty = SimpleListProperty(FXCollections.observableArrayList(quests))
        @IniConfig(key = "CheckSchedule") val checkScheduleProperty = SimpleIntegerProperty(checkSchedule)

        var enabled by enabledProperty
        var quests by questsProperty
        var checkSchedule by checkScheduleProperty
    }
}
