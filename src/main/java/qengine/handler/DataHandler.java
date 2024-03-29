package qengine.handler;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import qengine.dictionary.Dictionary;
import qengine.index.IndexCollection;

/*
 * This class feeds the data into the dictionary and the index
 * */
public final class DataHandler extends AbstractRDFHandler {

	private final Dictionary dictionary;
	private final IndexCollection hexastore;

	private int tripletCounter;

	public DataHandler(Dictionary dictionary, IndexCollection hexastore) {
		super();
		this.dictionary = dictionary;
		this.hexastore = hexastore;
		tripletCounter = 0;
	}

	@Override
	public void handleStatement(Statement st) {

		int subjectIndex = dictionary.addToDictionary(String.valueOf(st.getSubject()));
		int predicateIndex = dictionary.addToDictionary(String.valueOf(st.getPredicate()));
		int objectIndex = dictionary.addToDictionary(String.valueOf(st.getObject()));

		hexastore.hexastore(subjectIndex, predicateIndex, objectIndex);
		tripletCounter = getTripletCounter() + 1;
	}

	public int getTripletCounter() {
		return tripletCounter;
	}
}