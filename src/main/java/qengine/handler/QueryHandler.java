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
public class QueryHandler {

    public static void resultForAQuery(ParsedQuery query, Dictionary dictionary, IndexCollection hexaStore) {

        HashSet<Integer> resultForAnEntireQuery = new HashSet<>();
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        boolean firstPattern = true;

        System.out.println("Query : [");
        for (StatementPattern pattern : patterns) {

            System.out.println("    Pattern : ");
            HashSet<Integer> resultForOnePattern = QueryHandler.resultForAPattern(pattern, dictionary, hexaStore);
            if (firstPattern) {
                firstPattern = false;
                resultForAnEntireQuery = resultForOnePattern;
            }
//			System.out.println("Résultat une seule requete : " + resultForOnePattern);
            else {
                resultForAnEntireQuery = HashSetUtils.listIntersection(resultForAnEntireQuery, resultForOnePattern);
            }
        }
        System.out.println(" ]");
        System.out.println("Résultat : [ ");
        for (Integer result : resultForAnEntireQuery) {
            System.out.println("    " + result + " : " + dictionary.getDictionaryMap().get(result));
        }
        System.out.println(" ] ");
    }
    public static HashSet<Integer> resultForAPattern(StatementPattern pattern, Dictionary dictionary, IndexCollection hexaStore) {

        HashSet<Integer> listOfLeafFromOPS = new HashSet<>();
        int object = dictionary.getKeyByValue(pattern.getObjectVar().getValue().toString());
        int predicate = dictionary.getKeyByValue(pattern.getPredicateVar().getValue().toString());
//        String subject = pattern.getSubjectVar().toString();

        System.out.println("        Object : " + object + " Predicate : " + predicate);

        if (object == -1 || predicate == -1) {

            System.out.println("Une des deux données de la requête n'existe pas dans le dictionnaire");
        }
        else {

            listOfLeafFromOPS = hexaStore.getOPS().getLeafFromQuery(object, predicate);
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
