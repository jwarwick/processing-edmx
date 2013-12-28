default: all

all:
	javac -d . -classpath /Applications/Processing.app/Contents/Java/core.jar src/*.java 
	jar -cf library/eDMX.jar eDMX

clean:
	$(RM) library/*
	$(RM) eDMX/*

