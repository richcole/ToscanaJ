/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.Column;

class TableColumnPair {
    Table table;
    Column column;

    public TableColumnPair(Table table, Column column) {
        this.table = table;
        this.column = column;
    }

    public Table getTable() {
        return table;
    }

    public Column getColumn() {
        return column;
    }

    public String toString() {
        return table.getName() + ":" + column.getName();
    }
}
