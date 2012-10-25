/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

public class SQLTypeInfo {

    private final int typeInt;
    private final String typeName;

    public SQLTypeInfo(final int typeInt, final String typeName) {
        this.typeInt = typeInt;
        this.typeName = typeName;
    }

    public int getTypeInt() {
        return this.typeInt;
    }

    public String getTypeName() {
        return this.typeName;
    }

    @Override
    public String toString() {
        return "SQLTypeInfo: SQL type int = " + this.typeInt
                + ", name = " + this.typeName;
    }

}
