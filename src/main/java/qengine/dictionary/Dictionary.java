package qengine.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dictionary {

    private final Map<Integer, String> dictionaryMap = new HashMap<>();
    private final Map<String, Integer> dictionaryCounterMap = new HashMap<>();

    private static Dictionary dictionary_instance = null;
    private int index = 0;

    private Dictionary() {}

    public static Dictionary getInstance() {
        if (dictionary_instance == null) {
            dictionary_instance = new Dictionary();
        }
        return dictionary_instance;
    }
    public int addToDictionary(String stringToAdd) {

        if (!dictionaryCounterMap.containsKey(stringToAdd)) {
            dictionaryMap.put(index, stringToAdd);
            addToCounterDictionary(stringToAdd);
            index++;
            return index-1;
        }
        else {
            return getKeyByValue(stringToAdd);
        }
    }

    public void addToCounterDictionary(String string) {
        dictionaryCounterMap.put(string, index);
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
