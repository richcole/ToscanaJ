/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;

import java.io.*;
import java.util.Collection;

import org.tockit.context.model.BinaryRelationImplementation;

public class BurmeisterParser {
    public static final String DEFAULT_NAME = "<unnamed>";

    public static ContextImplementation importBurmeisterFile(File file) throws FileNotFoundException, DataFormatException {
        BufferedReader in;
        in = new BufferedReader(new FileReader(file));

        try {
            // check id
            String curLine = in.readLine();
            if (!curLine.equals("B")) {
                throw new DataFormatException("Burmeister identifier missing ('B' as first line)");
            }

            // fetch context name and initialize context
            curLine = in.readLine();
            if (curLine.equals("")) {
                curLine = DEFAULT_NAME;
            }
            ContextImplementation context = new ContextImplementation(curLine);

            // get context size
            curLine = getNextNonEmptyLine(in);
            int numberOfObjects = Integer.parseInt(curLine);
            curLine = getNextNonEmptyLine(in);
            int numberOfAttributes = Integer.parseInt(curLine);

            // grab objects and attributes, store additional arrays to get indizes
            Collection objects = context.getObjects();
            FCAElement[] objectArray = new FCAElement[numberOfObjects];
            for (int i = 0; i < numberOfObjects; i++) {
                curLine = getNextNonEmptyLine(in);
                FCAElementImplementation object = new FCAElementImplementation(curLine);
                objects.add(object);
                objectArray[i] = object;
            }
            Collection attributes = context.getAttributes();
            FCAElement[] attributeArray = new FCAElement[numberOfAttributes];
            for (int i = 0; i < numberOfAttributes; i++) {
                curLine = getNextNonEmptyLine(in);
                FCAElement attribute = new FCAElementImplementation(curLine, null);
                attributes.add(attribute);
                attributeArray[i] = attribute;
            }

            // process relation
            BinaryRelationImplementation relation = (BinaryRelationImplementation) context.getRelation();
            for (int i = 0; i < numberOfObjects; i++) {
                curLine = getNextNonEmptyLine(in);
                for (int j = 0; j < numberOfAttributes; j++) {
                    char c = curLine.charAt(j);
                    if (c == 'x' || c == 'X') {
                        relation.insert(objectArray[i], attributeArray[j]);
                    }
                }
            }

            return context;
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file", e);
        }
    }

    private static String getNextNonEmptyLine(BufferedReader in) throws IOException {
        String curLine;
        do {
            curLine = in.readLine();
        } while (curLine.equals(""));
        return curLine;
    }
}
