/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id:RdfQueryUtil.java 1929 2007-06-24 04:50:48Z peterbecker $
 */
package org.tockit.tupleware.source.rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;


public class RdfQueryUtil {
	
	public static Model createModel (File file) throws Exception {
		String readerLang = "RDF/XML";
		if (file.getAbsolutePath().endsWith(".n3")) {
			readerLang = "N3";
		}
		
		RDFReaderF rdfReaderFactory = new RDFReaderFImpl();
		RDFReader rdfReader = rdfReaderFactory.getReader(readerLang);
		
		Model rdfModel = ModelFactory.createDefaultModel();
		rdfReader.read(rdfModel, file.toURL().toString());
		return rdfModel;
	}
	
	public static ResultSet executeARQ (Model rdfModel, Query query) {
		QueryExecution queryEx = QueryExecutionFactory.create(query, rdfModel);	
		return queryEx.execSelect();
	}

	private static List<String> readQueriesFile(String queriesFileName)
		throws FileNotFoundException, IOException {
		File dataFile = new File(queriesFileName);
		FileInputStream fis = new FileInputStream(dataFile);
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(fis));
		List<String> queryStrings = new ArrayList<String>();
		String line = bufReader.readLine();
		while (line != null) {
			queryStrings.add(line);
			line = bufReader.readLine();
		}
		return queryStrings;
	}


	@SuppressWarnings("unchecked")
	public static void main (String[] args) {
		long time_start = System.currentTimeMillis();

		String usage = "Usage:\nRdfQueryTool dataFile queriesFile [--printTriples] [--printResultBindings]\n";
		usage += "where\n";
		usage += "\tdataFile - RDF or N3 file\n";
		usage += "\tqueriesFile - text file containing a RDQL query per line\n";
		
		if ((args.length < 2) || (args.length > 4)) {
			System.out.println(usage);
			System.exit(0);
		}
		
		String dataFileName = args[0];
		String queriesFileName = args[1];
		boolean printTriples = false;
		boolean printResults = false;
		if (args.length >= 3) {
			if  (args[2].endsWith("printTriples"))  {
				printTriples = true;
			}
			if  (args[2].endsWith("printResultBindings"))  {
				printResults = true;
			}
			if (args.length == 4) {
				if  (args[3].endsWith("printTriples")) {
					printTriples = true;
				}
				if (args[3].endsWith("printResultBindings")) {
					printResults = true;
				}
			}
			
		}
		
		
		try {
			long time_start_readQueriesFile = System.currentTimeMillis();
			List<String> queryStrings = readQueriesFile(queriesFileName);
			long time_readQueriesFile = System.currentTimeMillis() - time_start_readQueriesFile;

			long time_start_readDataFile = System.currentTimeMillis();
			Model rdfModel = RdfQueryUtil.createModel(new File (dataFileName));
			if (printTriples) {
				StmtIterator it = rdfModel.listStatements();
				while (it.hasNext()) {
					Statement cur = (Statement) it.next();
					System.out.println(cur);
				}
			}
			long time_readDataFile = System.currentTimeMillis() - time_start_readDataFile;
			
			long time_start_query = System.currentTimeMillis();
			Iterator<String> it = queryStrings.iterator();
			while (it.hasNext()) {
				String queryString = it.next();
				Query query = QueryFactory.create(queryString);
				ResultSet results = RdfQueryUtil.executeARQ(rdfModel, query);
				System.out.println("---QUERY " + queryString + "------------------");
				List<String> resultVars = results.getResultVars();
				int count = 0;
				for ( ; results.hasNext() ; ) {
					count++;
					QuerySolution resBinding = (QuerySolution)results.next() ;
					if (printResults) {
						for (int i = 0; i < resultVars.size(); i++) {
							String  queryVar = resultVars.get(i);
							Object obj = resBinding.get(queryVar);
							System.out.println("?" + queryVar + ": " + obj);
						}
					} 
				}
				// TODO: replace call to results.close() with matching close() form QueryExecution
				System.out.println("Num of results: " + count);
			}
			long time_query = System.currentTimeMillis() - time_start_query;

			long time_total = System.currentTimeMillis() - time_start;
		
			System.out.println("Time took to read queries file: " + time_readQueriesFile +" ms") ;
			System.out.println("Time took to load model from data file: " + time_readDataFile +" ms") ;		
			System.out.println("Time took to run all queries: " + time_query +" ms") ;
			System.out.println("Total run time: " + time_total +" ms") ;
			System.exit(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}


}
