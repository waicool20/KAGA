# KAGA
Kancolle Auto Gui App or Kaga - A GUI for the tool over here: https://github.com/mrmin123/kancolle-auto

---
**ALL WARNINGS/DISCLAIMERS FROM KANCOLLE-AUTO ALSO APPLY HERE,  
WE SHALL NOT BE HELD LIABLE FOR THE EXCESSIVE SALT CREATED IF YOU ARE BANNED OR FOR YOUR SUNKEN WAIFUS**

---

# Features

- No more hassling with the command line, just click away admiral!
- More human friendly values instead of pure numbers used to configure Kancolle Auto
- Profile saving, for easy switching between different routines from orel cruising to your 3-2 grind
- Automatic restart of Kancolle Auto, KAGA makes sure those subs return to 2-3 even after an internet hiccup!
- Saves crash logs automatically to <Kancolle Auto Directory>/crashes with details filled in ready for bug reporting
- Prevents lockscreen(configurable) in case you're too lazy to turn off your screensaver
- Tooltips to help you configure Kancolle Auto, press Shift Key while hovering over an option to see what it does
- Statistics to help you know what the script is doing! eg. Buckets used, sorties per hour etc. (See screenshots)

Planned:
- Bundling SikuliX and adding more stats related things like resource expenses by OCR

# Installation and Usage

* A working copy of KAGA, either download from [release page](https://github.com/waicool20/KAGA/releases) or build a copy from the latest commit (Instructions below)
* Install [Java JRE 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
* A working copy of [Kancolle Auto](https://github.com/mrmin123/kancolle-auto)

After you install the dependencies, you may run the JAR executable directly, after which you will be presented a screen to configure paths to the Sikuli Script Jar file and the Kancolle Auto Root directory.
After that the main application will start and you may begin configuring and saving profiles for Kancolle Auto, it's pretty straight forward with Kaga as your secretary!

# Build Instructions

To build the jar file after cloning/pulling the latest commits, go into the repo directory and run the right command

Windows:

> gradlew.bat build

Linux/MacOS:

> gradlew build

Alternatively if you have gradle installed into PATH then you may run:

> gradle build

A compiled Jar file which you can execute directly will be generated in the build/libs directory

# Screenshots

Looks may vary on different OS but should run the same regardless, I'm currently running Arch Linux with Arc Dark theme ;) 
It may also vary between current release and latest commit, so if you want the eye candy learn how to build it from source, it's easy!


Main window and debug window showing the console output of both KAGA and Kancolle Auto:
![Main and Debug window](screenshots/Main_and_Debug_window.png?raw=true)

Configuring the formations for each node is easy with a dropdown list:
![Choosing formations](screenshots/Choosing_formations.png?raw=true)

Same goes for quests, easy configuration with checkboxes:
![Choosing quests](screenshots/Choosing_quests.png?raw=true)

Statistics window for some of you nerds:
![Statistics window](screenshots/Main_and_Stats_window.png?raw=true)


