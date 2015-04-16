ECG Viewer - Opens and manipulates raw ECG data
===============================================
Author: Dakota Williams, drw9888@rit.edu

###Table of Contents

1. Setup
  1. Prerequisites
  2. Compilation
  3. Running
2. Workflow
  1. Opening
  2. Filtering
    1. Detrending
    2. Denoising
  3. Marking Bad Leads
  4. Annotations & Settings
  5. Exporting
    1. Data
    2. Bad Leads
    3. Annotations
3. Acknowledgements

###1. Setup
####1.1. Prerequisites
To run this application, a Java Runtime Environment (JRE) version 1.6 or higher is required.
This program is platform-independent thanks to the Java Virtual Machine, meaning this application is not dependent on the client operating system.
If plugin development is desired, then a Java Development Kit (JDK) version 1.6 or higher is also needed.
For more information about plugins, see section 2.1 of this document.
If compilation of the source code is necessary, then a JDK version 1.6 or higher and GNU `make` is also needed.
If compilation is not necessary, skip section 1.2.
A breakdown of requirements and dependencies are shown in the table below.

    | General Use        | Plugin Development | Application Development
----|--------------------|--------------------|------------------------
JDK | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark:
JRE |                    | :heavy_check_mark: | :heavy_check_mark:
make|                    |                    | :heavy_check_mark:

The latest JDK and JRE are available [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Getting `make`: 
- Windows - Either [MinGW](http://www.mingw.org) (just alias `mingw32-make` to `make`) or [Cygwin](https://www.cygwin.com) and make sure that `java` and `javac` are in `%PATH%`.
- OS X - Install [Xcode](https://developer.apple.com/xcode/), specifically its developer tools.
- Linux - Lucky you! You have it already!

####1.2. Compilation
Compiling the source code is as simple as running `make release`.
This creates a folder called `ECGViewer` in the source directory containing the jar and folders contains the libraries and plugins.
During active development there are other targets for the makefile:

- No target or `default`: Just running `make` will compile all the source files into class files in the current directory.
- `run`: This will run the compiled files with the classpath set to include necessary libraries.
- `debug`: This will start `jdb` with the necessary classpath.
- `clean`: This will delete the compiled class files from the source directory (note: not the plugin directory).
- `realclean`: Does the same thing as `clean` as well as removing the `ECGViewer` directory entirely.

####1.3. Running
To run the program, either double click on `ECGViewer/ECGViewer.jar` to execute it, or, from command line, run the command `java -jar ECGViewer/ECGViewer.jar`.

###2. Workflow
This section goes through a sample workflow of processing a dataset.
To begin, run the program with one of the prescribed methods in section 1.3.
The program should look like this:
![](imgs/1.png?raw=true)

####2.1. Opening

####2.2. Filtering

#####2.2.1. Detrending

#####2.2.2. Denoising

####2.3. Marking Bad Leads

####2.4. Annotations & Settings

####2.5. Exporting

#####2.5.1. Data

#####2.5.2. Bad Leads

#####2.5.3. Annotations

###3. Acknowledgements

This work is supported by the National Science Foundation CAREER Award #ACI-1350374 

Libraries Used

1. <a href="http://www.jfree.org/jfreechart/">JFreeChart</a> v1.0.19 - unmodified  
1. <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Math</a> v3.3 - unmodified  
1. <a href="https://code.google.com/p/savitzky-golay-filter/">savitzky-golay-filter</a> - modified  
  1. SGFilter.java was updated to resolve deprecated dependencies on Apache Commons Math. A copy of the Apache license is contained in the `sgfilter` directory  
1. <a href="https://github.com/cscheiblich/JWave/">JWave</a> - unmodified
