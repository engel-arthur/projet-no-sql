package qengine.index;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripleStore {

    private Map<Integer, Map<Integer, ArrayList<Integer>>> tripleStore;

    public TripleStore() {
        this.tripleStore = new HashMap<>();
    }

    public void insertTriplet(int root, int internal, int leaf) {

        if (tripleStore.get(root) == null) {

            Map<Integer,ArrayList<Integer>> mapInternals = new HashMap<>();
            tripleStore.put(root, mapInternals);
        }

        if (tripleStore.get(root).get(internal) == null) {

            ArrayList<Integer> listLeaf = new ArrayList<>();
            tripleStore.get(root).put(internal, listLeaf);
        }

        if (!tripleStore.get(root).get(internal).contains(leaf)) {

            tripleStore.get(root).get(internal).add(leaf);
        }
    }

    public ArrayList<Integer> loadTriplet(int root, int internal) {

        ArrayList<Integer> listResultTriplet =  new ArrayList<>();

        if (tripleStore.get(root) != null) {

            if (tripleStore.get(root).get(internal) != null) {

                listResultTriplet = tripleStore.get(root).get(internal);
            }
        }
        return listResultTriplet;
    }

    @Override
    public String toString() {
        return "TripleStore{" +
                "tripleStore=" + tripleStore.toString() +
                '}';
    }
}
