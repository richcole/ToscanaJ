/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public abstract class DatatypeFactory {
    public interface TypeCreator {
        boolean accepts(Element element);

        Datatype create(Element element);
    }

    private static List<TypeCreator> typeCreators = new ArrayList<TypeCreator>();

    public static void registerTypeCreator(final TypeCreator typeCreator) {
        typeCreators.add(typeCreator);
    }

    public static Datatype readType(final Element element) {
        for (final TypeCreator tc : typeCreators) {
            if (tc.accepts(element)) {
                return tc.create(element);
            }
        }
        throw new IllegalArgumentException("Unknown type element");
    }
}
