package com.waicool20.kaga.config;

import com.waicool20.kaga.Kaga;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KancolleAutoProfile {
    private StringProperty name;
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

    public static KancolleAutoProfile load(Path path) throws IOException {
        if (Files.exists(path)) {
            Matcher matcher =
                Pattern.compile("(.+?)-config\\.ini").matcher(path.getFileName().toString());
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
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

    public enum RecoveryMethod {
        BROWSER, KC3, KCV, KCT, E0, NONE
    }


    public enum ScheduledStopMode {
        TIME, EXPEDITION, SORTIE, PVP
    }


    public enum CombatFormation {
        LINE_AHEAD, DOUBLE_LINE, DIAMOND, ECHELON, LINE_ABREAST, COMBINEDFLEET_1, COMBINEDFLEET_2, COMBINEDFLEET_3, COMBINEDFLEET_4;

        @Override public String toString() {
            return this.name().toLowerCase();
        }
    }


    public enum Submarines {
        ALL, I_8, I_19, I_26, I_58, I_168, MARUYU, RO_500, U_511;

        @Override public String toString() {
            return this.name().replaceAll("_", "-").toLowerCase();
        }
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
            int offset = (TimeZone.getDefault().getRawOffset() - TimeZone.getTimeZone("Japan")
                .getRawOffset()) / 3600000;
            jstOffset = new SimpleIntegerProperty(offset);
        }

        public String getProgram() {
            return program.get();
        }

        public void setProgram(String program) {
            this.program.set(program);
        }

        public StringProperty programProperty() {
            return program;
        }

        public RecoveryMethod getRecoveryMethod() {
            return recoveryMethod.get();
        }

        public void setRecoveryMethod(RecoveryMethod recoveryMethod) {
            this.recoveryMethod.set(recoveryMethod);
        }

        public ObjectProperty<RecoveryMethod> recoveryMethodProperty() {
            return recoveryMethod;
        }

        public boolean isBasicRecovery() {
            return basicRecovery.get();
        }

        public void setBasicRecovery(boolean basicRecovery) {
            this.basicRecovery.set(basicRecovery);
        }

        public BooleanProperty basicRecoveryProperty() {
            return basicRecovery;
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

        public void setSleepCycle(int sleepCycle) {
            this.sleepCycle.set(sleepCycle);
        }

        public IntegerProperty sleepCycleProperty() {
            return sleepCycle;
        }

        public int getParanoia() {
            return paranoia.get();
        }

        public void setParanoia(int paranoia) {
            this.paranoia.set(paranoia);
        }

        public IntegerProperty paranoiaProperty() {
            return paranoia;
        }

        public int getSleepModifier() {
            return sleepModifier.get();
        }

        public void setSleepModifier(int sleepModifier) {
            this.sleepModifier.set(sleepModifier);
        }

        public IntegerProperty sleepModifierProperty() {
            return sleepModifier;
        }
    }


    public static class ScheduledSleep {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "StartTime") private StringProperty startTime;
        @IniConfig(key = "SleepLength") private DoubleProperty length;

        public ScheduledSleep(boolean enabled, String startTime, double length) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.startTime = new SimpleStringProperty(startTime);
            this.length = new SimpleDoubleProperty(length);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public String getStartTime() {
            return startTime.get();
        }

        public StringProperty startTimeProperty() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime.set(startTime);
        }

        public double getLength() {
            return length.get();
        }

        public void setLength(float length) {
            this.length.set(length);
        }

        public DoubleProperty lengthProperty() {
            return length;
        }
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

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public ScheduledStopMode getMode() {
            return mode.get();
        }

        public void setMode(ScheduledStopMode mode) {
            this.mode.set(mode);
        }

        public ObjectProperty<ScheduledStopMode> modeProperty() {
            return mode;
        }

        public int getCount() {
            return count.get();
        }

        public void setCount(int count) {
            this.count.set(count);
        }

        public IntegerProperty countProperty() {
            return count;
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

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public int getFleet2() {
            return fleet2.get();
        }

        public void setFleet2(int fleet2) {
            this.fleet2.set(fleet2);
        }

        public IntegerProperty fleet2Property() {
            return fleet2;
        }

        public int getFleet3() {
            return fleet3.get();
        }

        public void setFleet3(int fleet3) {
            this.fleet3.set(fleet3);
        }

        public IntegerProperty fleet3Property() {
            return fleet3;
        }

        public int getFleet4() {
            return fleet4.get();
        }

        public void setFleet4(int fleet4) {
            this.fleet4.set(fleet4);
        }

        public IntegerProperty fleet4Property() {
            return fleet4;
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

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public int getFleetComp() {
            return fleetComp.get();
        }

        public void setFleetComp(int fleetComp) {
            this.fleetComp.set(fleetComp);
        }

        public IntegerProperty fleetCompProperty() {
            return fleetComp;
        }
    }


    public static class Sortie {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "FleetComp") private IntegerProperty fleetComp;
        @IniConfig(key = "Area") private IntegerProperty area;
        @IniConfig(key = "Subarea") private IntegerProperty subArea;
        @IniConfig(key = "CombinedFleet") private BooleanProperty combinedFleet;
        @IniConfig(key = "Nodes") private IntegerProperty nodes;
        @IniConfig(key = "NodeSelects") private StringProperty nodeSelects;
        @IniConfig(key = "Formations") private SimpleListProperty<CombatFormation> formations;
        @IniConfig(key = "NightBattles") private BooleanProperty nightBattles;
        @IniConfig(key = "RetreatLimit") private IntegerProperty retreatLimit;
        @IniConfig(key = "RepairLimit") private IntegerProperty repairLimit;
        @IniConfig(key = "RepairTimeLimit") private IntegerProperty repairTimeLimit;
        @IniConfig(key = "CheckFatigue") private BooleanProperty checkFatigue;
        @IniConfig(key = "PortCheck") private BooleanProperty portCheck;
        @IniConfig(key = "MedalStop") private BooleanProperty medalStop;
        @IniConfig(key = "LastNodePush") private BooleanProperty lastNodePush;

        public Sortie(boolean enabled, int fleetComp, int area, int subArea, boolean combinedFleet,
            int nodes, String nodeSelects, List<CombatFormation> formations, boolean nightBattles,
            int retreatLimit, int repairLimit, int repairTimeLimit, boolean checkFatigue,
            boolean portCheck, boolean medalStop, boolean lastNodePush) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.fleetComp = new SimpleIntegerProperty(fleetComp);
            this.area = new SimpleIntegerProperty(area);
            this.subArea = new SimpleIntegerProperty(subArea);
            this.combinedFleet = new SimpleBooleanProperty(combinedFleet);
            this.nodes = new SimpleIntegerProperty(nodes);
            this.nodeSelects = new SimpleStringProperty(nodeSelects);
            this.formations =
                new SimpleListProperty<>(FXCollections.observableArrayList(formations));
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

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public int getFleetComp() {
            return fleetComp.get();
        }

        public void setFleetComp(int fleetComp) {
            this.fleetComp.set(fleetComp);
        }

        public IntegerProperty fleetCompProperty() {
            return fleetComp;
        }

        public int getArea() {
            return area.get();
        }

        public void setArea(int area) {
            this.area.set(area);
        }

        public IntegerProperty areaProperty() {
            return area;
        }

        public int getSubArea() {
            return subArea.get();
        }

        public void setSubArea(int subArea) {
            this.subArea.set(subArea);
        }

        public IntegerProperty subAreaProperty() {
            return subArea;
        }

        public boolean isCombinedFleet() {
            return combinedFleet.get();
        }

        public void setCombinedFleet(boolean combinedFleet) {
            this.combinedFleet.set(combinedFleet);
        }

        public BooleanProperty combinedFleetProperty() {
            return combinedFleet;
        }

        public int getNodes() {
            return nodes.get();
        }

        public void setNodes(int nodes) {
            this.nodes.set(nodes);
        }

        public IntegerProperty nodesProperty() {
            return nodes;
        }

        public String getNodeSelects() {
            return nodeSelects.get();
        }

        public void setNodeSelects(String nodeSelects) {
            this.nodeSelects.set(nodeSelects);
        }

        public StringProperty nodeSelectsProperty() {
            return nodeSelects;
        }

        public ObservableList<CombatFormation> getFormations() {
            return formations.get();
        }

        public void setFormations(ObservableList<CombatFormation> formations) {
            this.formations.set(formations);
        }

        public SimpleListProperty<CombatFormation> formationsProperty() {
            return formations;
        }

        public boolean isNightBattles() {
            return nightBattles.get();
        }

        public void setNightBattles(boolean nightBattles) {
            this.nightBattles.set(nightBattles);
        }

        public BooleanProperty nightBattlesProperty() {
            return nightBattles;
        }

        public int getRetreatLimit() {
            return retreatLimit.get();
        }

        public void setRetreatLimit(int retreatLimit) {
            this.retreatLimit.set(retreatLimit);
        }

        public IntegerProperty retreatLimitProperty() {
            return retreatLimit;
        }

        public int getRepairLimit() {
            return repairLimit.get();
        }

        public void setRepairLimit(int repairLimit) {
            this.repairLimit.set(repairLimit);
        }

        public IntegerProperty repairLimitProperty() {
            return repairLimit;
        }

        public int getRepairTimeLimit() {
            return repairTimeLimit.get();
        }

        public void setRepairTimeLimit(int repairTimeLimit) {
            this.repairTimeLimit.set(repairTimeLimit);
        }

        public IntegerProperty repairTimeLimitProperty() {
            return repairTimeLimit;
        }

        public boolean isCheckFatigue() {
            return checkFatigue.get();
        }

        public void setCheckFatigue(boolean checkFatigue) {
            this.checkFatigue.set(checkFatigue);
        }

        public BooleanProperty checkFatigueProperty() {
            return checkFatigue;
        }

        public boolean isPortCheck() {
            return portCheck.get();
        }

        public void setPortCheck(boolean portCheck) {
            this.portCheck.set(portCheck);
        }

        public BooleanProperty portCheckProperty() {
            return portCheck;
        }

        public boolean isMedalStop() {
            return medalStop.get();
        }

        public void setMedalStop(boolean medalStop) {
            this.medalStop.set(medalStop);
        }

        public BooleanProperty medalStopProperty() {
            return medalStop;
        }

        public boolean isLastNodePush() {
            return lastNodePush.get();
        }

        public void setLastNodePush(boolean lastNodePush) {
            this.lastNodePush.set(lastNodePush);
        }

        public BooleanProperty lastNodePushProperty() {
            return lastNodePush;
        }
    }


    public static class SubmarineSwitch {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "EnabledSubs") private ListProperty<Submarines> enabledSubs;

        public SubmarineSwitch(boolean enabled, List<Submarines> enabledSubs) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.enabledSubs =
                new SimpleListProperty<>(FXCollections.observableArrayList(enabledSubs));
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public ObservableList<Submarines> getEnabledSubs() {
            return enabledSubs.get();
        }

        public void setEnabledSubs(ObservableList<Submarines> enabledSubs) {
            this.enabledSubs.set(enabledSubs);
        }

        public ListProperty<Submarines> enabledSubsProperty() {
            return enabledSubs;
        }
    }


    public static class Lbas {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "EnabledGroups") private SetProperty<Integer> enabledGroups;
        @IniConfig(key = "Group1Nodes") private ListProperty<String> group1Nodes;
        @IniConfig(key = "Group2Nodes") private ListProperty<String> group2Nodes;
        @IniConfig(key = "Group3Nodes") private ListProperty<String> group3Nodes;

        public Lbas(boolean enabled, Set<Integer> enabledGroups, List<String> group1Nodes,
            List<String> group2Nodes, List<String> group3Nodes) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.enabledGroups =
                new SimpleSetProperty<>(FXCollections.observableSet(enabledGroups));
            this.group1Nodes =
                new SimpleListProperty<>(FXCollections.observableArrayList(group1Nodes));
            this.group2Nodes =
                new SimpleListProperty<>(FXCollections.observableArrayList(group2Nodes));
            this.group3Nodes =
                new SimpleListProperty<>(FXCollections.observableArrayList(group3Nodes));
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public ObservableSet<Integer> getEnabledGroups() {
            return enabledGroups.get();
        }

        public void setEnabledGroups(ObservableSet<Integer> enabledGroups) {
            this.enabledGroups.set(enabledGroups);
        }

        public SetProperty<Integer> enabledGroupsProperty() {
            return enabledGroups;
        }

        public ObservableList<String> getGroup1Nodes() {
            return group1Nodes.get();
        }

        public void setGroup1Nodes(ObservableList<String> group1Nodes) {
            this.group1Nodes.set(group1Nodes);
        }

        public ListProperty<String> group1NodesProperty() {
            return group1Nodes;
        }

        public ObservableList<String> getGroup2Nodes() {
            return group2Nodes.get();
        }

        public void setGroup2Nodes(ObservableList<String> group2Nodes) {
            this.group2Nodes.set(group2Nodes);
        }

        public ListProperty<String> group2NodesProperty() {
            return group2Nodes;
        }

        public ObservableList<String> getGroup3Nodes() {
            return group3Nodes.get();
        }

        public void setGroup3Nodes(ObservableList<String> group3Nodes) {
            this.group3Nodes.set(group3Nodes);
        }

        public ListProperty<String> group3NodesProperty() {
            return group3Nodes;
        }
    }


    public static class Quests {
        @IniConfig(key = "Enabled") private BooleanProperty enabled;
        @IniConfig(key = "Quests") private ListProperty<String> quests;
        @IniConfig(key = "CheckSchedule") private IntegerProperty checkSchedule;

        public Quests(boolean enabled, List<String> quests, int checkSchedule) {
            this.enabled = new SimpleBooleanProperty(enabled);
            this.quests = new SimpleListProperty<>(FXCollections.observableArrayList(quests));
            this.checkSchedule = new SimpleIntegerProperty(checkSchedule);
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public ObservableList<String> getQuests() {
            return quests.get();
        }

        public void setQuests(ObservableList<String> quests) {
            this.quests.set(quests);
        }

        public ListProperty<String> questsProperty() {
            return quests;
        }

        public int getCheckSchedule() {
            return checkSchedule.get();
        }

        public void setCheckSchedule(int checkSchedule) {
            this.checkSchedule.set(checkSchedule);
        }

        public IntegerProperty checkScheduleProperty() {
            return checkSchedule;
        }
    }
}
