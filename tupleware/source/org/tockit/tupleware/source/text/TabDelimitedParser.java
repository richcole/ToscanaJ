/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.tockit.tupleware.model.TupleSet;


/**
 * Parser to parse tuples from files.
 */
public class TabDelimitedParser {
    /**
     * Reads a list of tuples from a tab-delimited file.
     * 
     * The format used is one tuple per line, the elements delimited
     * with tabs. No escaping, so no tabs in the elements are allowed.
     * 
     * If a line doesn't contain at least two entries it is consider a
     * comment.
     * 
     * @return a Set of Object[] representing the tuples parsed
     */
    public static TupleSet parseTabDelimitedTuples(Reader input) throws IOException {
        TupleSet retVal = null;
        BufferedReader buffReader = new BufferedReader(input);
        int lineNum = 0;
        while(true) {
            String line = buffReader.readLine();
            if(line == null) {
                break;            
            }
            lineNum++;
            StringTokenizer tokenizer = new StringTokenizer(line, "\t", false);
            // lines without tabs are considered comments
            if(tokenizer.countTokens() <= 1) {
                continue;
            }
            String[] tuple = new String[tokenizer.countTokens()];
            int i = 0;
            while(tokenizer.hasMoreTokens()) {
                tuple[i] = tokenizer.nextToken();
                i++;
            }
            if(retVal == null) {
                retVal = new TupleSet(tuple); 
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
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        TupleSet result = parseTabDelimitedTuples(new FileReader(new File(args[0])));
        System.out.println("Vars:");
        System.out.println(TupleSet.toString(result.getVariableNames()));
        System.out.println("Tuples:");
        Set tuples = result.getTuples();
        for (Iterator iter = tuples.iterator(); iter.hasNext();) {
            Object[] tuple = (Object[]) iter.next();
            System.out.println(TupleSet.toString(tuple));
        }
    }
}
