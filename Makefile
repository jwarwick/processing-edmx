default: all

all:
	javac -d . src/*.java 
	jar -cf library/eDMX.jar eDMX

clean:
	$(RM) library/*
	$(RM) eDMX/*

