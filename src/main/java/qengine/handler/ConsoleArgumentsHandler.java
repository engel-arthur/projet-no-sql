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

    private static final String queriesOptionDescription = "Chemin vers le fichier contenant les requêtes à traiter";
    private static final String dataOptionDescription = "Chemin vers le fichier contenant les données à traiter";
    private static final String outputOptionDescription = "Chemin vers le dossier de sortie, qui contiendra les résultats des requêtes";
    private static final String jenaOptionDescription = "Active la vérification de la correction et complétude du système, en utilisant Jena comme oracle.";
    private static final String warmOptionDescription = "Utilise un pourcentage X de requêtes en échantillon d'entrée pour chauffer le système";
    private static final String shuffleOptionDescription = "Permute aléatoirement les requêtes en entrée";

    public static void handleArguments(String[] args) throws ParseException {
        defineOptions();
        CommandLine cmd = parseOptions(args);
        handleOptions(cmd);
    }

    private static void defineOptions() {
        options.addOption("queries", true, queriesOptionDescription);
        options.addOption("data", true, dataOptionDescription);
        options.addOption("output", true, outputOptionDescription);
        options.addOption("jena", false, jenaOptionDescription);
        options.addOption("warm", true, warmOptionDescription);
        options.addOption("shuffle", false, shuffleOptionDescription);
    }

    private static CommandLine parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        return cmd;
    }

    private static void handleOptions(CommandLine cmd) {
        if(cmd.hasOption("queries")) {
            String queriesFilepath = cmd.getOptionValue("queries");
            //set query path
        }
        if(cmd.hasOption("data")) {
            String dataFilepath = cmd.getOptionValue("data");
            //set data path
        }
        if(cmd.hasOption("output")) {
            String outputPath = cmd.getOptionValue("output");
            //set output path
        }
        if(cmd.hasOption("jena")) {
            //lancer fonction jena
        }
        if(cmd.hasOption("warm")) {
            int warmPercentage = Integer.parseInt(cmd.getOptionValue("warm"));
            //lancer fonction warm
        }
        if(cmd.hasOption("shuffle")) {
            //lancer fonction shuffle
        }
    }
}
