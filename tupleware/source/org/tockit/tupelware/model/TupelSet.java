/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class TupelSet {
    private String[] variableNames;
    private Set tupels = new HashSet();
    
    public TupelSet(String[] variableNames) {
        this.variableNames = variableNames;  
    }
    
    public void addTupel(Object[] tupel) {
        this.tupels.add(tupel);
    }
    
    public String[] getVariableNames() {
        return this.variableNames;
    }
    
    public Set getTupels() {
        return Collections.unmodifiableSet(tupels);
    }
}
