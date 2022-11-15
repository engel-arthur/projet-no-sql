package qengine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripleStore {

    private Map<Integer, Map<Integer, ArrayList<Integer>>> tripleStore;

    public TripleStore() {
        this.tripleStore = new HashMap<>();
    }

    public void insertTriplet(int root, int internal, int leaf) {

        if (tripleStore.containsKey(root)) {

            if (tripleStore.get(root).get(internal) != null) {

                if (tripleStore.get(root).get(internal).contains(leaf)) {

                    System.out.println("Déjà présent dans la base");
                }
                else {

                    tripleStore.get(root).get(internal).add(leaf);
                }
            }
            else {

                ArrayList<Integer> listLeaf = new ArrayList<>();
                listLeaf.add(leaf);
                tripleStore.get(root).put(internal, listLeaf);
            }
        }
        else {
            ArrayList<Integer> listLeaf = new ArrayList<>();
            listLeaf.add(leaf);
            Map<Integer,ArrayList<Integer>> mapInternals = new HashMap<>();
            mapInternals.put(internal, listLeaf);
            tripleStore.put(root, mapInternals);
        }
    }

}
