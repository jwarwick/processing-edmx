default: all

all:
	javac -d build -classpath /Applications/Processing.app/Contents/Java/core.jar src/*.java 
	jar cf library/eDMX.jar -C build eDMX

clean:
	$(RM) library/*
	$(RM) -rf build/*

package:
	zip -r bin/eDMX.zip eDMX -x "eDMX/library/.gitignore" 

