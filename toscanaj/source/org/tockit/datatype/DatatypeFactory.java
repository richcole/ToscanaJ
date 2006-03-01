/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;


public abstract class DatatypeFactory {
    public interface TypeCreator {
        boolean accepts(Element element);
        Datatype create(Element element);
    }
    
    private static List typeCreators = new ArrayList();
    
    public static void registerTypeCreator(TypeCreator typeCreator) {
        typeCreators.add(typeCreator);
    }
    
    public static Datatype readType(Element element) {
        for (Iterator iter = typeCreators.iterator(); iter.hasNext();) {
            TypeCreator tc = (TypeCreator) iter.next();
            if(tc.accepts(element)) {
                return tc.create(element);
            }
        }
        throw new IllegalArgumentException("Unknown type element");
    }
}
