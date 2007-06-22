/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.rdql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryEngine;
import com.hp.hpl.jena.rdql.QueryExecution;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;


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
	
	public static QueryResults executeRDQL (Model rdfModel, Query query) {
		List resultVars = query.getResultVars();
		query.setSource(rdfModel);	
		QueryExecution qe = new QueryEngine(query) ;
		QueryResults results = qe.exec();
		return results;
	}

	private static List readQueriesFile(String queriesFileName)
		throws FileNotFoundException, IOException {
		File dataFile = new File(queriesFileName);
		FileInputStream fis = new FileInputStream(dataFile);
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(fis));
		List queryStrings = new ArrayList();
		String line = bufReader.readLine();
		while (line != null) {
			queryStrings.add(line);
			line = bufReader.readLine();
		}
		return queryStrings;
	}


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
			List queryStrings = readQueriesFile(queriesFileName);
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
			Iterator it = queryStrings.iterator();
			while (it.hasNext()) {
				String queryString = (String) it.next();
				Query query = new Query(queryString);
				QueryResults results = RdfQueryUtil.executeRDQL(rdfModel, query);
				System.out.println("---QUERY " + queryString + "------------------");
				List resultVars = results.getResultVars();
				int count = 0;
				for ( Iterator iter = results ; iter.hasNext() ; ) {
					count++;
					ResultBinding resBinding = (ResultBinding)iter.next() ;
					Set triples = resBinding.getTriples();
					if (triples.size() <= 0) {
						System.out.println("size of result: " + triples.size());
					}
					if (printResults) {
						for (int i = 0; i < resultVars.size(); i++) {
							String  queryVar = (String) resultVars.get(i);
							Object obj = resBinding.get(queryVar);
							System.out.println("?" + queryVar + ": " + obj);
						}
					} 
				}
				results.close() ;
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
