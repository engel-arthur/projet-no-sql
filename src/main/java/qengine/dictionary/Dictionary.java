package qengine.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dictionary {

    private final Map<Integer, String> dictionaryMap = new HashMap<>();
    private final Map<String, Integer> dictionaryCounterMap = new HashMap<>();

    public void addToDictionary(int key, String stringToAdd) {
        dictionaryMap.put(key, stringToAdd);
    }

    public void addToCounterDictionary(String string, int indexOfTheString) {
        dictionaryCounterMap.put(string, indexOfTheString);
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

    public Map<String, Integer> getDictionaryCounterMap() {
        return dictionaryCounterMap;
    }

    //Used to populate the index in the main
    public int getKeyByValue(String value) {
        return dictionaryCounterMap.get(value) == null ? -1 : dictionaryCounterMap.get(value);
    }
}
