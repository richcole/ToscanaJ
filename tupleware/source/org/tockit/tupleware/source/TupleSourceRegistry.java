/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tockit.tupleware.source.cli.CommandLineSource;
import org.tockit.tupleware.source.sql.SqlQueryEngine;
import org.tockit.tupleware.source.text.TextSource;


public class TupleSourceRegistry {
	
	private static List tupleSources = new ArrayList();
	
	static {
		registerTupleSource(new TextSource());
		registerTupleSource(new SqlQueryEngine());
        registerTupleSource(new CommandLineSource());
	}
	
	private TupleSourceRegistry() {
	}
	
	public static void registerTupleSource(TupleSource tupleSource) {
		tupleSources.add(tupleSource);
	}
	
	public static List getTupleSources() {
		return Collections.unmodifiableList(tupleSources);
	}
}
