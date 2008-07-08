/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.sql;

import java.util.ArrayList;
import java.util.Iterator;

public class OrExpression implements Expression {
    private final ArrayList<Expression> subexpressions = new ArrayList<Expression>();

    public OrExpression() {
        // nothing to do here
    }

    public void addClause(final Expression clause) {
        subexpressions.add(clause);
    }

    public void removeClause(final Expression clause) {
        subexpressions.remove(clause);
    }

    public String getSQL() {
        if (subexpressions.size() == 0) {
            return "*";
        }
        String retVal = "";
        for (final Iterator<Expression> iterator = subexpressions.iterator(); iterator
                .hasNext();) {
            final Expression expression = iterator.next();
            retVal += "(" + expression.getSQL() + ")";
            if (iterator.hasNext()) {
                retVal += " OR ";
            }
        }
        return retVal;
    }

    public boolean isLesserThan(final Expression other) {
        // / @todo implement
        return false;
    }

    public boolean isEqual(final Expression other) {
        // / @todo implement
        return false;
    }
}
