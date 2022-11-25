package qengine.program;

import qengine.dictionary.Dictionary;
import qengine.handler.ConsoleArgumentsHandler;
import qengine.index.IndexCollection;
import qengine.parser.Parser;

final class Main {
	public static void main(String[] args) throws Exception {
		ConsoleArgumentsHandler.handleArguments(args);

		Dictionary dictionary = Dictionary.getInstance();
		IndexCollection hexastore = IndexCollection.getInstance();

		Parser.parseData();
		Parser.parseQueries(dictionary, hexastore);
	}

}
