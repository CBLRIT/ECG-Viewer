
LIBPATH=".:ImageJ/ij.jar:jfreechart-1.0.19/lib/jfreechart-1.0.19.jar:jfreechart-1.0.19/lib/jcommon-1.0.23.jar:commons-math3-3.3/commons-math3-3.3.jar:sgfilter/sgfilter_v1_2.jar"

LIBS=$(shell echo $(LIBPATH) | sed -e 's/:/ /g' | sed -e 's/\. //g')
#LIBS=$(shell echo $(LIBPATH) | awk -v w=. '($0~w)' RS=':' ORS=' ')

default:
	javac -g -cp $(LIBPATH) *.java

run:
	java -cp $(LIBPATH) Main &

urn: run

debug:
	jdb -classpath $(LIBPATH) Main

unchecked:
	javac -g -cp $(LIBPATH) *.java -Xlint:unchecked

release:
	javac -cp $(LIBPATH) *.java
	echo "Main-Class: Main" > manifest.txt
	echo "Class-Path: $(LIBS)" >> manifest.txt
	jar cfm ECGViewer.jar manifest.txt *.class $(LIBS)
	chmod u+x ECGViewer.jar
	make clean
	rm manifest.txt

#Main.class: MoldTorso.class Main.java
#	javac -cp $(LIBPATH) Main.java
#
#MoldTorso.class: MoldTorso.java Mesh.class
#	javac -cp $(LIBPATH) MoldTorso.java
#
#Mesh.class: Mesh.java UnalignedCoordinateException.class
#	javac -cp $(LIBPATH) Mesh.java
#
#UnalignedCoordinateException.class: UnalignedCoordinateException.java
#	javac -cp $(LIBPATH) UnalignedCoordinateException.java

clean:
	rm *.class

realclean:
	rm *.class ECGViewer.jar

