export JAVA_TOOL_OPTIONS += -Dfile.encoding=UTF8
all:
	scalac -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." ./src/*.scala ./src/rt/service/Items/*.scala ./src/rt/service/Parsers/*.scala

run:
	scala -J-Xmx1g -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." rtService 1 output.txt
clean:
	find . -name "*.class" | xargs rm
	rm output.txt
