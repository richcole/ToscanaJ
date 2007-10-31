/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.util.StringTokenizer;


/**
 * Parser to parse tuples from files.
 */
public class SeparatedTextParser {
    /**
     * Reads a list of tuples from a separator-delimited text file.
     * 
     * The format used is one tuple per line, the elements delimited
     * with the given separator. Quotes and escapes can be used as defined
     * in the org.tockit.util.StringTokenizer class.
     * 
     * If a line doesn't contain at least two entries it is consider a
     * comment.
     */
    public static Relation<Object> parseTabDelimitedTuples(
            Reader input, char separator, char quote, char escape, 
            boolean firstLineHeader) throws IOException {
        Relation<Object> retVal = null;
        BufferedReader buffReader = new BufferedReader(input);
        int lineNum = 0;
        while(true) {
            String line = buffReader.readLine();
            if(line == null) {
                break;            
            }
            lineNum++;
            StringTokenizer tokenizer = new StringTokenizer(line, separator, quote, escape);
            String[] tuple = tokenizer.tokenizeAll();
            // lines without separators are considered comments
            if(tuple.length <= 1) {
                continue;
            }
            if(retVal == null) {
                if(firstLineHeader) {
                    retVal = new RelationImplementation<Object>(tuple);
                } else {
                    retVal = new RelationImplementation<Object>(tuple.length);
                    retVal.addTuple(tuple);
                }
            } else {
            	try {
					retVal.addTuple(tuple);
	           	} catch(IllegalArgumentException e) {
	           		throw new IOException("Illegal tuple in line #" + lineNum);
	           	}
            }
        }
        return retVal;
    }
}
