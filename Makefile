all: gen bin bin/parser



bin/parser: gen/parser.yy.c gen/parser.tab.o src/parser.cc
	g++ -g -o bin/parser gen/parser.tab.o gen/parser.yy.c src/parser.cc

gen/parser.yy.c: src/parser.ll gen/parser.tab.cc
	win_flex -o gen/parser.yy.c src/parser.ll

gen/parser.tab.o: gen/parser.tab.cc
	g++ -g -c -o gen/parser.tab.o gen/parser.tab.cc

gen/parser.tab.cc: src/parser.yy gen
	win_bison -d src/parser.yy -o gen/parser.tab.cc


gen:
	mkdir gen

bin:
	mkdir bin
