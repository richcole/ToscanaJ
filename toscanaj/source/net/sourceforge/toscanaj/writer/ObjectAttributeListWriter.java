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
    public static void writeObjectAttributeList(Context context, PrintStream out) {
        for (Iterator<Object> objIt = context.getObjects().iterator(); objIt.hasNext();) {
            Object object = objIt.next();
            out.print(object);
            out.print(":");
            boolean firstAttr = true;
            for (Iterator<Object> attrIt = context.getAttributes().iterator(); attrIt.hasNext();) {
                Object attribute = attrIt.next();
                if(context.getRelation().contains(object, attribute)) {
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
