package qengine.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dictionary {

    private Map<Integer, String> dictionaryMap = new HashMap<>();

    public void addToDictionary(int key, String stringToAdd) {
        dictionaryMap.put(key, stringToAdd);
    }

    /*
        The idea is to parse a file fills with data and
        add them to a dictionary coupled with an index.
        Each index corresponds to a property in the data,
        but a property can just have one index.
    */

    public Map<Integer, String> getDictionaryMap() {
        return dictionaryMap;
    }

    //Used to populate the index in the main
    public int getKeyByValue(String value) {

        for (Map.Entry<Integer, String> entry : dictionaryMap.entrySet()) {

            if (Objects.equals(value, entry.getValue())) {

                return entry.getKey();
            }
        }
        return -1;
    }
}
