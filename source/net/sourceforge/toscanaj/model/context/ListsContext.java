/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.model.context;

import org.tockit.util.ListSet;


public interface ListsContext extends Context {
    ListSet getObjectList();
    ListSet getAttributeList();
}
