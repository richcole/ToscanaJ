/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package net.sourceforge.toscanaj.model.sql;

import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.order.Ordered;

public class ValueSetExpression implements Expression {
	private Column column;
	private String[] values;
	
	public ValueSetExpression(Column column, String[] values) {
		this.column = column;
		this.values = values;
	}

    public String getSQL() {
    	String retval = "";
    	for (int i = 0; i < values.length; i++) {
            retval += "(" + column.getName() + "=" + values[i] + ")";
            if(i != values.length - 1) {
            	retval += " OR ";
            }
        }
        return retval;
    }

    public boolean isLesserThan(Ordered other) {
    	if(! (other instanceof ValueSetExpression)) {
    		return false;
    	}
    	ValueSetExpression otherExp = (ValueSetExpression) other;
    	if( otherExp.column != this.column ) {
    		return false;
    	}
        return isValueSubset(otherExp);
    }

    public boolean isEqual(Ordered other) {
        if(! (other instanceof ValueSetExpression)) {
            return false;
        }
        ValueSetExpression otherExp = (ValueSetExpression) other;
        if( otherExp.column != this.column ) {
            return false;
        }
        if( otherExp.values.length != this.values.length ) {
        	return false;
        }
		return isValueSubset(otherExp);
    }

    private boolean isValueSubset(ValueSetExpression otherExp) {
    	for (int i = 0; i < otherExp.values.length; i++) {
            String element = otherExp.values[i];
            if( !this.containsValue(element) ) {
            	return false;
            }	
        }
        return true;
    }
    
    private boolean containsValue(String value) {
    	for (int i = 0; i < values.length; i++) {
            if( values[i] == value ) {
            	return true;
            }
        }
        return true;
    }
}
