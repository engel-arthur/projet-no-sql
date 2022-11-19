package qengine.query;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import qengine.dictionary.Dictionary;
import qengine.index.IndexCollection;

import java.util.ArrayList;

/*
 * Cette classe permet de s'occuper des queries
 * Donc on teste si c'est une requête normale ou en étoile
 * Puis on fait en fonction
 * En étoile = en plusieurs lignes
 * On a toujours SEULEMENT le sujet en variable dans les queries
 * Donc que OPS et POS à tester dans les index puisque qu'on doit trouver S à chacune
 * */
public class QueryHandler {

    public static void resultForAQuery(StatementPattern pattern, Dictionary dictionary, IndexCollection hexaStore) {

        int object = dictionary.getKeyByValue(pattern.getObjectVar().getValue().toString());
        int predicate = dictionary.getKeyByValue(pattern.getPredicateVar().getValue().toString());
        Value subject = pattern.getSubjectVar().getValue();

        System.out.println("Object : " + object);
        System.out.println("Predicate : " + predicate);
        System.out.println("Subject : " + subject);

        if (object == -1 || predicate == -1) {

            System.out.println("Une des deux données de la requête n'existe pas dans le dictionnaire");
        }
        else {

            ArrayList<Integer> listOfLeafFromOPS = hexaStore.getOPS().getLeafFromQuery(object, predicate);
            ArrayList<Integer> listOfLeafFromPOS = hexaStore.getPOS().getLeafFromQuery(predicate, object);

            System.out.println("Résultats obtenus avec OPS : " + listOfLeafFromOPS);
            System.out.println("Résultats obtenus avec POS : " + listOfLeafFromPOS);

            /*
            * Ici, faudrait que l'on fasse des tests pour savoir quoi utiliser entre OPS et POS
            * parce que techniquement ils rendent la même chose
            * mais un des deux doit être plus rapide
            * en fonction du taux d'apparition des termes
            * Exemple : est ce que l'objet que l'on a apparaît plus souvent
            * que le prédicat ou inversement
            *
            * Aussi, on doit mieux gérer les requêtes en étoile
            * car pour le moment on affiche juste les différents résultats mais
            * lors de ce type de requête, il faut renvoyer seulement le résultat final
            * résultant du filtrage de la première requête avec les autres
            * */
        }
    }
}
