package qengine.dictionary;

import qengine.index.Index;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public void parseDataFile(String dataFile, Index hexaStore) throws IOException {


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
                int[] indexData = new int[lineSplitted.length - 1];

                for (int i = 0; i < lineSplitted.length - 1; i++) {
                    if (!dictionary.containsValue(lineSplitted[i])) {

                        addToDictionary(index, lineSplitted[i]);
                        indexData[i] = index;
                        index++;
                    }
                    else {
                        indexData[i] = getKeyByValue(lineSplitted[i]);
                    }
                }
                hexaStore.hexaStores(indexData[0], indexData[1], indexData[2]);
            }
        }
    }

    public Map<Integer, String> getDictionary() {
        return dictionary;
    }

    public int getKeyByValue(String value) {

        for (Map.Entry<Integer, String> entry : dictionary.entrySet()) {

            if (Objects.equals(value, entry.getValue())) {

                return entry.getKey();
            }
        }
        return -1;
    }
}
