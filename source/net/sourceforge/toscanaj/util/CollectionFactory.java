/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;

import java.util.*;

public class CollectionFactory {
    public static List createDefaultList() {
        return new ArrayList();
    }

    public static LinkedList createFastInsertDeleteList() {
        return new LinkedList();
    }

    public static Set createDefaultSet() {
        return new HashSet();
    }

    public static Map createDefaultMap() {
        return new HashMap();
    }

}
