package qengine.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * Stores data (from a Dictionary) in a triple store format.
 *
 * The structure is three levels deep, we have the root, which can have multiple internal children, which can have multiple leaves.
 * In a triplet, the first element would be the root node, the second the internal node, and the third would be the leaf node
 * */

public final class Index {

    private final Map<Integer, Map<Integer, HashSet<Integer>>> tripletsIndex;

    public Index() {
        this.tripletsIndex = new HashMap<>();
    }

    public void insertTriplet(int root, int internal, int leaf) {

        //If the root of the triplet we want to add is not already in the index, we add it, along with an empty internal child.
        tripletsIndex.computeIfAbsent(root, k -> new HashMap<>());

        //If the internal node we want to add is not already a child of the root, we add it, along with an empty list of leaves
        tripletsIndex.get(root).computeIfAbsent(internal, k -> new HashSet<>());

        //If the leaf we want to add is not a child of the internal node, we add it
        tripletsIndex.get(root).get(internal).add(leaf);

        //If the triplet is already in the index, nothing happens
    }

    //Checks if the root node and internal node exist in the index, and return the corresponding list of leaves
    public HashSet<Integer> getLeafFromQuery(int root, int internal) {

        HashSet<Integer> listResultTriplet =  new HashSet<>();

        if (tripletsIndex.get(root) != null) {

            if (tripletsIndex.get(root).get(internal) != null) {

                listResultTriplet = tripletsIndex.get(root).get(internal);
            }
        }
        return listResultTriplet;
    }

    @Override
    public String toString() {
        return "TripleStore{" +
                "tripleStore=" + tripletsIndex +
                '}';
    }
}
