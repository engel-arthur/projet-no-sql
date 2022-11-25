package qengine.index;

/*
 * Stores six indexes to implement the hexastore approach
 * Each index corresponds to a different permutation of Subjet, Predicate, and Object
 * */
public class IndexCollection {

    private static IndexCollection indexCollection_instance = null;
    Index SPO = new Index();
    Index SOP = new Index();
    Index PSO = new Index();
    Index POS = new Index();
    Index OSP = new Index();
    Index OPS = new Index();

    private IndexCollection() {}

    public static IndexCollection getInstance() {
        if (indexCollection_instance == null) {
            indexCollection_instance = new IndexCollection();
        }
        return indexCollection_instance;
    }


    //Adds a record to the index, in the right order for each TripleStore
    public void hexastore(int subject, int predicate, int object) {

        SPO.insertTriplet(subject, predicate, object);
        SOP.insertTriplet(subject, object, predicate);
        PSO.insertTriplet(predicate, subject, object);
        POS.insertTriplet(predicate, object, subject);
        OSP.insertTriplet(object, subject, predicate);
        OPS.insertTriplet(object, predicate, subject);
    }

    public Index getSPO() {
        return SPO;
    }

    public Index getSOP() {
        return SOP;
    }

    public Index getPSO() {
        return PSO;
    }

    public Index getPOS() {
        return POS;
    }

    public Index getOSP() {
        return OSP;
    }

    public Index getOPS() {
        return OPS;
    }
}
