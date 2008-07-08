/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package net.sourceforge.toscanaj.model.sql;

import net.sourceforge.toscanaj.model.database.Column;

public class ValueSetExpression implements Expression {
    private final Column column;
    private final String[] values;

    public ValueSetExpression(final Column column, final String[] values) {
        this.column = column;
        this.values = values;
    }

    public String getSQL() {
        String retval = "";
        for (int i = 0; i < values.length; i++) {
            retval += "(" + column.getSqlExpression() + "=" + values[i] + ")";
            if (i != values.length - 1) {
                retval += " OR ";
            }
        }
        return retval;
    }

    public boolean isLesserThan(final Expression other) {
        if (!(other instanceof ValueSetExpression)) {
            return false;
        }
        final ValueSetExpression otherExp = (ValueSetExpression) other;
        if (otherExp.column != this.column) {
            return false;
        }
        return isValueSubset(otherExp);
    }

    public boolean isEqual(final Expression other) {
        if (!(other instanceof ValueSetExpression)) {
            return false;
        }
        final ValueSetExpression otherExp = (ValueSetExpression) other;
        if (otherExp.column != this.column) {
            return false;
        }
        if (otherExp.values.length != this.values.length) {
            return false;
        }
        return isValueSubset(otherExp);
    }

    private boolean isValueSubset(final ValueSetExpression otherExp) {
        for (final String element : otherExp.values) {
            if (!this.containsValue(element)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsValue(final String value) {
        for (final String value2 : values) {
            if (value2 == value) {
                return true;
            }
        }
        return true;
    }
}
