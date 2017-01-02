# KAGA
Kancolle Auto Gui App or Kaga - A GUI for the tool over here: https://github.com/mrmin123/kancolle-auto

---
**ALL WARNINGS/DISCLAIMERS FROM KANCOLLE-AUTO ALSO APPLY HERE,  
WE SHALL NOT BE HELD LIABLE FOR THE EXCESSIVE SALT CREATED IF YOU ARE BANNED OR FOR YOUR SUNKEN WAIFUS**

---

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


