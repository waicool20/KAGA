# KAGA
Kancolle Auto Gui App or KAGA 

A GUI for the tool over here: https://github.com/mrmin123/kancolle-auto (Versions below 2.0.0)  
And here: https://github.com/mrmin123/kcauto-kai (Versions 2.0.0 and above)

---
**ALL WARNINGS/DISCLAIMERS FROM KANCOLLE-AUTO ALSO APPLY HERE,  
WE SHALL NOT BE HELD LIABLE FOR THE EXCESSIVE SALT CREATED IF YOU ARE BANNED OR FOR YOUR SUNKEN WAIFUS**

---

# Features

- No more hassling with the command line, just click away admiral!
- More human friendly values instead of pure numbers used to configure KCAuto-Kai
- Profile saving, for easy switching between different routines from orel cruising to your 3-2 grind
- Automatic restart of KCAuto-Kai, KAGA makes sure those subs return to 2-3 even after an internet hiccup!
- Saves crash logs automatically to <KCAuto-Kai Directory>/crashes with details filled in ready for bug reporting
- Prevents lockscreen(configurable) in case you're too lazy to turn off your screensaver
- Tooltips to help you configure KCAuto-Kai, press Shift Key while hovering over an option to see what it does
- Statistics to help you know what the script is doing! eg. Buckets used, sorties per hour etc. (See screenshots)

Currently under development:
- UI renewal in certain submenus
- Discord integration for notifications on crashes and resource queries using YuuBot, remember to join our [Discord Server](#want-to-chat)

# Installation and Usage

* A working copy of KAGA, either download from [release page](https://github.com/waicool20/KAGA/releases) or build a copy from the latest commit (Instructions below)
* Install [Java JRE 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
* A working copy of [KCAuto-Kai](https://github.com/mrmin123/kcauto-kai)

After you install the dependencies, you may run the JAR executable directly, after which you will be presented a screen to configure paths to the Sikuli Script Jar file and the KCAuto-Kai Root directory.
After that the main application will start and you may begin configuring and saving profiles for KCAuto-Kai, it's pretty straight forward with Kaga as your secretary!

# Build Instructions

Prerequisites: 

* [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) is needed to build KAGA as opposed to Java JRE 8

To build the jar file after cloning/pulling the latest commits, go into the repo directory and run the right command

Windows:

> gradlew.bat

Linux/MacOS:

> ./gradlew

A compiled Jar file which you can execute directly will be generated in the build/libs directory

# Screenshots

Looks may vary on different OS but should run the same regardless, I'm currently running Arch Linux with Arc Dark theme ;) 
It may also vary between current release and latest commit, so if you want the eye candy learn how to build it from source, it's easy!


Main window and debug window showing the console output of both KAGA and KCAuto-Kai:
![Main and Debug window](screenshots/Main_and_Debug_window.png?raw=true)

Configuring the formations for each node is easy with a dropdown list:
![Choosing formations](screenshots/Choosing_formations.png?raw=true)

Statistics window for some of you nerds:
![Statistics window](screenshots/Main_and_Stats_window.png?raw=true)

Discord integration available via YuuBot on our discord server. Crash notifications and queries can be executed to check on your scripts status. For more information [go check out the wiki page on Discord integration.](https://github.com/waicool20/KAGA/wiki/KAGA-Discord-Integration)
![Discord integration](https://camo.githubusercontent.com/67060cb4367408a5cf70613cac2cc9f8492ac9b5/68747470733a2f2f692e696d6775722e636f6d2f3639636d6175452e706e673f7261773d74727565)

# Troubleshooting

Check out the [Troubleshooting wiki page](https://github.com/waicool20/KAGA/wiki)

# Want to chat?

Want to chat? Or just ask a quick question instead of submitting a full blown issue? Or maybe you just want to share your waifu...
well then you're welcome to join us in Discord:
 
[<img src="https://discordapp.com/assets/fc0b01fe10a0b8c602fb0106d8189d9b.png" alt="alt text" width="200px">](https://discord.gg/2tt5Der)
