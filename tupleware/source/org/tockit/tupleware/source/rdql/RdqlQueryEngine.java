/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.rdql;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.tockit.tupleware.model.TupleSet;

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


public class RdqlQueryEngine {
		
	private RdqlQueryEngine () {	
	}

	public static TupleSet executeQuery(String queryString, Model model) {
		Query query = new Query(queryString) ;
		List resultVars = query.getResultVars();
		TupleSet resTupleSet = new TupleSet(
							(String[]) resultVars.toArray(new String[resultVars.size()]));
		query.setSource(model);	
		QueryExecution qe = new QueryEngine(query) ;
		QueryResults results = qe.exec();
		for ( Iterator iter = results ; iter.hasNext() ; ) {
			ResultBinding resBinding = (ResultBinding)iter.next() ;
			Object[] tuple = new Object[resultVars.size()];
			for (int i = 0; i < resultVars.size(); i++) {
				String  queryVar = (String) resultVars.get(i);
				Object obj = resBinding.get(queryVar);
				tuple[i] = obj;				
			} 
			resTupleSet.addTuple(tuple);
		}
		results.close() ;
		return resTupleSet;
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

		TupleSet tupleSet = RdqlQueryEngine.executeQuery(queryString, model);
		Iterator it = tupleSet.getTuples().iterator();
		System.out.println("TUPLE SET: ");
		while (it.hasNext()) {
			Object[] element = (Object[]) it.next();
			System.out.print("---");
			for (int i = 0; i < element.length; i++) {
				Object object = element[i];
				System.out.print(object + ", ");
			}
			System.out.println();
		}
		
		System.out.println("finished");
	}

}
