package qengine.parser;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import qengine.dictionary.Dictionary;
import qengine.handler.DataHandler;
import qengine.handler.QueryHandler;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/*
 * This class handles the parsing of the files containing the data and the queries
 * */
public final class Parser {// ========================================================================

    private static final String baseURI = null;

    /**
     * Votre répertoire de travail où vont se trouver les fichiers à lire
     */
    private static final String workingDir = "data/";

    private static String queryFile = workingDir + "sample_query.queryset";

    private static String dataFile = workingDir + "100K.nt";

    private static String outputPath = workingDir + "out";

    private static final ArrayList<String> rawQueries = new ArrayList<>();
    private static final ArrayList<ParsedQuery> parsedQueries = new ArrayList<>();
    private static final ArrayList<HashSet<Integer>> queriesResults = new ArrayList<>();


    private static int warmPercentage = 100;

    private static boolean shuffle = false;

    // ========================================================================
    public static void parseQueries() throws IOException {

        try (Stream<String> lineStream = Files.lines(Paths.get(getQueryFile()))) {

            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryStringBuilder = new StringBuilder();


            while (lineIterator.hasNext()) {

                String line = lineIterator.next();
                //On filtre les lignes vides
                if(!line.isEmpty()) {
                    parseQueryLine(line, queryStringBuilder);
                }
            }
        }

        if(isShuffle()) {
            shuffleQueries();
        }

        processQueries();

        storeQueriesWithTheirResultsInFile();
    }

    private static void parseQueryLine(String line, StringBuilder queryStringBuilder) {
        /*
         * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
         * On considère alors que c'est la fin d'une requête
         */
        SPARQLParser sparqlParser = new SPARQLParser();
        queryStringBuilder.append(line);

        if (line.trim().endsWith("}")) {
            ParsedQuery query = sparqlParser.parseQuery(queryStringBuilder.toString(), baseURI);
            parsedQueries.add(query);
            rawQueries.add(queryStringBuilder.toString());
            queryStringBuilder.setLength(0); // Reset le buffer de la requête en chaine vide
        }
    }

    public static void shuffleQueries() {
        Collections.shuffle(parsedQueries);
    }

    public static void processQueries() {
        for(int i = 0; i < (parsedQueries.size() * (getWarmPercentage() /100f)); i++) {

            queriesResults.add(QueryHandler.resultForAQuery(parsedQueries.get(i)));
        }
    }

    public static void storeQueriesWithTheirResultsInFile() throws FileNotFoundException {
        File output = new File(getOutputPath() + "/results.csv");
        try(PrintWriter printWriter = new PrintWriter(output)){

            printWriter.println("Queries,Results");
            for(int i = 0; i< rawQueries.size(); i++) {

                String fileLine = rawQueries.get(i).toString() + ",{";
                for(Integer queryResult : queriesResults.get(i)) {
                    Dictionary dictionary = Dictionary.getInstance();
                    fileLine += dictionary.getDictionaryMap().get(queryResult) + ",";
                }
                if(fileLine.endsWith(","))
                    fileLine = StringUtils.chop(fileLine);
                fileLine+="}";
                printWriter.println(fileLine);
            }
        }
    }
    public static void parseData() throws IOException {

        try (Reader dataReader = new FileReader(getDataFile())) {

            // On va parser des données au format ntriples
            RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

            // On utilise notre implémentation de handler
            rdfParser.setRDFHandler(new DataHandler());

            // Parsing et traitement de chaque triple par le handler
            rdfParser.parse(dataReader, baseURI);

        }
    }

    public static String getDataFile() {
        return dataFile;
    }

    public static void setDataFile(String dataFile) {
        if(checkIfStringPathExists(dataFile))
            Parser.dataFile = dataFile;
        //TODO else throw exception
    }

    public static String getQueryFile() {
        return queryFile;
    }

    public static void setQueryFile(String queryFile) {
        if(checkIfStringPathExists(queryFile))
            Parser.queryFile = queryFile;
    }

    private static boolean checkIfStringPathExists(String stringPath) {
        Path path = Paths.get(stringPath);
        return Files.exists(path);
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static void setOutputPath(String outputPath) {
        if(checkIfStringPathExists(queryFile))
            Parser.outputPath = outputPath;
    }

    public static int getWarmPercentage() {
        return warmPercentage;
    }

    public static void setWarmPercentage(int warmPercentage) {
        //TODO remplacer éventuellement par des exceptions
        if(warmPercentage > 100)
            Parser.warmPercentage = 100;
        else if(warmPercentage < 0)
            Parser.warmPercentage = 0;
        else
            Parser.warmPercentage = warmPercentage;
    }

    public static boolean isShuffle() {
        return shuffle;
    }

    public static void setShuffle(boolean shuffle) {
        Parser.shuffle = shuffle;
    }
}