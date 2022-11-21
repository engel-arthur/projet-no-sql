package qengine.program;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
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
		Dictionary dictionary = Dictionary.getInstance();
		IndexCollection hexastore = IndexCollection.getInstance();
		parseData();
		System.out.println(hexastore.getSPO().toString());
		System.out.println(dictionary.getDictionaryMap().toString());
		System.out.println(dictionary.getDictionaryCounterMap().toString());

		parseQueries(dictionary, hexastore);
	}

	// ========================================================================
	private static void parseQueries(Dictionary dictionary, IndexCollection hexaStore) throws IOException {
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

	private static void parseData() throws IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			// On va parser des données au format ntriples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			// On utilise notre implémentation de handler
			rdfParser.setRDFHandler(new MainRDFHandler());

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);

		}
	}
}
