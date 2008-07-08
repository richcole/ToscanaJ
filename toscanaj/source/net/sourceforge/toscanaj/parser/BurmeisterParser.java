/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;

public class BurmeisterParser {
    public static final String DEFAULT_NAME = "<unnamed>";

    public static Context<FCAElement, FCAElement> importBurmeisterFile(
            final File file) throws FileNotFoundException, DataFormatException {
        return importBurmeisterFromReader(new FileReader(file));
    }

    public static Context<FCAElement, FCAElement> importBurmeisterFromReader(
            final Reader reader) throws DataFormatException {
        final BufferedReader in = new BufferedReader(reader);

        try {
            // check id
            String curLine = in.readLine();
            if (!curLine.equals("B")) {
                throw new DataFormatException(
                        "Burmeister identifier missing ('B' as first line)");
            }

            // fetch context name and initialize context
            curLine = in.readLine();
            if (curLine.equals("")) {
                curLine = DEFAULT_NAME;
            }
            final ContextImplementation<FCAElement, FCAElement> context = new ContextImplementation<FCAElement, FCAElement>(
                    curLine);

            // get context size
            curLine = getNextNonEmptyLine(in);
            final int numberOfObjects = Integer.parseInt(curLine);
            curLine = getNextNonEmptyLine(in);
            final int numberOfAttributes = Integer.parseInt(curLine);

            // grab objects and attributes, store additional arrays to get
            // indizes
            final Collection<FCAElement> objects = context.getObjects();
            final FCAElement[] objectArray = new FCAElement[numberOfObjects];
            for (int i = 0; i < numberOfObjects; i++) {
                curLine = getNextNonEmptyLine(in);
                final FCAElementImplementation object = new FCAElementImplementation(
                        curLine);
                objects.add(object);
                objectArray[i] = object;
            }
            final Collection<FCAElement> attributes = context.getAttributes();
            final FCAElement[] attributeArray = new FCAElement[numberOfAttributes];
            for (int i = 0; i < numberOfAttributes; i++) {
                curLine = getNextNonEmptyLine(in);
                final FCAElement attribute = new FCAElementImplementation(
                        curLine, null);
                attributes.add(attribute);
                attributeArray[i] = attribute;
            }

            // process relation
            final BinaryRelationImplementation<FCAElement, FCAElement> relation = context
                    .getRelationImplementation();
            for (int i = 0; i < numberOfObjects; i++) {
                curLine = getNextNonEmptyLine(in);
                if (curLine == null) {
                    throw new DataFormatException(
                            "Relation contains less lines than expected.");
                }
                for (int j = 0; j < numberOfAttributes; j++) {
                    final char c = curLine.charAt(j);
                    if (c == 'x' || c == 'X') {
                        relation.insert(objectArray[i], attributeArray[j]);
                    }
                }
            }

            return context;
        } catch (final IOException e) {
            throw new DataFormatException("Error reading input file", e);
        }
    }

    private static String getNextNonEmptyLine(final BufferedReader in)
            throws IOException {
        String curLine;
        do {
            curLine = in.readLine();
            // we shouldn't get null for curLine, but we want to exit
            // gracefully,
            // therefore the test with the literal empty string upfront
        } while ("".equals(curLine));
        return curLine;
    }
}
