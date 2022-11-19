package qengine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Index {

    //The structure is three levels deep, we have the root, which can have multiple internal children, which can have multiple leaves.
    //In a triplet, the first element would be the root node, the second the internal node, and the third would be the leaf node

    private Map<Integer, Map<Integer, ArrayList<Integer>>> tripleStore;

    public Index() {
        this.tripleStore = new HashMap<>();
    }

    public void insertTriplet(int root, int internal, int leaf) {

        //If the root of the triplet we want to add is not already in the index, we add it, along with an empty internal child.
        if (tripleStore.get(root) == null) {

            Map<Integer,ArrayList<Integer>> mapInternals = new HashMap<>();
            tripleStore.put(root, mapInternals);
        }

        //If the internal node we want to add is not already a child of the root, we add it, along with an empty list of leaves
        if (tripleStore.get(root).get(internal) == null) {

            ArrayList<Integer> listLeaf = new ArrayList<>();
            tripleStore.get(root).put(internal, listLeaf);
        }

        //If the leaf we want to add is not a child of the internal node, we add it
        if (!tripleStore.get(root).get(internal).contains(leaf)) {

            tripleStore.get(root).get(internal).add(leaf);
        }

        //If the triplet is already in the index, nothing happens
    }

    //Checks if the root node and internal node exist in the index, and return the corresponding list of leaves
    public ArrayList<Integer> getLeafFromQuery(int root, int internal) {

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
