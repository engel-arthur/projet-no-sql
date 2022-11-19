package qengine.index;

public class IndexCollection {

    //We implement the hexastore approach, hence we have 6 TripleStores.
    Index SPO = new Index();
    Index SOP = new Index();
    Index PSO = new Index();
    Index POS = new Index();
    Index OSP = new Index();
    Index OPS = new Index();




    //Adds a record to the index, in the right order for each TripleStore
    public void hexaStore(int subject, int predicate, int object) {

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
