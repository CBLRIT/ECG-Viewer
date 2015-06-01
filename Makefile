
LIBPATH=".:jfreechart-1.0.19/lib/jfreechart-1.0.19.jar:jfreechart-1.0.19/lib/jcommon-1.0.23.jar:commons-math3-3.3/commons-math3-3.3.jar:sgfilter/sgfilter_v1_2.jar:JWave/src/jwave.jar"

LIBS=$(shell echo $(LIBPATH) | sed -e 's/:/ /g' | sed -e 's/\. //g')
#LIBS=$(shell echo $(LIBPATH) | awk -v w=. '($0~w)' RS=':' ORS=' ')

default:
	javac -g -cp $(LIBPATH) *.java

run:
	java -cp $(LIBPATH) Main &

debug:
	jdb -classpath $(LIBPATH) Main

unchecked:
	javac -g -cp $(LIBPATH) *.java -Xlint:unchecked

release:
	make -i realclean
	mkdir ECGViewer
	mkdir ECGViewer/libs
	for lib in $(LIBS); do \
		cp $$lib ECGViewer/libs/ ; \
	done
	cp -R plugins/ ECGViewer/
	cp manifest.txt ECGViewer/
	cp README.md ECGViewer/
	mkdir ECGViewer/imgs/
	cp -R imgs/toolbarButtonGraphics/ ECGViewer/imgs/toolbarButtonGraphics/
	cd ECGViewer; \
	javac -cp .:$(shell cd ECGViewer; ls -1 libs/*.jar | sed -e ':a;N;$$!ba;s/\s/:/g') ../*.java -d ./; \
	jar cfm ECGViewer.jar manifest.txt *.class imgs/
	chmod u+x ECGViewer/ECGViewer.jar
	rm ECGViewer/*.class ECGViewer/manifest.txt
	rm -rf ECGViewer/imgs

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
	rm *.class
	rm -rf ECGViewer

