package qengine.dictionary;

import java.util.HashMap;
import java.util.Map;

/*
 * Stores a dictionary of values.
 *
 * The idea is to parse a file filled with data and
 * add them to a dictionary coupled with an index.
 * Each index corresponds to a property in the data,
 * but a property can just have one index.
 * */

public final class Dictionary {

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
            addToCounterMap(stringToAdd);
            index++;
            return index-1;
        }
        else {
            return getKeyByValue(stringToAdd);
        }
    }

    public void addToCounterMap(String string) {
        dictionaryCounterMap.put(string, index);
    }

    public Map<Integer, String> getDictionaryMap() {
        return dictionaryMap;
    }

    public Map<String, Integer> getDictionaryCounterMap() {
        return dictionaryCounterMap;
    }

    public int getKeyByValue(String value) {
        return dictionaryCounterMap.get(value) == null ? -1 : dictionaryCounterMap.get(value);
    }
}
