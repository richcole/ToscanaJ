/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import net.sourceforge.toscanaj.model.context.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;

import java.io.*;
import java.util.Collection;

import org.tockit.util.StringTokenizer;

/**
 * This parser reads a file with a list of objects and their attributes into a context.
 * 
 * The file format uses a line for each object with its attributes. The first part is
 * the text for the object itself, followed by a colon and then a list of attributes
 * it relates to seperated by semicolons. Double-quotes can be used for quotation,
 * backslashes as escapes.
 */
public class ObjectAttributeListParser {
    public static ContextImplementation importOALFile(File file) throws FileNotFoundException, DataFormatException {
        BufferedReader in;
        in = new BufferedReader(new FileReader(file));

        try {
            String name = file.getName();
            if(name.endsWith(".oal")) {
            	name = name.substring(0, name.length() - 4);
            }
			ContextImplementation context = new ContextImplementation(name);

            Collection objects = context.getObjects();
            Collection attributes = context.getAttributes();
            BinaryRelationImplementation relation = (BinaryRelationImplementation) context.getRelation();
            

            String curLine = in.readLine();
            while(curLine != null) {
            	if(curLine.indexOf(':') == -1) {
                    throw new DataFormatException("Input file contains line without colon");
            	}
                // using the tokenizer allows for quotes and escapes
                StringTokenizer tokenizer = new StringTokenizer(curLine,':','"','\\');
                String objectText = tokenizer.nextToken();
				FCAElement object = new FCAElementImplementation(objectText);
                objects.add(object);
                // the rest might be split along more colons, though -- just ignore that
                String rest = curLine.substring(objectText.length() + 1);
                tokenizer = new StringTokenizer(rest,';','"','\\');
                while(tokenizer.hasNext()) {
                	FCAElement attribute = new FCAElementImplementation(tokenizer.next());
                    attributes.add(attribute);
                    relation.insert(object, attribute);
                }
            	do {
                	curLine = in.readLine();
            	} while(curLine != null && curLine.length() == 0);
            }

            return context;
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file", e);
        }
    }
}
