package qengine.program;

import qengine.dictionary.Dictionary;
import qengine.handler.ConsoleArgumentsHandler;
import qengine.index.IndexCollection;
import qengine.parser.Parser;

public final class Main {
	private static int warmPercentage = 100;
	public static void main(String[] args) throws Exception {
		ConsoleArgumentsHandler.handleArguments(args);

		Dictionary dictionary = Dictionary.getInstance();
		IndexCollection hexastore = IndexCollection.getInstance();

		Parser.parseData();
		Parser.parseQueries(dictionary, hexastore, warmPercentage);
	}

	public static int getWarmPercentage() {
		return warmPercentage;
	}

	public static void setWarmPercentage(int warmPercentage) {
		//TODO remplacer éventuellement par des exceptions
		if(warmPercentage > 100)
			Main.warmPercentage = 100;
		else if(warmPercentage < 0)
			Main.warmPercentage = 0;
		else
			Main.warmPercentage = warmPercentage;
	}
}
