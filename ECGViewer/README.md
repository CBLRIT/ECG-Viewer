<a name="top"></a>ECG Viewer - Opens and manipulates raw ECG data
===============================================
Author: Dakota Williams, drw9888@rit.edu

##Table of Contents

1. [Setup](#1)
  1. [Prerequisites](#1.1)
  2. [Compilation](#1.2)
  3. [Running](#1.3)
2. [Workflow](#2)
  1. [Opening](#2.1)
  2. [Filtering](#2.2)
    1. [Detrending](#2.2.1)
    2. [Denoising](#2.2.2)
  3. [Marking Bad Leads](#2.3)
  4. [Annotations](#2.4)
  5. [Exporting](#2.5)
    1. [Data](#2.5.1)
    2. [Bad Leads](#2.5.2)
    3. [Annotations](#2.5.3)
3. [Acknowledgements](#3)

##<a name="1"></a>1. Setup [[top](#top)]
###<a name="1.1"></a>1.1. Prerequisites [[top](#top)]
To run this application, a Java Runtime Environment (JRE) version 1.6 or higher is required.
This program is platform-independent thanks to the Java Virtual Machine, meaning this application is not dependent on the client operating system.
If plugin development is desired, then a Java Development Kit (JDK) version 1.6 or higher is also needed.
For more information about plugins, see section [2.1](#2.1) of this document.
If compilation of the source code is necessary, then a JDK version 1.6 or higher and GNU `make` is also needed.
If compilation is not necessary, skip section 1.2.
A breakdown of requirements and dependencies are shown in the table below.

    | General Use        | Plugin Development | Application Development
---:|:------------------:|:------------------:|:----------------------:
JDK | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark:
JRE |                    | :heavy_check_mark: | :heavy_check_mark:
make|                    |                    | :heavy_check_mark:

The latest JDK and JRE are available [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Getting `make`: 
- Windows - Either [MinGW](http://www.mingw.org) (just alias `mingw32-make` to `make`) or [Cygwin](https://www.cygwin.com) and make sure that `java` and `javac` are in `%PATH%`.
- OS X - Install [Xcode](https://developer.apple.com/xcode/), specifically its developer tools.
- Linux - Lucky you! You have it already!

###<a name="1.2"></a>1.2. Compilation [[top](#top)]
Compiling the source code is as simple as running `make release`.
This creates a folder called `ECGViewer` in the source directory containing the jar and folders contains the libraries and plugins.
During active development there are other targets for the makefile:

- No target or `default`: Just running `make` will compile all the source files into class files in the current directory.
- `run`: This will run the compiled files with the classpath set to include necessary libraries.
- `debug`: This will start `jdb` with the necessary classpath.
- `clean`: This will delete the compiled class files from the source directory (note: not the plugin directory).
- `realclean`: Does the same thing as `clean` as well as removing the `ECGViewer` directory entirely.

###<a name="1.3"></a>1.3. Running [[top](#top)]
To run the program, either double click on `ECGViewer/ECGViewer.jar` to execute it, or, from command line, run the command `java -jar ECGViewer/ECGViewer.jar`.

##<a name="2"></a>2. Workflow [[top](#top)]
This section goes through a sample workflow of processing a dataset.
To begin, run the program with one of the prescribed methods in section 1.3.
The program should look like this:
![](imgs/1.png?raw=true)

###<a name="2.1"></a>2.1. Opening [[top](#top)]
There are two options for opening a file, opening the whole file and opening a subset of a file.
To open a whole file, go to `File->Open...` which will present a dialog like this:
![](imgs/2-1.png?raw=true)

Opening a subset of a file is a bit different.
Using `File->Open Subset...` will show a dialog
![](imgs/3-1.png?raw=true) 

like this.
The two text boxes on the left side of the dialog specify the time into the dataset that begins the subset and how long the subset is, respectively.  Both of these times are measured in milliseconds.

Currently, the supported file types include .dat and .123 files.
For a more in depth analysis of these files, see `plugins/DATFile.java` and `plugins/_123File.java`.
More file types can be read in by creating plugins.
For more information on creating plugins, see [`plugins/README`](plugins/README).

After loading the file, the main window should display the leads as graphs.
![](imgs/4.png?raw=true)

###<a name="2.2"></a>2.2. Filtering [[top](#top)]
Applying a filter can be done two ways: To an individual lead, or to all the leads at once.
Applying a filter to all the leads in done in the main window from the `Filter All` menu.
Selecting a method from there will display a preview dialog of a single lead and parameter sliders for that filter.
After clicking OK, the filter will be applied to all leads.

The other way to apply a filter is to one lead, individually. 
Clicking on a graph will produce a window as shown below.
![](imgs/5.png?raw=true)

Controls for navigating the graph:
- Click-n-drag (upper-left to lower-right): Zoom in on area
- Click-n-drag (lower-right to upper-left): Zoom out
- Ctrl + click-n-drag: Pan

The lower text boxes allow for manual focus of the graph.
`Start offset` sets the where the left side of the graph is aligned.
`Length` sets how much the x-axis contains.

The menu `Filter` on this lead window has all of the filter options available to filter just this lead.

####<a name="2.2.1"></a>2.2.1. Detrending [[top](#top)]
The main purpose of detrending is to bring the data to a baseline so it can accurately represent the data.
The detrending options can be found on the first half of the `Filter` menu, before the separator.

- Detrend: A polynomial fit, the order of the polynomial is asked for in the dialog.
- Constant Offset: Shifts the entire signal by a constant value.

####<a name="2.2.2"></a>2.2.2. Denoising [[top](#top)]
Denoising a signal removes extraneous data and leaves the more important parts of the signal.
The denoising options are found on the second half of the `Filter` menu, after the separator.
Some solid options include:

- Savitsky-Golay filter: Does a good job at smoothing the signal, however the morphology may change.
- FFT: Has a sweet spot for minimizing noise, however if too low, the filter may lose information.
- Wavelet: Solid all-around choice.
- Butterworth: Finicky, but works well.

###<a name="2.3"></a>2.3. Marking Bad Leads [[top](#top)]
Picking out bad leads must be done individually to each lead.
To do so, click on the lead's graph in the main window to open that lead's window.
On that lead's menu, click `Dataset->Bad Lead`.
Doing so will set the background of that lead to red, indicating it is bad.

There is an option to interpolate bad leads with their direct neighbors.
In the settings panel (Main Window `File->Settings`), check `Interpolate Bad Leads?` to activate this functionality.

###<a name="2.4"></a>2.4. Annotations [[top](#top)]
Annotations are used to mark places of interest in the signal.
Four designated titles currently exist:

1. P-wave,
2. QRS-complex,
3. R-wave,
4. T-wave

They represent the major features in a single beat.
Annotations are placed on an individual lead, but apply to all leads.
For example, if an annotation is set on lead a, then on lead b, the same annotations appear.

Placing an annotation is a two-step procedure.
First, make sure `Annotation->Place Annotations` is checked.
This disables the graph navigation function and enables placing annotations.
Second, selecting the correct type of annotation from the `Annotation` menu and clicking on the graph where the annotation should be placed will add a new annotation.
To enable graph navigation, uncheck `Annotation->Place Annotations`.

There is a feature to auto annotate the R-waves (highest peaks) in the signal.
To use this, click `Annotation->Find R-Waves`.

To clear all annotations, use `Annotation->Clear`.

Customizing annotation colors can be set from the settings panel.

###<a name="2.5"></a>2.5. Exporting [[top](#top)]
Exporting data allows it to be used in other programs and routines not a part of this application.
All export methods can be found in the `File` menu of the main window.

####<a name="2.5.1"></a>2.5.1. Data [[top](#top)]
Like opening data, exporting data can be done two ways: All at once, or just a subset.
To export all of the data, use `File->Export...`.
To export just a subset of the data, use `File-Export Subset...`.
The subset bounds are defined by the two text boxes at the bottom of the main window.
In both of these methods, the file format must be chosen from the drop-down in the export dialog.

Currently, there is one way of exporting data.
The data is exported into a file of comma separated values (CSV), where first row is the lead number, and each subsequent row is the time followed by the value for that lead at that time.
Even though the drop-down provides a MATLAB matrix option, do not use it since it does not work.

####<a name="2.5.2"></a>2.5.2. Bad Leads [[top](#top)]
Exporting bad leads is done by `File->Export Bad Leads...`.
The file output is all of the lead numbers of the bad leads, one per line.

####<a name="2.5.3"></a>2.5.3. Annotations [[top](#top)]
Exporting annotations is invoked by `File->Export Annotations...`.
Each annotation is associated with a number in order of how they appear in the menu (P-wave -> 0, QRS -> 1, etc.).
These numbers are stored with there temporal position in the signal, one per line.

For example, an R-wave (3rd in menu -> 2) annotation was placed at time 1000.
When the annotations are exported, then the file would have a line that looks like `2.0 1000.0`.

##<a name="3"></a>3. Acknowledgements [[top](#top)]
This work is supported by the National Science Foundation CAREER Award #ACI-1350374 

Libraries Used

1. <a href="http://www.jfree.org/jfreechart/">JFreeChart</a> v1.0.19 - unmodified  
1. <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Math</a> v3.3 - unmodified  
1. <a href="https://code.google.com/p/savitzky-golay-filter/">savitzky-golay-filter</a> - modified  
  1. SGFilter.java was updated to resolve deprecated dependencies on Apache Commons Math. A copy of the Apache license is contained in the `sgfilter` directory  
1. <a href="https://github.com/cscheiblich/JWave/">JWave</a> - unmodified
