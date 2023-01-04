package qengine.parser;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
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
import qengine.program.ProgramEvaluation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/*
 * This class handles the parsing of the files containing the data and the queries
 * */
public final class Parser {

    private static final String baseURI = null;

    private static final String workingDir = "data/";
    public static final float NS_TO_MS_RATIO = 1000000;

    private static String queriesDirectory = workingDir + "sample_query.queryset";

    private static String dataFile = workingDir + "100K.nt";

    private static String outputPath = workingDir + "out";

    private static final Dictionary dictionary = Dictionary.getInstance();
    private static final IndexCollection hexastore = IndexCollection.getInstance();
    private static final ArrayList<String> rawQueries = new ArrayList<>();
    private static final ArrayList<ParsedQuery> parsedQueries = new ArrayList<>();
    private static final ArrayList<HashSet<Integer>> queriesResults = new ArrayList<>();

    private static int warmPercentage = 0;
    private static boolean shuffle = false;
    private static boolean jenaVerificationEnabled = false;

    private static boolean exportEnabled = false;

    // ========================================================================

    public static void fullProcess() throws IOException {

        ProgramEvaluation.addToOutputData("DATA_FILE_NAME", dataFile);
        ProgramEvaluation.addToOutputData("QUERIES_FOLDER", queriesDirectory);

        long startTimeDataReading = System.nanoTime();
        parseData();
        long endTimeDataReading = System.nanoTime();
        float totalTimeDataReading = (endTimeDataReading - startTimeDataReading)/ NS_TO_MS_RATIO;
        ProgramEvaluation.addToOutputData("DATA_READING_TIME", String.valueOf(totalTimeDataReading));
        ProgramEvaluation.addToOutputData("DICO_CREATION_TIME", String.valueOf(totalTimeDataReading));
        ProgramEvaluation.addToOutputData("INDEXES_CREATION_TIME", String.valueOf(totalTimeDataReading));

        long startTimeQueriesReading = System.nanoTime();
        parseQueriesDirectory();
        long endTimeQueriesReading = System.nanoTime();
        float totalTimeQueriesReading = (endTimeQueriesReading - startTimeQueriesReading)/ NS_TO_MS_RATIO;
        ProgramEvaluation.addToOutputData("QUERIES_READING_TIME", String.valueOf(totalTimeQueriesReading));

        ProgramEvaluation.addToOutputData("QUERIES_AMOUNT", String.valueOf(parsedQueries.size()));

        if(isShuffle())
            shuffleQueries();

        processQueries(true);

        long startTimeProcessQueries = System.nanoTime();
        processQueries(false);
        long endTimeProcessQueries = System.nanoTime();
        float totalTimeProcessQueries = (endTimeProcessQueries - startTimeProcessQueries)/ NS_TO_MS_RATIO;
        ProgramEvaluation.addToOutputData("QUERIES_PROCESSING_TIME", String.valueOf(totalTimeProcessQueries));

        if (exportEnabled)
            storeQueriesWithTheirResultsInFile();

        long startTimeJenaVerification = System.nanoTime();
        jenaParse();
        long endTimeJenaVerification = System.nanoTime();
        float totalTimeJenaVerification = (endTimeJenaVerification - startTimeJenaVerification)/ NS_TO_MS_RATIO;
        ProgramEvaluation.addToOutputData("JENA_TIME", String.valueOf(totalTimeJenaVerification));
    }
    private static void parseQueriesDirectory() throws IOException {
        File queriesDirectory = new File(getQueriesDirectory());
        File[] queryFiles = queriesDirectory.listFiles();
        assert queryFiles != null;

        for(File queryFile : queryFiles) {
            if(queryFile.isFile() && FilenameUtils.getExtension(queryFile.getName()).equals("queryset")) {
                parseQueriesFromAFile(getQueriesDirectory() + "/"  + queryFile.getName());
            }
        }
    }
    private static void parseQueriesFromAFile(String filepath) throws IOException {

        try (Stream<String> lineStream = Files.lines(Paths.get(filepath))) {

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
        long seed = System.nanoTime();
        Collections.shuffle(parsedQueries, new Random(seed));
        Collections.shuffle(rawQueries, new Random(seed));
    }

    private static void processQueries(boolean warmEnabled) {
        QueryHandler queryHandler = new QueryHandler(dictionary, hexastore);
        queriesResults.clear();

        int numberOfQueriesToProcess = parsedQueries.size();

        if(warmEnabled)
            numberOfQueriesToProcess *= getWarmPercentage() /100f;

        for(int i = 0; i < numberOfQueriesToProcess; i++) {

            queriesResults.add(queryHandler.resultForAQuery(parsedQueries.get(i)));
        }
    }

    private static void storeQueriesWithTheirResultsInFile() throws FileNotFoundException {
        File output = new File(getOutputPath() + "/results.csv");
        String csvSeparator = ",";
        String resultSeparator = "|";
        try(PrintWriter printWriter = new PrintWriter(output)){

            printWriter.println("Queries,ResultsAmount,Results");
            for(int i = 0; i< rawQueries.size(); i++) {

                StringBuilder fileLine = new StringBuilder(rawQueries.get(i) + csvSeparator + queriesResults.get(i).size() + csvSeparator + "[");
                for(Integer queryResult : queriesResults.get(i)) {
                    fileLine.append(dictionary.getDictionaryMap().get(queryResult)).append(resultSeparator);
                }
                if(fileLine.toString().endsWith(resultSeparator))
                    fileLine = new StringBuilder(StringUtils.chop(fileLine.toString()));
                fileLine.append("]");
                printWriter.println(fileLine);
            }
        }
    }
    private static void parseData() throws IOException {

        try (Reader dataReader = new FileReader(getDataFile())) {

            DataHandler dataHandler = new DataHandler(dictionary, hexastore);

            // On va parser des données au format ntriples
            RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

            // On utilise notre implémentation de handler
            rdfParser.setRDFHandler(dataHandler);

            // Parsing et traitement de chaque triple par le handler
            rdfParser.parse(dataReader, baseURI);

            ProgramEvaluation.addToOutputData("TRIPLET_AMOUNT", String.valueOf(dataHandler.getTripletCounter()));
            ProgramEvaluation.addToOutputData("INDEXES_AMOUNT", String.valueOf(dictionary.getDictionaryMap().size()));

        }
    }

    private static boolean checkIfPathIsValid(String path) {
        File f = new File(path);
        return f.exists();
    }

    private static void jenaParse() {
        Model model = RDFDataMgr.loadModel(getDataFile());
        int nbErrors = 0;

        for(int i=0; i < rawQueries.size(); i++) {

            String queryString = rawQueries.get(i);
            try (QueryExecution queryExecution = QueryExecutionFactory.create(queryString, model)) {

                ResultSet jenaResults = getQueryResultFromJena(queryExecution);

                if(isJenaVerificationEnabled()) {
                    ArrayList<String> jenaResultsStringArray = storeJenaResultsToStringArray(jenaResults);

                    ArrayList<String> homeResultsStringArray = indexSetToStringArray(queriesResults.get(i));

                    boolean differentResults = compareJenaResultsWithHomeResults(jenaResultsStringArray, homeResultsStringArray);

                    if(differentResults) {
                        printDifferenceBetweenResults(queryString, jenaResultsStringArray, homeResultsStringArray);
                    }
                }

            }
        }
    }

    private static ResultSet getQueryResultFromJena(QueryExecution queryExecution) {

        //Extrait dans une méthode car le nom "execSelect" est peu explicite
        return queryExecution.execSelect();
    }

    private static ArrayList<String> storeJenaResultsToStringArray(ResultSet results) {
        ArrayList<String> jenaResults = new ArrayList<>();

        while(results.hasNext()) {

            QuerySolution result = results.next();
            for (Iterator<String> resultIterator = result.varNames(); resultIterator.hasNext(); ) {

                String node = resultIterator.next();
                RDFNode resultNode = result.get(node);
                if(resultNode!=null)
                    jenaResults.add(resultNode.toString());
            }
        }

        return jenaResults;
    }

    private static void printDifferenceBetweenResults(String queryString, ArrayList<String> jenaResults, ArrayList<String> homeResults) {
        System.out.println("Résultats différents!");
        System.out.println("Requête : " + queryString);
        System.out.println("Résultats \"maison\" : " + homeResults.toString());
        System.out.println("Résultats Jena : " + jenaResults.toString() + "\n");
    }

    private static ArrayList<String> indexSetToStringArray(HashSet<Integer> set) {
        ArrayList<String> result = new ArrayList<>();
        for(int index : set) {

            result.add(dictionary.getDictionaryMap().get(index));
        }
        return result;
    }
    private static boolean compareJenaResultsWithHomeResults(ArrayList<String> jenaResults, ArrayList<String> homeResults) {
        boolean different = false;
        if(jenaResults.size() != homeResults.size()) {

            different = true;
        }
        else {

            for(String result : jenaResults) {

                if(!homeResults.contains(result))
                    different = true;
            }
        }

        return different;
    }

    public static String getDataFile() {
        return dataFile;
    }

    public static void setDataFile(String dataFile) {
        if(checkIfPathIsValid(dataFile))
            Parser.dataFile = dataFile;
        //TODO else throw exception
    }

    public static String getQueriesDirectory() {
        return queriesDirectory;
    }

    public static void setQueriesDirectory(String queriesDirectory) {
        if(checkIfPathIsValid(queriesDirectory))
            Parser.queriesDirectory = queriesDirectory;
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static void setOutputPath(String outputPath) {
        if(checkIfPathIsValid(queriesDirectory))
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

    public static boolean isJenaVerificationEnabled() {
        return jenaVerificationEnabled;
    }

    public static void setJenaVerificationEnabled(boolean jenaVerificationEnabled) {
        Parser.jenaVerificationEnabled = jenaVerificationEnabled;
    }

    public static void setExportEnabled(boolean exportEnabled) {
        Parser.exportEnabled = exportEnabled;
    }
}