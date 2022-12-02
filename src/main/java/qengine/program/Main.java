package qengine.program;

import qengine.handler.ConsoleArgumentsHandler;
import qengine.parser.Parser;

public final class Main {
	public static final float NS_TO_MS_RATIO = 1000000;

	public static void main(String[] args) throws Exception {
		ProgramEvaluation.init();

		long startTime = System.nanoTime();

		ConsoleArgumentsHandler.handleArguments(args);

		Parser.fullProcess();

		long endTime = System.nanoTime();


		float totalTime = (endTime - startTime) / NS_TO_MS_RATIO;
		System.out.println("Temps d'exécution du programmme : " + totalTime);

		ProgramEvaluation.addToOutputData("TOTAL_TIME", String.valueOf(totalTime));

		ProgramEvaluation.writeOutputDataTOCSV();
	}

}
