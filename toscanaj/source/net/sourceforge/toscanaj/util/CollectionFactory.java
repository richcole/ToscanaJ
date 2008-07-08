/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionFactory {
    public static List<Object> createDefaultList() {
        return new ArrayList<Object>();
    }

    public static List createFastInsertDeleteList() {
        return new LinkedList();
    }

    public static Set createDefaultSet() {
        return new HashSet();
    }

    public static Map createDefaultMap() {
        return new HashMap();
    }

}
