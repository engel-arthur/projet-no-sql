package qengine.handler;

import org.apache.commons.cli.*;
import qengine.parser.Parser;

/*
 * This class handles the arguments passed as parameters of the program
 *
 * Here are the (soon to be) supported options :
 * -queries : path to the queries file
 * -data : path to the data file
 * -output : path to the output folder
 * -jena (flag) : activates verification using Jena
 * -warm "X" : warms up the system for performance testing
 * -shuffle : randomly permutates the queries
 * */

public final class ConsoleArgumentsHandler {
    private static final Options options = new Options();

    public static final String QUERIES = "queries";
    public static final String OUTPUT = "output";
    public static final String DATA = "data";
    public static final String JENA = "jena";
    public static final String WARM = "warm";
    public static final String SHUFFLE = "shuffle";

    public static final String EXPORT_QUERY_RESULTS = "export_query_results";

    private static final String QUERIES_OPTION_DESCRIPTION = "Chemin vers le fichier contenant les requêtes à traiter";
    private static final String DATA_OPTION_DESCRIPTION = "Chemin vers le fichier contenant les données à traiter";
    private static final String OUTPUT_OPTION_DESCRIPTION = "Chemin vers le dossier de sortie, qui contiendra les résultats des requêtes";
    private static final String JENA_OPTION_DESCRIPTION = "Active la vérification de la correction et complétude du système, en utilisant Jena comme oracle.";
    private static final String WARM_OPTION_DESCRIPTION = "Utilise un pourcentage X de requêtes en échantillon d'entrée pour chauffer le système";
    private static final String SHUFFLE_OPTION_DESCRIPTION = "Permute aléatoirement les requêtes en entrée";

    private static final String EXPORT_QUERY_RESULTS_OPTION_DESCRIPTION = "Exporte les résultats des requêtes dans un ficher CSV";

    private ConsoleArgumentsHandler(){}
    public static void handleArguments(String[] args) throws ParseException {
        defineOptions();
        CommandLine cmd = parseOptions(args);
        handleOptions(cmd);
    }

    private static void defineOptions() {
        options.addOption(QUERIES, true, QUERIES_OPTION_DESCRIPTION);
        options.addOption(DATA, true, DATA_OPTION_DESCRIPTION);
        options.addOption(OUTPUT, true, OUTPUT_OPTION_DESCRIPTION);
        options.addOption(JENA, false, JENA_OPTION_DESCRIPTION);
        options.addOption(WARM, true, WARM_OPTION_DESCRIPTION);
        options.addOption(SHUFFLE, false, SHUFFLE_OPTION_DESCRIPTION);
        options.addOption(EXPORT_QUERY_RESULTS, false, EXPORT_QUERY_RESULTS_OPTION_DESCRIPTION);
    }

    private static CommandLine parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static void handleOptions(CommandLine cmd) {
        if(cmd.hasOption(QUERIES)) {
            String queriesFilepath = cmd.getOptionValue(QUERIES);
            Parser.setQueryFile(queriesFilepath);
        }
        if(cmd.hasOption(DATA)) {
            String dataFilepath = cmd.getOptionValue(DATA);
            Parser.setDataFile(dataFilepath);
        }
        if(cmd.hasOption(OUTPUT)) {
            String outputPath = cmd.getOptionValue(OUTPUT);
            Parser.setOutputPath(outputPath);
        }
        if(cmd.hasOption(JENA)) {
            Parser.setJenaEnabled(true);
        }
        if(cmd.hasOption(WARM)) {
            int warmPercentage = Integer.parseInt(cmd.getOptionValue(WARM));
            Parser.setWarmPercentage(warmPercentage);
        }
        if(cmd.hasOption(SHUFFLE)) {
            Parser.setShuffle(true);
        }
        if (cmd.hasOption(EXPORT_QUERY_RESULTS)) {
            Parser.setExportEnabled(true);
        }
    }
}
