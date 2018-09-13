# KAGA
Kancolle Auto Gui App or KAGA 

A GUI for the tool over here: https://github.com/mrmin123/kancolle-auto (Versions below 2.0.0)  
And here: https://github.com/mrmin123/kcauto (Versions 2.0.0 and above)

---
**ALL WARNINGS/DISCLAIMERS FROM KANCOLLE-AUTO ALSO APPLY HERE,  
WE SHALL NOT BE HELD LIABLE FOR THE EXCESSIVE SALT CREATED IF YOU ARE BANNED OR FOR YOUR SUNKEN WAIFUS**

---

# Features

- No more hassling with the command line, just click away admiral!
- More human friendly values instead of pure numbers used to configure KCAuto
- Profile saving, for easy switching between different routines 
- Automatic restart of KCAuto, ~~KAGA makes sure those subs return to 2-3 even after an internet hiccup!~~ Orel is dead T_T
- Saves crash logs automatically to <KCAuto Directory>/crashes with details filled in ready for bug reporting
- Prevents lockscreen(configurable) in case you're too lazy to turn off your screensaver
- Tooltips to help you configure KCAuto, press Shift Key while hovering over an option to see what it does
- Statistics to help you know what the script is doing! eg. Buckets used, sorties per hour etc. (See screenshots)
- Discord integration, basic crash notification and script status queries via YuuBot, a cute bot that resides in our server.
- Global shortcut key that can be rebinded to start and stop the script even when your mouse is out of control.
- KC3 Ship list importer for convenience when configuring the ShipSwitcher module.

# Installation and Usage

* A working copy of KAGA:
    * You can find official releases on the [Release Page](https://github.com/waicool20/KAGA/releases) (Recommended) 
    * You can find per-commit based builds on [AppVeyor](https://ci.appveyor.com/project/waicool20/kaga) (Possibly unstable but bleeding edge)
    * You can clone and build a copy from the latest commit (Possibly unstable but bleeding edge, instructions below)
* Install [Java JRE 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
* A working copy of [KCAuto](https://github.com/mrmin123/kcauto)

After you install the dependencies, you may run the JAR executable directly, after which you will be presented a screen to configure paths to the Sikuli Script Jar file and the KCAuto Root directory.
After that the main application will start and you may begin configuring and saving profiles for KCAuto, it's pretty straight forward with Kaga as your secretary!

# Help

Refer to the [wiki](https://github.com/waicool20/kaga/wiki) on how to use some features like on how to import ships from KC3 for the ship switcher module
or how to bind shortcut keys.

If you have any further questions, you're welcome to contact me on Discord (Invite below)

# Build Instructions

Prerequisites: 

* [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) is needed to build KAGA as opposed to Java JRE 8
* [My Utility Library](https://github.com/waicool20/waicoolUtils), place this inside the project directory, you don't need to do this if you are cloning and pulling all submodules

Git clone instructions: 

```bash
git clone https://github.com/waicool20/KAGA.git     // Clone repository, replace with ssh url if you prefer that  
cd KAGA/                                            // Make sure you are in the KAGA directory
git submodule update --init --recursive             // Pull all submodules, this includes the utility library
```

To build the jar file after cloning/pulling the latest commits, go into the repo directory and run the right command

Windows:

> gradlew.bat

Linux/MacOS:

> ./gradlew

A compiled Jar file which you can execute directly will be generated in the build/libs directory

# Screenshots

Looks may vary on different OS but should run the same regardless, I'm currently running Arch Linux with Arc Dark theme ;) 
It may also vary between current release and latest commit, so if you want the eye candy learn how to build it from source, it's easy!


Main window and debug window showing the console output of both KAGA and KCAuto:
![Main and Debug window](screenshots/Main_and_Debug_window.png?raw=true)

Configuring the formations for each node is easy with a dropdown list:
![Choosing formations](screenshots/Choosing_formations.png?raw=true)

Statistics window for some of you nerds:
![Statistics window](screenshots/Main_and_Stats_window.png?raw=true)

Discord integration available via YuuBot on our discord server. Crash notifications and queries can be executed to check on your scripts status. For more information [go check out the wiki page on Discord integration.](https://github.com/waicool20/KAGA/wiki/KAGA-Discord-Integration)
![Discord integration](screenshots/Discord_Integration.png?raw=true)

# Troubleshooting

Check out the [Troubleshooting wiki page](https://github.com/waicool20/KAGA/wiki)

# Want to chat?

Want to chat? Or just ask a quick question instead of submitting a full blown issue? Or maybe you just want to share your waifu...
well then you're welcome to join us in Discord:
 
[<img src="https://discordapp.com/assets/fc0b01fe10a0b8c602fb0106d8189d9b.png" alt="alt text" width="200px">](https://discord.gg/2tt5Der)
