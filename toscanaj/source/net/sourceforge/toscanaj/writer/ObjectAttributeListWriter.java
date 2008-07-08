/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.writer;

import java.io.PrintStream;
import java.util.Iterator;

import org.tockit.context.model.Context;

public class ObjectAttributeListWriter {
    public static void writeObjectAttributeList(final Context context,
            final PrintStream out) {
        for (final Iterator<Object> objIt = context.getObjects().iterator(); objIt
                .hasNext();) {
            final Object object = objIt.next();
            out.print(object);
            out.print(":");
            boolean firstAttr = true;
            for (final Iterator<Object> attrIt = context.getAttributes()
                    .iterator(); attrIt.hasNext();) {
                final Object attribute = attrIt.next();
                if (context.getRelation().contains(object, attribute)) {
                    if (firstAttr) {
                        firstAttr = false;
                    } else {
                        out.print(",");
                    }
                    out.print(attribute);
                }
            }
            out.println();
        }
        out.close();
    }
}
