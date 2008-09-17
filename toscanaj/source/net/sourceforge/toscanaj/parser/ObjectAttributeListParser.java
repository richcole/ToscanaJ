/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
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
import org.tockit.util.StringTokenizer;

/**
 * This parser reads a file with a list of objects and their attributes into a
 * context.
 * 
 * The file format uses a line for each object with its attributes. The first
 * part is the text for the object itself, followed by a colon and then a list
 * of attributes it relates to seperated by semicolons. Double-quotes can be
 * used for quotation, backslashes as escapes.
 */
public class ObjectAttributeListParser {
    public static ContextImplementation<FCAElement, FCAElement> importOALFile(
            final File file) throws FileNotFoundException, DataFormatException {
        String name = file.getName();
        if (name.endsWith(".oal")) {
            name = name.substring(0, name.length() - 4);
        }
        return importOALFromReader(new FileReader(file), name);
    }

    public static ContextImplementation<FCAElement, FCAElement> importOALFromReader(
            final Reader reader, final String name) throws DataFormatException {
        final BufferedReader in = new BufferedReader(reader);
        try {
            final ContextImplementation<FCAElement, FCAElement> context = new ContextImplementation<FCAElement, FCAElement>(
                    name);

            final Collection<FCAElement> objects = context.getObjects();
            final Collection<FCAElement> attributes = context.getAttributes();
            final BinaryRelationImplementation<FCAElement, FCAElement> relation = context
            .getRelationImplementation();

            String curLine = in.readLine();
            int lineCount = 0;
            while (curLine != null) {
                lineCount++;
                if (curLine.indexOf(':') == -1) {
                    throw new DataFormatException(
                            "Input file contains line without colon in line "
                            + lineCount);
                }
                // using the tokenizer allows for quotes and escapes
                StringTokenizer tokenizer = new StringTokenizer(curLine, ':',
                        '"', '\\');
                final String objectText = tokenizer.nextToken();
                if (objectText.length() != 0) {
                    final FCAElement object = new FCAElementImplementation(
                            objectText);
                    objects.add(object);
                    // the rest might be split along more colons, though -- just
                    // ignore that
                    final String rest = curLine.substring(objectText.length() + 1);
                    tokenizer = new StringTokenizer(rest, ';', '"', '\\');
                    while (tokenizer.hasNext()) {
                        final FCAElement attribute = new FCAElementImplementation(
                                tokenizer.next());
                        attributes.add(attribute);
                        relation.insert(object, attribute);
                    }
                } else {
                    final String rest = curLine
                            .substring(objectText.length() + 1);
                    tokenizer = new StringTokenizer(rest, ';', '"', '\\');
                    while (tokenizer.hasNext()) {
                        final FCAElement attribute = new FCAElementImplementation(
                                tokenizer.next());
                        attributes.add(attribute);
                    }
                }
                do {
                    curLine = in.readLine();
                } while (curLine != null && curLine.length() == 0);
            }

            return context;
        } catch (final IOException e) {
            throw new DataFormatException("Error reading input file", e);
        } finally {
            try {
                in.close();
            } catch (final IOException e) {
                e.printStackTrace(); // nothing better to do here
            }
        }
    }
}
