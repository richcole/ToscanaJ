/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;


public class DatabaseRetrievedObject {
    private Object key = null;
    private String displayString;
    private String queryWhereClause;
    private String specialWhereClause;

    public DatabaseRetrievedObject(String queryWhereClause, String displayString) {
        this.queryWhereClause = queryWhereClause;
        this.displayString = displayString;
    }

    public boolean hasKey() {
        return this.key != null;
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public boolean hasSpecialWhereClause() {
        return this.specialWhereClause != null;
    }

    public String getSpecialWhereClause() {
        return specialWhereClause;
    }

    public void setSpecialWhereClause(String specialWhereClause) {
        this.specialWhereClause = specialWhereClause;
    }

    public String getQueryWhereClause() {
        return queryWhereClause;
    }

    public String getDisplayString() {
        return displayString;
    }

    public String toString() {
        return getDisplayString();
    }
}
