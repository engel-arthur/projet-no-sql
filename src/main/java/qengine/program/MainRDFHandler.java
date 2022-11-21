package qengine.program;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import qengine.dictionary.Dictionary;
import qengine.index.IndexCollection;

/**
 * Le RDFHandler intervient lors du parsing de données et permet d'appliquer un traitement pour chaque élément lu par le parseur.
 * 
 * <p>
 * Ce qui servira surtout dans le programme est la méthode {@link #handleStatement(Statement)} qui va permettre de traiter chaque triple lu.
 * </p>
 * <p>
 * À adapter/réécrire selon vos traitements.
 * </p>
 */
public final class MainRDFHandler extends AbstractRDFHandler {

	@Override
	public void handleStatement(Statement st) {

		Dictionary dictionary = Dictionary.getInstance();
		IndexCollection hexastore = IndexCollection.getInstance();

		int subjectIndex = dictionary.addToDictionary(String.valueOf(st.getSubject()));
		int predicateIndex = dictionary.addToDictionary(String.valueOf(st.getPredicate()));
		int objectIndex = dictionary.addToDictionary(String.valueOf(st.getObject()));

		hexastore.hexaStore(subjectIndex, predicateIndex, objectIndex);
	}
}