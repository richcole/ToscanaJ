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
    private ArrayList<Expression> subexpressions = new ArrayList<Expression>();

    public OrExpression() {
		// nothing to do here
    }
    
    public void addClause(Expression clause) {
        subexpressions.add(clause);
    }

    public void removeClause(Expression clause) {
        subexpressions.remove(clause);
    }

    public String getSQL() {
        if(subexpressions.size() == 0) {
            return "*";
        }
        String retVal = "";
        for (Iterator<Expression> iterator = subexpressions.iterator(); iterator.hasNext();) {
            Expression expression = iterator.next();
            retVal += "(" + expression.getSQL() + ")";
            if(iterator.hasNext()) {
                retVal += " OR ";
            }
        }
        return retVal;
    }

    public boolean isLesserThan(Expression other) {
        /// @todo implement
        return false;
    }

    public boolean isEqual(Expression other) {
        /// @todo implement
        return false;
    }
}
