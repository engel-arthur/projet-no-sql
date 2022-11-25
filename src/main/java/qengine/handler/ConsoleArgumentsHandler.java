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

    private static final String queriesOptionDescription = "Chemin vers le fichier contenant les requêtes à traiter";
    private static final String dataOptionDescription = "Chemin vers le fichier contenant les données à traiter";
    private static final String outputOptionDescription = "Chemin vers le dossier de sortie, qui contiendra les résultats des requêtes";
    private static final String jenaOptionDescription = "Active la vérification de la correction et complétude du système, en utilisant Jena comme oracle.";
    private static final String warmOptionDescription = "Utilise un pourcentage X de requêtes en échantillon d'entrée pour chauffer le système";
    private static final String shuffleOptionDescription = "Permute aléatoirement les requêtes en entrée";


    private ConsoleArgumentsHandler(){}
    public static void handleArguments(String[] args) throws ParseException {
        defineOptions();
        CommandLine cmd = parseOptions(args);
        handleOptions(cmd);
    }

    private static void defineOptions() {
        options.addOption(QUERIES, true, queriesOptionDescription);
        options.addOption(DATA, true, dataOptionDescription);
        options.addOption(OUTPUT, true, outputOptionDescription);
        options.addOption(JENA, false, jenaOptionDescription);
        options.addOption(WARM, true, warmOptionDescription);
        options.addOption(SHUFFLE, false, shuffleOptionDescription);
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
