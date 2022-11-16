package qengine.index;

public class Index {

    //We implement the hexastore approach, hence we have 6 TripleStores.
    TripleStore SPO = new TripleStore();
    TripleStore SOP = new TripleStore();
    TripleStore PSO = new TripleStore();
    TripleStore POS = new TripleStore();
    TripleStore OSP = new TripleStore();
    TripleStore OPS = new TripleStore();




    //Adds a record to the index, in the right order for each TripleStore
    public void hexaStore(int subject, int predicate, int object) {

        SPO.insertTriplet(subject, predicate, object);
        SOP.insertTriplet(subject, object, predicate);
        PSO.insertTriplet(predicate, subject, object);
        POS.insertTriplet(predicate, object, subject);
        OSP.insertTriplet(object, subject, predicate);
        OPS.insertTriplet(object, predicate, subject);
    }

    public TripleStore getSPO() {
        return SPO;
    }

    public TripleStore getSOP() {
        return SOP;
    }

    public TripleStore getPSO() {
        return PSO;
    }

    public TripleStore getPOS() {
        return POS;
    }

    public TripleStore getOSP() {
        return OSP;
    }

    public TripleStore getOPS() {
        return OPS;
    }
}
