package qengine.program;

import qengine.handler.ConsoleArgumentsHandler;
import qengine.parser.Parser;

public final class Main {
	public static void main(String[] args) throws Exception {
		ConsoleArgumentsHandler.handleArguments(args);

		Parser.fullProcess();
	}

}
