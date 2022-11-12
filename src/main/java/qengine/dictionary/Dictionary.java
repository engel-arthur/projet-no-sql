package qengine.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    private Map<Integer, String> dictionary = new HashMap<>();

    protected void addToDictionary(int key, String stringToAdd) {
        dictionary.put(key, stringToAdd);
    }

    /*
        The idea is to parse a file fills with data and
        add them to a dictionary coupled with an index.
        Each index corresponds to a property in the data,
        but a property can just have one index.
    */
    public void parseDataFile(String dataFile) throws IOException {

        try (BufferedReader dataFileStream = Files.newBufferedReader(Paths.get(dataFile))) {
            String lineStream = "";
            int index = 0;
            /*
                We store a line while it is not null
                If this line ends by ".", it means
                that it is the end of the query
            */
            while ((lineStream = dataFileStream.readLine()) != null) {

                String[] lineSplitted = lineStream.split("[ \t]");

                for (int i = 0; i < lineSplitted.length - 1; i++) {
                    if (!dictionary.containsValue(lineSplitted[i])) {

                        addToDictionary(index, lineSplitted[i]);
                        index++;
                    }
                }
            }
        }
    }

    public Map<Integer, String> getDictionary() {
        return dictionary;
    }
}
