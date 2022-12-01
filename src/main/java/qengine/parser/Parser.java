package qengine.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import qengine.dictionary.Dictionary;
import qengine.handler.DataHandler;
import qengine.handler.QueryHandler;
import qengine.index.IndexCollection;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

/*
 * This class handles the parsing of the files containing the data and the queries
 * */
public final class Parser {// ========================================================================

    private static final String baseURI = null;

    private static final String workingDir = "data/";

    private static String queryFile = workingDir + "sample_query.queryset";

    private static String dataFile = workingDir + "100K.nt";

    private static String outputPath = workingDir + "out";

    private static final Dictionary dictionary = Dictionary.getInstance();
    private static final IndexCollection hexastore = IndexCollection.getInstance();
    private static final ArrayList<String> rawQueries = new ArrayList<>();
    private static final ArrayList<ParsedQuery> parsedQueries = new ArrayList<>();
    private static final ArrayList<HashSet<Integer>> queriesResults = new ArrayList<>();

    private static int warmPercentage = 100;
    private static boolean shuffle = false;
    private static boolean jenaEnabled = false;

    // ========================================================================

    public static void fullProcess() throws IOException {
        parseData();
        parseQueries();

        if(isShuffle())
            shuffleQueries();

        processQueries(true);

        processQueries(false);


        storeQueriesWithTheirResultsInFile();

        if(isJenaEnabled())
            jenaParse();
    }
    private static void parseQueries() throws IOException {

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

    private static void shuffleQueries() {
        Collections.shuffle(parsedQueries);
    }

    private static void processQueries(boolean warmEnabled) {
        QueryHandler queryHandler = new QueryHandler(dictionary, hexastore);

        int numberOfQueriesToProcess = parsedQueries.size();

        if(warmEnabled)
            numberOfQueriesToProcess *= getWarmPercentage() /100f;

        for(int i = 0; i < numberOfQueriesToProcess; i++) {

            queriesResults.add(queryHandler.resultForAQuery(parsedQueries.get(i)));
        }
    }

    private static void storeQueriesWithTheirResultsInFile() throws FileNotFoundException {
        File output = new File(getOutputPath() + "/results.csv");
        try(PrintWriter printWriter = new PrintWriter(output)){

            printWriter.println("Queries,Results");
            for(int i = 0; i< rawQueries.size(); i++) {

                StringBuilder fileLine = new StringBuilder(rawQueries.get(i) + ",{");
                for(Integer queryResult : queriesResults.get(i)) {
                    fileLine.append(dictionary.getDictionaryMap().get(queryResult)).append(",");
                }
                if(fileLine.toString().endsWith(","))
                    fileLine = new StringBuilder(StringUtils.chop(fileLine.toString()));
                fileLine.append("}");
                printWriter.println(fileLine);
            }
        }
    }
    private static void parseData() throws IOException {

        try (Reader dataReader = new FileReader(getDataFile())) {

            // On va parser des données au format ntriples
            RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

            // On utilise notre implémentation de handler
            rdfParser.setRDFHandler(new DataHandler(dictionary, hexastore));

            // Parsing et traitement de chaque triple par le handler
            rdfParser.parse(dataReader, baseURI);

        }
    }

    private static boolean checkIfStringPathExists(String stringPath) {
        Path path = Paths.get(stringPath);
        return Files.exists(path);
    }

    private static void jenaParse() {
        Model model = RDFDataMgr.loadModel(getDataFile());

        for(String queryString : rawQueries) {

            try (QueryExecution queryExecution = QueryExecutionFactory.create(queryString, model)) {
                Query query = QueryFactory.create(queryString) ;
                ResultSet results = queryExecution.execSelect();
                ResultSetFormatter.out(System.out, results, query) ;
            }
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

    public static boolean isJenaEnabled() {
        return jenaEnabled;
    }

    public static void setJenaEnabled(boolean jenaEnabled) {
        Parser.jenaEnabled = jenaEnabled;
    }
}