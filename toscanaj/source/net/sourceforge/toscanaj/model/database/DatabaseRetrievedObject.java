/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.model.context.FCAElement;

import org.jdom.Element;

/**
 * @todo drop distinctions between objects with or without keys and with or
 *       without special clauses. The object should have only one clause
 *       attached which gives whatever this object represents, the clause for
 *       the full object set of a label should not be part of instances of this
 *       class. Probably it is a good idea if aggregates just return null as
 *       their clause -- they just don't have any clause specific for them. This
 *       change needs first dropping the distinction between different viewers
 *       in the DatabaseViewerManager.
 */
public class DatabaseRetrievedObject implements FCAElement {
    private Object key = null;
    private final String displayString;
    private final String queryWhereClause;
    private String specialWhereClause;

    public DatabaseRetrievedObject(final String queryWhereClause,
            final String displayString) {
        this.queryWhereClause = queryWhereClause;
        this.displayString = displayString;
    }

    public boolean hasKey() {
        return this.key != null;
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(final Object key) {
        this.key = key;
    }

    public boolean hasSpecialWhereClause() {
        return this.specialWhereClause != null;
    }

    public String getSpecialWhereClause() {
        return specialWhereClause;
    }

    public void setSpecialWhereClause(final String specialWhereClause) {
        this.specialWhereClause = specialWhereClause;
    }

    public String getQueryWhereClause() {
        return queryWhereClause;
    }

    public String getDisplayString() {
        return displayString;
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

    public Object getData() {
        return this.displayString;
    }

    public Element getDescription() {
        return null;
    }

    public int getContextPosition() {
        return -1;
    }
}
