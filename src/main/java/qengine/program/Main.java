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
import qengine.index.IndexCollection;
import qengine.query.QueryHandler;

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
		IndexCollection hexaStore = new IndexCollection();
		parseDataFile(dataFile, dictionary, hexaStore);
		System.out.println(hexaStore.getSPO().toString());
		System.out.println(dictionary.getDictionaryMap().toString());
		System.out.println(dictionary.getDictionaryCounterMap().toString());

		parseQueries(dictionary, hexaStore);
	}

	// ========================================================================

	//Parses the data file, assigning an index to each element and storing all the records in an index.
	public static void parseDataFile(String dataFile, Dictionary dictionary, IndexCollection hexaStore) throws IOException {
		try (BufferedReader dataFileStream = Files.newBufferedReader(Paths.get(dataFile))) {
			String lineStream = "";
			int index = 0;
            /*
                We store a line while it is not null
            */
			while ((lineStream = dataFileStream.readLine()) != null) {
				//Split the triplet in three parts
				String[] lineSplitted = lineStream.split("[ \t]");

				//Array used to populate the index with each record
				int[] indexData = new int[lineSplitted.length - 1];

				//For each part of the triplet
				for (int i = 0; i < lineSplitted.length - 1; i++) {
					//If the part is not stored in the dictionnary
					if (lineSplitted[i].contains("<")) {
						lineSplitted[i] = lineSplitted[i].replaceAll("[<>]", "");
					}
					if (!dictionary.getDictionaryMap().containsValue(lineSplitted[i])) {
						//We assign the next available index
						dictionary.addToDictionary(index, lineSplitted[i]);
						dictionary.addToCounterDictionary(lineSplitted[i], index);
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

	private static void parseQueries(Dictionary dictionary, IndexCollection hexaStore) throws IOException {
		/**
		 * Try-with-resources
		 *
		 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
		 * entièrement dans une collection.
		 */
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();

			while (lineIterator.hasNext())
				/*
				 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
				 * On considère alors que c'est la fin d'une requête
				 */
			{
				String line = lineIterator.next();
				queryString.append(line);
//				System.out.println("QueryString : " + queryString);

				if (line.trim().endsWith("}")) {
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
//					System.out.println("ParsedQuery : " + query);
					processAQuery(query, dictionary, hexaStore); // Traitement de la requête, à adapter/réécrire pour votre programme

					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}
			}
		}
	}

	public static void processAQuery(ParsedQuery query, Dictionary dictionary, IndexCollection hexaStore) {

		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

		for (StatementPattern pattern : patterns) {

			QueryHandler.resultForAQuery(pattern, dictionary, hexaStore);
		}

		// Utilisation d'une classe anonyme
		query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {

			@Override
			public void meet(Projection projection) {
				System.out.println("Projection : " + projection.getProjectionElemList().getElements());
			}
		});
	}
}
