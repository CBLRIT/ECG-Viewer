
LIBPATH=ImageJ/ij.jar

default: LoadImages Main.java
	javac -cp $(LIBPATH) Main.java

LoadImages: LoadImages.java
	javac -cp $(LIBPATH) LoadImages.java

clean:
	rm *.class
