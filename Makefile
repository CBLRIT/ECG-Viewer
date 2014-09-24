
LIBPATH=ImageJ/ij.jar

default:
	javac -cp $(LIBPATH) *.java

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
