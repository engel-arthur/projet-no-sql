package qengine.program;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import qengine.dictionary.Dictionary;
import qengine.index.Index;
final class Main {
	static final String baseURI = null;

	/**
	 * Votre répertoire de travail où vont se trouver les fichiers à lire
	 */
	static final String workingDir = "data/";

	/**
	 * Fichier contenant les requêtes sparql
	 */
	static final String queryFile = workingDir + "sample_query.queryset";

	/**
	 * Fichier contenant des données rdf
	 */
	static final String dataFile = workingDir + "sample_data.nt";

	// ========================================================================

	/**
	 * Entrée du programme
	 */
	public static void main(String[] args) throws Exception {
		Dictionary dictionary = new Dictionary();
		Index hexaStore = new Index();
		parseDataFile(dataFile, dictionary, hexaStore);
		System.out.println(hexaStore.getSPO().toString());
		System.out.println(dictionary.getDictionaryMap().toString());
	}

	// ========================================================================

	//Parses the data file, assigning an index to each element and storing all the records in an index.
	public static void parseDataFile(String dataFile, Dictionary dictionary, Index hexaStore) throws IOException {
		try (BufferedReader dataFileStream = Files.newBufferedReader(Paths.get(dataFile))) {
			String lineStream = "";
			int index = 0;
            /*
                We store a line while it is not null
                If this line ends by ".", it means
                that it is the end of the triplet
            */
			while ((lineStream = dataFileStream.readLine()) != null) {
				//Split the triplet in three parts
				String[] lineSplitted = lineStream.split("[ \t]");

				//Array used to populate the index with each record
				int[] indexData = new int[lineSplitted.length - 1];

				//For each part of the triplet
				for (int i = 0; i < lineSplitted.length - 1; i++) {
					//If the part is not stored in the dictionnary
					if (!dictionary.getDictionaryMap().containsValue(lineSplitted[i])) {
						//We assign the next available index
						dictionary.addToDictionary(index, lineSplitted[i]);
						//We use this index to populate the indexData array
						indexData[i] = index;
						index++;
					}
					else {
						//If it's already in the dictionnary, we have to retrieve the corresponding index to populate indexData (might be costly)
						indexData[i] = dictionary.getKeyByValue(lineSplitted[i]);
					}
				}
				//We hexastore each record
				hexaStore.hexaStore(indexData[0], indexData[1], indexData[2]);
			}
		}
	}
}
