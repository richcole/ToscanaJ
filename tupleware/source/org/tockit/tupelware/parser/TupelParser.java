/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.tockit.tupelware.model.TupelSet;


/**
 * Parser to parse tupels from files.
 */
public class TupelParser {
    /**
     * Reads a list of tupels from a tab-delimited file.
     * 
     * The format used is one tupel per line, the elements delimited
     * with tabs. No escaping, so no tabs in the elements are allowed.
     * 
     * If a line doesn't contain at least two entries it is consider a
     * comment.
     * 
     * @return a Set of Object[] representing the tupels parsed
     */
    public static TupelSet parseTabDelimitedTupels(Reader input) throws IOException {
        TupelSet retVal = null;
        BufferedReader buffReader = new BufferedReader(input);
        while(true) {
            String line = buffReader.readLine();
            if(line == null) {
                break;            
            }
            StringTokenizer tokenizer = new StringTokenizer(line, "\t", false);
            // lines without tabs are considered comments
            if(tokenizer.countTokens() <= 1) {
                continue;
            }
            String[] tupel = new String[tokenizer.countTokens()];
            int i = 0;
            while(tokenizer.hasMoreTokens()) {
                tupel[i] = tokenizer.nextToken();
                i++;
            }
            if(retVal == null) {
                retVal = new TupelSet(tupel); 
            } else {
                retVal.addTupel(tupel);
            }
        }
        return retVal;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        TupelSet result = parseTabDelimitedTupels(new FileReader(new File(args[0])));
        System.out.println("Vars:");
        System.out.println(TupelSet.toString(result.getVariableNames()));
        System.out.println("Tupels:");
        Set tupels = result.getTupels();
        for (Iterator iter = tupels.iterator(); iter.hasNext();) {
            Object[] tupel = (Object[]) iter.next();
            System.out.println(TupelSet.toString(tupel));
        }
    }
}
