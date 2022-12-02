package qengine.program;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static qengine.parser.Parser.getOutputPath;

public final class ProgramEvaluation {

    private static HashMap<String, String> outputData = new HashMap<>();
    private static ArrayList<String> keysForData = new ArrayList<>(Arrays.asList("DATA_FILE_NAME", "QUERIES_FILE_NAME", "TRIPLET_AMOUNT", "QUERIES_AMOUNT",
            "DATA_READING_TIME", "QUERIES_READING_TIME", "DICO_CREATION_TIME", "INDEXES_AMOUNT", "INDEXES_CREATION_TIME", "QUERIES_PROCESSING_TIME", "TOTAL_TIME"));

    public static void init(){
        for (String key : keysForData) {
            outputData.put(key, "NOT_AVAILABLE");
        }
    }

    public static void writeOutputDataTOCSV() throws FileNotFoundException {

        File output = new File(getOutputPath() + "/program_data.csv");
        String csvSeparator = ",";

        try(PrintWriter printWriter = new PrintWriter(output)){

            StringBuilder dataHeaders = new StringBuilder();
            StringBuilder dataValues = new StringBuilder();

            for (String key : keysForData) {

                dataHeaders.append(key).append(csvSeparator);
                dataValues.append(outputData.get(key)).append(csvSeparator);
            }

            dataHeaders.setLength(dataHeaders.length() - 1);
            dataValues.setLength(dataValues.length() - 1);

            printWriter.println(dataHeaders);
            printWriter.println(dataValues);
        }
    }

    public static HashMap<String, String> getOutputData() {
        return outputData;
    }

    public static void addToOutputData(String key, String value) {
        outputData.put(key, value);
    }
}
