/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.writer;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tockit.context.model.Context;

public class ObjectAttributeListWriter {
    public static void writeObjectAttributeList(final Context context,
            final PrintStream out) {
        // first try and find attributes that are not assigned at all (empty
        // columns)
        final Set<Object> unassignedAttributes = new HashSet<Object>();
        checkAttributeLoop: for (final Object attribute : context
                .getAttributes()) {
            for (final Object object : context.getObjects()) {
                if (context.getRelation().contains(object, attribute)) {
                    continue checkAttributeLoop;
                }
            }
            unassignedAttributes.add(attribute);
        }
        if (unassignedAttributes.size() != 0) {
            out.print(":");
            boolean firstAttr = true;
            for (final Object attribute : unassignedAttributes) {
                if (firstAttr) {
                    firstAttr = false;
                } else {
                    out.print(",");
                }
                out.print(attribute);

            }
            out.println();
        }
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
