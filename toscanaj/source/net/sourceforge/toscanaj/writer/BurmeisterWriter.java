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

public class BurmeisterWriter {
    public static void writeToBurmeisterFormat(final Context context,
            final PrintStream out) {
        // write file ID
        out.println("B");

        // write context summary
        out.println(context.getName());
        out.println(context.getObjects().size());
        out.println(context.getAttributes().size());

        // write objects and attributes
        for (final Iterator<Object> itOb = context.getObjects().iterator(); itOb
                .hasNext();) {
            final Object object = itOb.next();
            out.println(object.toString());
        }
        for (final Iterator<Object> itAt = context.getAttributes().iterator(); itAt
                .hasNext();) {
            final Object attribute = itAt.next();
            out.println(attribute.toString());
        }

        // write relation
        for (final Iterator<Object> itOb = context.getObjects().iterator(); itOb
                .hasNext();) {
            final Object object = itOb.next();
            for (final Iterator<Object> itAt = context.getAttributes()
                    .iterator(); itAt.hasNext();) {
                final Object attribute = itAt.next();
                if (context.getRelation().contains(object, attribute)) {
                    out.print('x');
                } else {
                    out.print('.');
                }
            }
            out.println();
        }

        out.close();
    }
}
