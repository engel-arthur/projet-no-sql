package qengine.handler;

import org.apache.commons.cli.*;

public final class ConsoleArgumentsHandler {
    /*
                                    Cette classe gère les arguments passées en paramètre du programme.

                                    -queries "/chemin/vers/dossier/requetes"
                                    -data "/chemin/vers/fichier/donnees"
                                    -output "/chemin/vers/dossier/sortie"
                                    -jena : active la vérification de la correction et complétude du système
                                    en utilisant Jena comme un oracle
                                    -warm "X" : utilise un échantillon des requêtes en entrée (prises
                                    au hasard) correspondant au pourcentage "X" pour chauffer le système
                                    -shuffle : considère une permutation aléatoire des requêtes en entrée
                                 */
    private static final Options options = new Options();

    public static final String QUERIES = "queries";
    public static final String OUTPUT = "output";
    public static final String DATA = "data";
    public static final String JENA = "jena";
    public static final String WARM = "warm";
    public static final String SHUFFLE = "shuffle";

    private static final String QUERIES_OPTION_DESCRIPTION = "Chemin vers le fichier contenant les requêtes à traiter";
    private static final String DATA_OPTION_DESCRIPTION = "Chemin vers le fichier contenant les données à traiter";
    private static final String OUTPUT_OPTION_DESCRIPTION = "Chemin vers le dossier de sortie, qui contiendra les résultats des requêtes";
    private static final String JENA_OPTION_DESCRIPTION = "Active la vérification de la correction et complétude du système, en utilisant Jena comme oracle.";
    private static final String WARM_OPTION_DESCRIPTION = "Utilise un pourcentage X de requêtes en échantillon d'entrée pour chauffer le système";
    private static final String SHUFFLE_OPTION_DESCRIPTION = "Permute aléatoirement les requêtes en entrée";


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
    }

    private static CommandLine parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static void handleOptions(CommandLine cmd) {
        if(cmd.hasOption(QUERIES)) {
            String queriesFilepath = cmd.getOptionValue(QUERIES);
            //set query path
        }
        if(cmd.hasOption(DATA)) {
            String dataFilepath = cmd.getOptionValue(DATA);
            //set data path
        }
        if(cmd.hasOption(OUTPUT)) {
            String outputPath = cmd.getOptionValue(OUTPUT);
            //set output path
        }
        if(cmd.hasOption(JENA)) {
            //lancer fonction jena
        }
        if(cmd.hasOption(WARM)) {
            int warmPercentage = Integer.parseInt(cmd.getOptionValue(WARM));
            //lancer fonction warm
        }
        if(cmd.hasOption(SHUFFLE)) {
            //lancer fonction shuffle
        }
    }
}
