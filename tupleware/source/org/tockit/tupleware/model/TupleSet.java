/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class TupleSet {
    private String[] variableNames;
    private Set tuples = new HashSet();
    
    public TupleSet(String[] variableNames) {
        this.variableNames = variableNames;  
    }
    
    public void addTuple(Object[] tuple) {
        if(tuple.length != this.variableNames.length) {
            throw new IllegalArgumentException("Tuples have to have the same length as the number of variables");
        }
        this.tuples.add(tuple);
    }
    
    public String[] getVariableNames() {
        return this.variableNames;
    }
    
    public Set getTuples() {
        return Collections.unmodifiableSet(tuples);
    }
    
    public static String toString(Object[] tuple) {
        StringBuffer retVal = new StringBuffer();
        for (int i = 0; i < tuple.length; i++) {
            if(i != 0) {
                retVal.append(" ");
            }
            retVal.append(tuple[i].toString());
        }
        return retVal.toString();
    }
}