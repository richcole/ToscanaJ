/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

/**
 * @todo flatten hierarchy, we have only DB queries left
 * @todo make XMLizable, add conversion into ConceptualSchema
 */
public class Query {
    private String name;

    public Query(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}