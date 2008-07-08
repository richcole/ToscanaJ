package net.sourceforge.toscanaj.util;

import java.util.Collection;
import java.util.Iterator;

public class Formatter {
    public static String toSetFormat(final Collection input) {
        return toSetFormat(input.iterator());
    }

    public static String toSetFormat(final Iterator input) {
        final StringBuilder result = new StringBuilder();
        result.append("{");
        boolean first = true;
        while (input.hasNext()) {
            final Object object = input.next();
            if (!first) {
                result.append(",");
            }
            result.append(object.toString());
            first = false;
        }
        result.append("}");
        return result.toString();
    }
}
