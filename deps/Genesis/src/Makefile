Interpreter: GenesisInterpreter.java Evaluator.java Parser.java Node.java GenesisIO.java GenesisVars.java GenesisList.java Utility.java Scope.java Quote.java
	javac -Xlint:unchecked GenesisInterpreter.java Evaluator.java Token.java Parser.java Node.java GenesisIO.java GenesisVars.java GenesisList.java Utility.java Token.java Scope.java Quote.java SourcePgm.java

Parser: Parser.java Node.java GenesisIO.java GenesisVars.java GenesisList.java Utility.java Token.java
	javac   Parser.java Node.java GenesisIO.java GenesisVars.java GenesisList.java Utility.java Token.java

clean:
	@rm -f *.class 2> /dev/null

test: Genesis*.java Scope.java Node.java Utility.java Token.java Parser.java test.java
	javac Genesis*.java Scope.java Node.java Utility.java Token.java Parser.java test.java

test2: Genesis*.java Scope.java Node.java Utility.java test2.java
	javac Genesis*.java Scope.java Node.java Utility.java test2.java
zip: 
	cd book; make; cd ..; makezip
rebuild: rebuild.o
	gcc rebuild.o -o rebuild
	chmod 4755 rebuild
checkmail: checkmail.o
	gcc checkmail.o -o checkmail
	chmod 4755 checkmail
