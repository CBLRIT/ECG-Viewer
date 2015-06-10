#!/bin/bash

javac -encoding ISO-8859-1 $(find . -name "*.java" -not -name "*Test.java")
jar -cf jwave.jar $(find . -name "*.class")
