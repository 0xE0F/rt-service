export JAVA_TOOL_OPTIONS += -Dfile.encoding=UTF8
all:
	scalac -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." *.scala 
run:
	scala -J-Xmx1g -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." rtService 4 output.txt
clean:
	find . -name "*.class" | xargs rm
	rm output.txt
