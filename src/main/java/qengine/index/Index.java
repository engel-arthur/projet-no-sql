package qengine.index;

public class Index {

    /**
     * Créer 6 versions différentes de chaque requête
     * Les stocker
     * Avoir des B+ Tree
     */

    TripleStore SPO = new TripleStore();
    TripleStore SOP = new TripleStore();
    TripleStore PSO = new TripleStore();
    TripleStore POS = new TripleStore();
    TripleStore OSP = new TripleStore();
    TripleStore OPS = new TripleStore();




    public void hexaStores(int subject, int predicate, int object) {

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
