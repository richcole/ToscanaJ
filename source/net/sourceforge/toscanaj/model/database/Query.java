/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.ArrayList;
import java.util.List;

/**
 * @todo move the query list into the ConceptualSchema
 */
public class Query {
    private String name;

    static private ArrayList queries = new ArrayList();

    /**
     * Returns an iterator on all available queries.
     */
    static public List getQueries() {
        return queries;
    }

    static public void clearQueries() {
        queries.clear();
    }

    public Query(String name) {
        this.name = name;
        queries.add(this);
    }

    public String getName() {
        return this.name;
    }
}