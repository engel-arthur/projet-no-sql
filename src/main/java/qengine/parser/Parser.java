package qengine.parser;

import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import qengine.dictionary.Dictionary;
import qengine.index.IndexCollection;
import qengine.handler.DataHandler;
import qengine.handler.QueryHandler;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
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

    private static final ArrayList<String> queries = new ArrayList<>();

    // ========================================================================
    public static void parseQueries(Dictionary dictionary, IndexCollection hexastore, int warmPercentage) throws IOException {

        try (Stream<String> lineStream = Files.lines(Paths.get(getQueryFile()))) {

            int parsedQueries = 0;
            int totalNumberOfQueries = getNumberOrQueriesInFile(getQueryFile());

            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryStringBuilder = new StringBuilder();


            while (lineIterator.hasNext() && (parsedQueries < (totalNumberOfQueries * (warmPercentage / 100f)))) {

                String line = lineIterator.next();
                //On filtre les lignes vides
                if(!line.isEmpty()) {

                    parseQueryLine(line, queryStringBuilder);
                    //Le stringbuilder est vidé quand une query est parsée
                    if(queryStringBuilder.length() == 0) {
                        parsedQueries++;
                    }
                }
            }
        }
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
            QueryHandler.resultForAQuery(query);

            queryStringBuilder.setLength(0); // Reset le buffer de la requête en chaine vide
        }
    }

    private static int getNumberOrQueriesInFile(String path) throws IOException {
        int counter = 0;
        try (Stream<String> lineStream = Files.lines(Paths.get(getQueryFile()))) {
            Iterator<String> lineIterator = lineStream.iterator();

            //On compte la première ligne
            while (lineIterator.hasNext()) {
                String line = lineIterator.next();
                if(line.trim().endsWith("}")) {
                    counter++;
                }
            }
        }
        System.out.println("BONJOUR LE COUNTERU " + counter);
        return counter;
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

    /**
     * Fichier contenant des données rdf
     */
    public static String getDataFile() {
        return dataFile;
    }

    public static void setDataFile(String dataFile) {
        if(checkIfStringPathExists(dataFile))
            Parser.dataFile = dataFile;
        //TODO else throw exception
    }

    /**
     * Fichier contenant les requêtes sparql
     */
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
}