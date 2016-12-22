package com.waicool20.kaga.config;

import com.waicool20.kaga.Kaga;
import javafx.beans.property.*;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KancolleAutoProfile {
    private StringProperty name;

    public enum RecoveryMethod {
        BROWSER, KC3, KCV, KCT, E0, NONE
    }

    public static class General {

        @IniConfig(key = "Program") private StringProperty program;
        @IniConfig(key = "RecoveryMethod") private ObjectProperty<RecoveryMethod> recoveryMethod;
        @IniConfig(key = "BasicRecovery") private BooleanProperty basicRecovery;
        @IniConfig(key = "JSTOffset", read = false) private IntegerProperty jstOffset;
        @IniConfig(key = "SleepCycle") private IntegerProperty sleepCycle;
        @IniConfig(key = "Paranoia") private IntegerProperty paranoia;
        @IniConfig(key = "SleepModifier") private IntegerProperty sleepModifier;

        public General(String program, RecoveryMethod recoveryMethod, boolean basicRecovery,
            int sleepCycle, int paranoia, int sleepModifier) {
            this.program = new SimpleStringProperty(program);
            this.recoveryMethod = new SimpleObjectProperty<>(recoveryMethod);
            this.basicRecovery = new SimpleBooleanProperty(basicRecovery);
            this.sleepCycle = new SimpleIntegerProperty(sleepCycle);
            this.paranoia = new SimpleIntegerProperty(paranoia);
            this.sleepModifier = new SimpleIntegerProperty(sleepModifier);
            int offset = (TimeZone.getDefault().getRawOffset() - TimeZone.getTimeZone("Japan").getRawOffset())/3600000;
            jstOffset = new SimpleIntegerProperty(offset);
        }

        public String getProgram() {
            return program.get();
        }

        public StringProperty programProperty() {
            return program;
        }

        public void setProgram(String program) {
            this.program.set(program);
        }

        public RecoveryMethod getRecoveryMethod() {
            return recoveryMethod.get();
        }

        public ObjectProperty<RecoveryMethod> recoveryMethodProperty() {
            return recoveryMethod;
        }

        public void setRecoveryMethod(RecoveryMethod recoveryMethod) {
            this.recoveryMethod.set(recoveryMethod);
        }

        public boolean isBasicRecovery() {
            return basicRecovery.get();
        }

        public BooleanProperty basicRecoveryProperty() {
            return basicRecovery;
        }

        public void setBasicRecovery(boolean basicRecovery) {
            this.basicRecovery.set(basicRecovery);
        }

        public int getJstOffset() {
            return jstOffset.get();
        }

        public IntegerProperty jstOffsetProperty() {
            return jstOffset;
        }

        public int getSleepCycle() {
            return sleepCycle.get();
        }

        public IntegerProperty sleepCycleProperty() {
            return sleepCycle;
        }

        public void setSleepCycle(int sleepCycle) {
            this.sleepCycle.set(sleepCycle);
        }

        public int getParanoia() {
            return paranoia.get();
        }

        public IntegerProperty paranoiaProperty() {
            return paranoia;
        }

        public void setParanoia(int paranoia) {
            this.paranoia.set(paranoia);
        }

        public int getSleepModifier() {
            return sleepModifier.get();
        }

        public IntegerProperty sleepModifierProperty() {
            return sleepModifier;
        }

        public void setSleepModifier(int sleepModifier) {
            this.sleepModifier.set(sleepModifier);
        }
    }


    public static class ScheduledSleep {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "StartTime") private IntegerProperty startTime;
        @IniConfig(key = "SleepLength") private DoubleProperty length;

        public ScheduledSleep(boolean enabled, int startTime, double length) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.startTime = new SimpleIntegerProperty(startTime);
            this.length = new SimpleDoubleProperty(length);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public int getStartTime() {
            return startTime.get();
        }

        public IntegerProperty startTimeProperty() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime.set(startTime);
        }

        public double getLength() {
            return length.get();
        }

        public DoubleProperty lengthProperty() {
            return length;
        }

        public void setLength(float length) {
            this.length.set(length);
        }
    }


    public enum ScheduledStopMode {
        TIME, EXPEDITION, SORTIE, PVP
    }


    public static class ScheduledStop {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "Mode") private ObjectProperty<ScheduledStopMode> mode;
        @IniConfig(key = "Count") private IntegerProperty count;

        public ScheduledStop(boolean enabled, ScheduledStopMode mode, int count) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.mode = new SimpleObjectProperty<>(mode);
            this.count = new SimpleIntegerProperty(count);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public ScheduledStopMode getMode() {
            return mode.get();
        }

        public ObjectProperty<ScheduledStopMode> modeProperty() {
            return mode;
        }

        public void setMode(ScheduledStopMode mode) {
            this.mode.set(mode);
        }

        public int getCount() {
            return count.get();
        }

        public IntegerProperty countProperty() {
            return count;
        }

        public void setCount(int count) {
            this.count.set(count);
        }
    }


    public static class Expeditions {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "Fleet2") private IntegerProperty fleet2;
        @IniConfig(key = "Fleet3") private IntegerProperty fleet3;
        @IniConfig(key = "Fleet4") private IntegerProperty fleet4;

        public Expeditions(boolean enabled, int fleet2, int fleet3, int fleet4) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.fleet2 = new SimpleIntegerProperty(fleet2);
            this.fleet3 = new SimpleIntegerProperty(fleet3);
            this.fleet4 = new SimpleIntegerProperty(fleet4);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public int getFleet2() {
            return fleet2.get();
        }

        public IntegerProperty fleet2Property() {
            return fleet2;
        }

        public void setFleet2(int fleet2) {
            this.fleet2.set(fleet2);
        }

        public int getFleet3() {
            return fleet3.get();
        }

        public IntegerProperty fleet3Property() {
            return fleet3;
        }

        public void setFleet3(int fleet3) {
            this.fleet3.set(fleet3);
        }

        public int getFleet4() {
            return fleet4.get();
        }

        public IntegerProperty fleet4Property() {
            return fleet4;
        }

        public void setFleet4(int fleet4) {
            this.fleet4.set(fleet4);
        }
    }


    public static class Pvp {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "FleetComp") private IntegerProperty fleetComp;

        public Pvp(boolean enabled, int fleetComp) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.fleetComp = new SimpleIntegerProperty(fleetComp);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public int getFleetComp() {
            return fleetComp.get();
        }

        public IntegerProperty fleetCompProperty() {
            return fleetComp;
        }

        public void setFleetComp(int fleetComp) {
            this.fleetComp.set(fleetComp);
        }
    }


    public enum CombatFormation {
        LINE_AHEAD, DOUBLE_LINE, DIAMOND, ECHELON, LINE_ABREAST, COMBINEDFLEET_1, COMBINEDFLEET_2, COMBINEDFLEET_3, COMBINEDFLEET_4
    }


    public static class Sortie {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "FleetComp") private IntegerProperty fleetComp;
        @IniConfig(key = "Area") private IntegerProperty area;
        @IniConfig(key = "Subarea") private IntegerProperty subArea;
        @IniConfig(key = "CombinedFleet") private BooleanProperty combinedFleet;
        @IniConfig(key = "Nodes") private IntegerProperty nodes;
        @IniConfig(key = "NodeSelects") private StringProperty nodeSelects;
        @IniConfig(key = "Formations") private ObjectProperty<LinkedList<CombatFormation>>
            formations;
        @IniConfig(key = "NightBattles") private BooleanProperty nightBattles;
        @IniConfig(key = "RetreatLimit") private IntegerProperty retreatLimit;
        @IniConfig(key = "RepairLimit") private IntegerProperty repairLimit;
        @IniConfig(key = "RepairTimeLimit") private IntegerProperty repairTimeLimit;
        @IniConfig(key = "CheckFatigue") private BooleanProperty checkFatigue;
        @IniConfig(key = "PortCheck") private BooleanProperty portCheck;
        @IniConfig(key = "MedalStop") private BooleanProperty medalStop;
        @IniConfig(key = "LastNodePush") private BooleanProperty lastNodePush;

        public Sortie(boolean enabled, int fleetComp, int area, int subArea, boolean combinedFleet,
            int nodes, String nodeSelects, LinkedList<CombatFormation> formations,
            boolean nightBattles, int retreatLimit, int repairLimit, int repairTimeLimit,
            boolean checkFatigue, boolean portCheck, boolean medalStop, boolean lastNodePush) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.fleetComp = new SimpleIntegerProperty(fleetComp);
            this.area = new SimpleIntegerProperty(area);
            this.subArea = new SimpleIntegerProperty(subArea);
            this.combinedFleet = new SimpleBooleanProperty(combinedFleet);
            this.nodes = new SimpleIntegerProperty(nodes);
            this.nodeSelects = new SimpleStringProperty(nodeSelects);
            this.formations = new SimpleObjectProperty<>(formations);
            this.nightBattles = new SimpleBooleanProperty(nightBattles);
            this.retreatLimit = new SimpleIntegerProperty(retreatLimit);
            this.repairLimit = new SimpleIntegerProperty(repairLimit);
            this.repairTimeLimit = new SimpleIntegerProperty(repairTimeLimit);
            this.checkFatigue = new SimpleBooleanProperty(checkFatigue);
            this.portCheck = new SimpleBooleanProperty(portCheck);
            this.medalStop = new SimpleBooleanProperty(medalStop);
            this.lastNodePush = new SimpleBooleanProperty(lastNodePush);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public int getFleetComp() {
            return fleetComp.get();
        }

        public IntegerProperty fleetCompProperty() {
            return fleetComp;
        }

        public void setFleetComp(int fleetComp) {
            this.fleetComp.set(fleetComp);
        }

        public int getArea() {
            return area.get();
        }

        public IntegerProperty areaProperty() {
            return area;
        }

        public void setArea(int area) {
            this.area.set(area);
        }

        public int getSubArea() {
            return subArea.get();
        }

        public IntegerProperty subAreaProperty() {
            return subArea;
        }

        public void setSubArea(int subArea) {
            this.subArea.set(subArea);
        }

        public boolean isCombinedFleet() {
            return combinedFleet.get();
        }

        public BooleanProperty combinedFleetProperty() {
            return combinedFleet;
        }

        public void setCombinedFleet(boolean combinedFleet) {
            this.combinedFleet.set(combinedFleet);
        }

        public int getNodes() {
            return nodes.get();
        }

        public IntegerProperty nodesProperty() {
            return nodes;
        }

        public void setNodes(int nodes) {
            this.nodes.set(nodes);
        }

        public String getNodeSelects() {
            return nodeSelects.get();
        }

        public StringProperty nodeSelectsProperty() {
            return nodeSelects;
        }

        public void setNodeSelects(String nodeSelects) {
            this.nodeSelects.set(nodeSelects);
        }

        public LinkedList<CombatFormation> getFormations() {
            return formations.get();
        }

        public ObjectProperty<LinkedList<CombatFormation>> formationsProperty() {
            return formations;
        }

        public void setFormations(LinkedList<CombatFormation> formations) {
            this.formations.set(formations);
        }

        public boolean isNightBattles() {
            return nightBattles.get();
        }

        public BooleanProperty nightBattlesProperty() {
            return nightBattles;
        }

        public void setNightBattles(boolean nightBattles) {
            this.nightBattles.set(nightBattles);
        }

        public int getRetreatLimit() {
            return retreatLimit.get();
        }

        public IntegerProperty retreatLimitProperty() {
            return retreatLimit;
        }

        public void setRetreatLimit(int retreatLimit) {
            this.retreatLimit.set(retreatLimit);
        }

        public int getRepairLimit() {
            return repairLimit.get();
        }

        public IntegerProperty repairLimitProperty() {
            return repairLimit;
        }

        public void setRepairLimit(int repairLimit) {
            this.repairLimit.set(repairLimit);
        }

        public int getRepairTimeLimit() {
            return repairTimeLimit.get();
        }

        public IntegerProperty repairTimeLimitProperty() {
            return repairTimeLimit;
        }

        public void setRepairTimeLimit(int repairTimeLimit) {
            this.repairTimeLimit.set(repairTimeLimit);
        }

        public boolean isCheckFatigue() {
            return checkFatigue.get();
        }

        public BooleanProperty checkFatigueProperty() {
            return checkFatigue;
        }

        public void setCheckFatigue(boolean checkFatigue) {
            this.checkFatigue.set(checkFatigue);
        }

        public boolean isPortCheck() {
            return portCheck.get();
        }

        public BooleanProperty portCheckProperty() {
            return portCheck;
        }

        public void setPortCheck(boolean portCheck) {
            this.portCheck.set(portCheck);
        }

        public boolean isMedalStop() {
            return medalStop.get();
        }

        public BooleanProperty medalStopProperty() {
            return medalStop;
        }

        public void setMedalStop(boolean medalStop) {
            this.medalStop.set(medalStop);
        }

        public boolean isLastNodePush() {
            return lastNodePush.get();
        }

        public BooleanProperty lastNodePushProperty() {
            return lastNodePush;
        }

        public void setLastNodePush(boolean lastNodePush) {
            this.lastNodePush.set(lastNodePush);
        }
    }


    public enum Submarines {
        ALL("all"), I_8("i-8"), I_19("i-19"), I_26("i-26"), I_58("i-58"), I_168("i-168"), MARUYU("maruyu"), RO_500("ro-500"), U_511("u-511");

        private String name;

        Submarines(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }


    public static class SubmarineSwitch {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "EnabledSubs") private ObjectProperty<LinkedList<Submarines>> enabledSubs;

        public SubmarineSwitch(boolean enabled, LinkedList<Submarines> enabledSubs) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.enabledSubs = new SimpleObjectProperty<>(enabledSubs);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public LinkedList<Submarines> getEnabledSubs() {
            return enabledSubs.get();
        }

        public ObjectProperty<LinkedList<Submarines>> enabledSubsProperty() {
            return enabledSubs;
        }

        public void setEnabledSubs(LinkedList<Submarines> enabledSubs) {
            this.enabledSubs.set(enabledSubs);
        }
    }


    public static class Lbas {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "EnabledGroups") private ObjectProperty<LinkedList<Integer>> enabledGroups;
        @IniConfig(key = "Group1Nodes") private ObjectProperty<LinkedList<String>> group1Nodes;
        @IniConfig(key = "Group2Nodes")private ObjectProperty<LinkedList<String>> group2Nodes;
        @IniConfig(key = "Group3Nodes")private ObjectProperty<LinkedList<String>> group3Nodes;

        public Lbas(boolean enabled, LinkedList<Integer> enabledGroups,
            LinkedList<String> group1Nodes, LinkedList<String> group2Nodes,
            LinkedList<String> group3Nodes) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.enabledGroups = new SimpleObjectProperty<>(enabledGroups);
            this.group1Nodes = new SimpleObjectProperty<>(group1Nodes);
            this.group2Nodes = new SimpleObjectProperty<>(group2Nodes);
            this.group3Nodes = new SimpleObjectProperty<>(group3Nodes);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public LinkedList<Integer> getEnabledGroups() {
            return enabledGroups.get();
        }

        public ObjectProperty<LinkedList<Integer>> enabledGroupsProperty() {
            return enabledGroups;
        }

        public void setEnabledGroups(LinkedList<Integer> enabledGroups) {
            this.enabledGroups.set(enabledGroups);
        }

        public LinkedList<String> getGroup1Nodes() {
            return group1Nodes.get();
        }

        public ObjectProperty<LinkedList<String>> group1NodesProperty() {
            return group1Nodes;
        }

        public void setGroup1Nodes(LinkedList<String> group1Nodes) {
            this.group1Nodes.set(group1Nodes);
        }

        public LinkedList<String> getGroup2Nodes() {
            return group2Nodes.get();
        }

        public ObjectProperty<LinkedList<String>> group2NodesProperty() {
            return group2Nodes;
        }

        public void setGroup2Nodes(LinkedList<String> group2Nodes) {
            this.group2Nodes.set(group2Nodes);
        }

        public LinkedList<String> getGroup3Nodes() {
            return group3Nodes.get();
        }

        public ObjectProperty<LinkedList<String>> group3NodesProperty() {
            return group3Nodes;
        }

        public void setGroup3Nodes(LinkedList<String> group3Nodes) {
            this.group3Nodes.set(group3Nodes);
        }
    }


    public static class Quests {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "Quests") private ObjectProperty<LinkedList<String>> quests;
        @IniConfig(key = "CheckSchedule") private IntegerProperty checkSchedule;

        public Quests(boolean enabled, LinkedList<String> quests, int checkSchedule) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.quests = new SimpleObjectProperty<>(quests);
            this.checkSchedule = new SimpleIntegerProperty(checkSchedule);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public LinkedList<String> getQuests() {
            return quests.get();
        }

        public ObjectProperty<LinkedList<String>> questsProperty() {
            return quests;
        }

        public void setQuests(LinkedList<String> quests) {
            this.quests.set(quests);
        }

        public int getCheckSchedule() {
            return checkSchedule.get();
        }

        public IntegerProperty checkScheduleProperty() {
            return checkSchedule;
        }

        public void setCheckSchedule(int checkSchedule) {
            this.checkSchedule.set(checkSchedule);
        }
    }


    private General general;
    private ScheduledSleep scheduledSleep;
    private ScheduledStop scheduledStop;
    private Expeditions expeditions;
    private Pvp pvp;
    private Sortie sortie;
    private SubmarineSwitch submarineSwitch;
    private Lbas lbas;
    private Quests quests;

    public KancolleAutoProfile(String name, General general, ScheduledSleep scheduledSleep,
        ScheduledStop scheduledStop, Expeditions expeditions, Pvp pvp, Sortie sortie,
        SubmarineSwitch submarineSwitch, Lbas lbas, Quests quests) {
        this.name = new SimpleStringProperty(name);
        this.general = general;
        this.scheduledSleep = scheduledSleep;
        this.scheduledStop = scheduledStop;
        this.expeditions = expeditions;
        this.pvp = pvp;
        this.sortie = sortie;
        this.submarineSwitch = submarineSwitch;
        this.lbas = lbas;
        this.quests = quests;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Path getPath() {
        return Paths.get(Kaga.CONFIG_DIR.toString(), getName() + "-config.ini");
    }

    public General getGeneral() {
        return general;
    }

    public ScheduledSleep getScheduledSleep() {
        return scheduledSleep;
    }

    public ScheduledStop getScheduledStop() {
        return scheduledStop;
    }

    public Expeditions getExpeditions() {
        return expeditions;
    }

    public Pvp getPvp() {
        return pvp;
    }

    public Sortie getSortie() {
        return sortie;
    }

    public SubmarineSwitch getSubmarineSwitch() {
        return submarineSwitch;
    }

    public Lbas getLbas() {
        return lbas;
    }

    public Quests getQuests() {
        return quests;
    }

    public void save() throws IOException {
        save(getPath());
    }

    public void save(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        Files.write(path, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        Wini ini = new Wini();
        IniUtils.objectToSection(ini.add("General"), general);
        IniUtils.objectToSection(ini.add("ScheduledSleep"), scheduledSleep);
        IniUtils.objectToSection(ini.add("ScheduledStop"), scheduledStop);
        IniUtils.objectToSection(ini.add("Expeditions"), expeditions);
        IniUtils.objectToSection(ini.add("PvP"), pvp);
        IniUtils.objectToSection(ini.add("Combat"), sortie);
        IniUtils.objectToSection(ini.add("SubmarineSwitch"), submarineSwitch);
        IniUtils.objectToSection(ini.add("LBAS"), lbas);
        IniUtils.objectToSection(ini.add("Quests"), quests);
        ini.store(path.toFile());
    }

    public void delete() throws IOException {
        Path config = getPath();
        if (Files.exists(config)) {
            Files.delete(config);
        }
    }

    public static KancolleAutoProfile load(Path path) throws IOException {
        if (Files.exists(path)) {
            Matcher matcher = Pattern.compile("(.+?)-config\\.ini").matcher(path.getFileName().toString());
            String name = matcher.matches() ? matcher.group(1) : "Current Profile";

            Wini ini = new Wini(path.toFile());
            Ini.Section generalSection = ini.get("General");
            Ini.Section scheduledSleepSection = ini.get("ScheduledSleep");
            Ini.Section scheduledStopSection = ini.get("ScheduledStop");
            Ini.Section expeditionsSection = ini.get("Expeditions");
            Ini.Section pvpSection = ini.get("PvP");
            Ini.Section combatSection = ini.get("Combat");
            Ini.Section submarineSwitchSection = ini.get("SubmarineSwitch");
            Ini.Section lbasSection = ini.get("LBAS");
            Ini.Section questSection = ini.get("Quests");
            if (generalSection == null || scheduledSleepSection == null
                || scheduledStopSection == null || expeditionsSection == null || pvpSection == null
                || combatSection == null || submarineSwitchSection == null || lbasSection == null
                || questSection == null) {
                return null;
            }
            General general = IniUtils.sectionToObject(generalSection, General.class);
            ScheduledSleep scheduledSleep =
                IniUtils.sectionToObject(scheduledSleepSection, ScheduledSleep.class);
            ScheduledStop scheduledStop =
                IniUtils.sectionToObject(scheduledStopSection, ScheduledStop.class);
            Expeditions expeditions =
                IniUtils.sectionToObject(expeditionsSection, Expeditions.class);
            Pvp pvp = IniUtils.sectionToObject(pvpSection, Pvp.class);
            Sortie sortie = IniUtils.sectionToObject(combatSection, Sortie.class);
            SubmarineSwitch submarineSwitch =
                IniUtils.sectionToObject(submarineSwitchSection, SubmarineSwitch.class);
            Lbas lbas = IniUtils.sectionToObject(lbasSection, Lbas.class);
            Quests quests = IniUtils.sectionToObject(questSection, Quests.class);

            return new KancolleAutoProfile(name, general, scheduledSleep, scheduledStop,
                expeditions, pvp, sortie, submarineSwitch, lbas, quests);
        }
        return null;
    }
}
