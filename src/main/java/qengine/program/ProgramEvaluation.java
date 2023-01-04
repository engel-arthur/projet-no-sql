package qengine.program;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static qengine.parser.Parser.getOutputPath;

public final class ProgramEvaluation {

    private static HashMap<String, String> outputData = new HashMap<>();
    private static ArrayList<String> keysForData = new ArrayList<>(Arrays.asList("DATA_FILE_NAME", "QUERIES_FOLDER", "TRIPLET_AMOUNT", "QUERIES_AMOUNT",
            "DATA_READING_TIME", "QUERIES_READING_TIME", "DICO_CREATION_TIME", "INDEXES_AMOUNT", "INDEXES_CREATION_TIME", "QUERIES_PROCESSING_TIME", "JENA_TIME", "TOTAL_TIME"));

    public static void init(){
        for (String key : keysForData) {
            outputData.put(key, "NOT_AVAILABLE");
        }
    }

    public static void writeOutputDataTOCSV() throws IOException {

        File output = new File(getOutputPath() + "/program_data.csv");
        String csvSeparator = ",";

        if(!output.exists()) {
            initializeProgramDataFile(output, csvSeparator);
        }

        try(PrintWriter printWriter = new PrintWriter(new FileWriter(output, true))){

            StringBuilder dataValues = new StringBuilder();

            for (String key : keysForData) {

                dataValues.append(outputData.get(key)).append(csvSeparator);
            }

            dataValues.setLength(dataValues.length() - 1);
            printWriter.println(dataValues);
        }
    }

    private static void initializeProgramDataFile(File output, String csvSeparator) throws FileNotFoundException {
        try(PrintWriter printWriter = new PrintWriter(output)){

            StringBuilder dataHeaders = new StringBuilder();

            for (String key : keysForData) {

                dataHeaders.append(key).append(csvSeparator);
            }
            dataHeaders.setLength(dataHeaders.length() - 1);

            printWriter.println(dataHeaders);
        }
    }

    public static HashMap<String, String> getOutputData() {
        return outputData;
    }

    public static void addToOutputData(String key, String value) {
        if(keysForData.contains(key))
            outputData.put(key, value);
    }
}
