/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import java.util.Hashtable;

public class ScaleImplementation implements Scale {
    protected String name;
    protected Hashtable<String, ScaleColumn> columns = new Hashtable<String, ScaleColumn>();

    public ScaleImplementation(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addColumn(final ScaleColumn column, final String id) {
        this.columns.put(id, column);
    }

    public ScaleColumn getColumn(final String id) {
        return columns.get(id);
    }

    public ScaleColumn[] getColumns() {
        return (ScaleColumn[]) this.columns.values().toArray();
    }
}
