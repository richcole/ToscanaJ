/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.rdf.query;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;


import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.n3.N3JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFException;
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


public class RdqlQueryEngine {
		
	private RdqlQueryEngine () {	
	}

	public static void executeQuery(String queryString, Model model) {
		Query query = new Query(queryString) ;
		query.setSource(model);
		
		List queryVars = query.getResultVars();
		System.out.println("Query vars: " + queryVars);
		List triplePatterns = query.getTriplePatterns();
		System.out.println("Triple patterns: " + triplePatterns);
		Iterator it = triplePatterns.iterator();
		while (it.hasNext()) {
			Triple triple = (Triple) it.next();
			System.out.println("triple pattern: " + triple);
			System.out.println("\tobject: " + triple.getObject());
			System.out.println("\tpredicate: " + triple.getPredicate());
			System.out.println("\tsubject: " + triple.getSubject());
		}
		
		QueryExecution qe = new QueryEngine(query) ;
		QueryResults results = qe.exec() ;
		List resultVars = results.getResultVars();
		System.out.println("RESULT VARS: " + resultVars);
		for ( Iterator iter = results ; iter.hasNext() ; ) {
			ResultBinding cur = (ResultBinding)iter.next() ;
			System.out.println("res binding: " + cur);
			Object obj_A = cur.get("a") ;
			// obj will be a Jena object: resource, property or RDFNode.
			Object obj_B = cur.get("b") ;
			Object obj_C = cur.get("c") ;
			System.out.println("\t" + obj_A + "\timplements\t" + obj_C + "\tis-a\t" + obj_B);
		}
		results.close() ;
	}
	
	public static void main (String[] args) {
		if (args.length != 1 ) {
			System.out.println("Expecting relative file path as an argument");
			System.exit(0);
		}
		File file = new File(args[0]);
		
		String readerLang = "RDF/XML";
		if (file.getAbsolutePath().endsWith(".n3")) {
			readerLang = "N3";
		}
		
		RDFReaderF rdfReaderFactory = new RDFReaderFImpl();
		RDFReader rdfReader = rdfReaderFactory.getReader(readerLang);
		
		Model model = ModelFactory.createDefaultModel();
		try {
			rdfReader.read(model, file.toURL().toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		StmtIterator statementIterator = model.listStatements();
		while(statementIterator.hasNext()) {
			Statement curStatement = statementIterator.nextStatement();
		}
		
		String queryString = "SELECT ?a, ?b " + 
							"WHERE (?a, <implements>, ?c) , " + 
							"(?c, <is-a>, ?b)";
		System.out.println("Query: " + queryString);

		RdqlQueryEngine.executeQuery(queryString, model);
		
		System.out.println("finished");
	}

}
