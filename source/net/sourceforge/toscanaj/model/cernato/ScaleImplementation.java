/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.Hashtable;

import net.sourceforge.toscanaj.model.manyvaluedcontext.Scale;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleColumn;

public class ScaleImplementation implements Scale {
    protected String name;
    protected Hashtable columns = new Hashtable();

    public ScaleImplementation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addColumn(ScaleColumn column, String id) {
    	this.columns.put(id, column);
    }

    public ScaleColumn getColumn(String id) {
        return (ScaleColumn) columns.get(id);
    }

    public ScaleColumn[] getColumns() {
    	return (ScaleColumn[]) this.columns.values().toArray();
    }
}
