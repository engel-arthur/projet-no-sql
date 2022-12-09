package qengine.handler;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import qengine.dictionary.Dictionary;
import qengine.index.IndexCollection;
import qengine.utils.HashSetUtils;

import java.util.HashSet;
import java.util.List;

/*
 * This class handles the queries
 * It tests if it's a single request or a star request and acts accordingly
 * The variable in the queries is ALWAYS the Subject
 * Hence we only have to test OPS and POS.
 * */
public final class QueryHandler {

    private final Dictionary dictionary;
    private final IndexCollection hexastore;

    public QueryHandler(Dictionary dictionary, IndexCollection hexastore) {
        this.dictionary = dictionary;
        this.hexastore = hexastore;
    }

    public HashSet<Integer> resultForAQuery(ParsedQuery query) {

        HashSet<Integer> resultForAnEntireQuery = new HashSet<>();
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

        resultForAnEntireQuery = getResultFromPatterns(resultForAnEntireQuery, patterns);

        return resultForAnEntireQuery;
    }

    private void displayResult(HashSet<Integer> resultForAnEntireQuery) {
        System.out.println("Résultat : [ ");
        for (Integer result : resultForAnEntireQuery) {
            System.out.println("    " + result + " : " + dictionary.getDictionaryMap().get(result));
        }
        System.out.println(" ] ");
    }

    private HashSet<Integer> getResultFromPatterns(HashSet<Integer> resultForAnEntireQuery, List<StatementPattern> patterns) {
        boolean firstPattern = true;
        HashSet<Integer> resultForAnEntireQueryCopy = new HashSet<>(resultForAnEntireQuery);

        for (StatementPattern pattern : patterns) {

            HashSet<Integer> resultForOnePattern = resultForAPattern(pattern);

            if (firstPattern) {
                firstPattern = false;
                resultForAnEntireQueryCopy = resultForOnePattern;
            }
            else {
                resultForAnEntireQueryCopy = HashSetUtils.listIntersection(resultForAnEntireQueryCopy, resultForOnePattern);
            }
        }
        return resultForAnEntireQueryCopy;
    }

    public HashSet<Integer> resultForAPattern(StatementPattern pattern) {

        HashSet<Integer> listOfLeafFromOPS = new HashSet<>();
        int object = dictionary.getKeyByValue(pattern.getObjectVar().getValue().toString());
        int predicate = dictionary.getKeyByValue(pattern.getPredicateVar().getValue().toString());
//        String subject = pattern.getSubjectVar().toString();


        if (object == -1 || predicate == -1) {

            System.err.println("Une des deux données de la requête n'existe pas dans le dictionnaire Object : " + pattern.getObjectVar().getValue().toString() + " Predicate : " + pattern.getPredicateVar().getValue().toString());
        }
        else {

            listOfLeafFromOPS = hexastore.getOPS().getLeafFromQuery(object, predicate);
            /*
            * Ici, faudrait que l'on fasse des tests pour savoir quoi utiliser entre OPS et POS
            * parce que techniquement ils rendent la même chose
            * mais un des deux doit être plus rapide
            * en fonction du taux d'apparition des termes
            * Exemple : est ce que l'objet que l'on a apparaît plus souvent
            * que le prédicat ou inversement
            * */
        }
        return listOfLeafFromOPS;
    }
}
