export JAVA_TOOL_OPTIONS += -Dfile.encoding=UTF8
Out="output.txt"
Url="http://rostov.life-realty.ru/region/azov/sale/?view=simple&page="
PageCount=1

all:
	scalac -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." ./src/*.scala ./src/rt/service/Items/*.scala ./src/rt/service/Parsers/*.scala

run:
	scala -J-Xmx1g -classpath "./lib/commons-lang3-3.1.jar:./lib/htmlcleaner-2.6.1.jar:." rtService ${Url} ${PageCount} ${Out}
clean:
	find . -name "*.class" | xargs rm
	rm ${Out}
