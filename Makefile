all:
	scalac -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." *.scala 
run:
	scala -J-Xmx1g -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." rtService
clean:
	rm *.class
