/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;

import java.util.HashSet;
import java.util.Set;

public class IdPool {
    private Set allocatedIds = new HashSet();
    private int nextNumber = 1;

    public String getFreeId() {
        String retVal;
        do {
            retVal = String.valueOf(nextNumber);
            nextNumber++;
        } while (allocatedIds.contains(retVal));
        reserveId(retVal);
        return retVal;
    }

    public void releaseId(String id) {
        this.allocatedIds.remove(id);
    }

    public void reserveId(String id) {
        this.allocatedIds.add(id);
    }
    
    public boolean IdIsReserved (String id) {
    	return this.allocatedIds.contains(id);
    }
}
