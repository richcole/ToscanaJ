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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.Lattice;

public class CSCParser {
    protected static class CSCTokenizer {
        private BufferedReader inputReader;
        private String currentToken;
        private int currentLine = 1;

		public CSCTokenizer(File file) throws IOException, DataFormatException {
		    this.inputReader = new BufferedReader(new FileReader(file));
		    advance();
		}
		
		public String getCurrentToken() {
			return this.currentToken;
		}
		
		public void advance() throws IOException, DataFormatException {
			int character;
			do {
				character = this.inputReader.read();
				if(character == '\n') {
					this.currentLine += 1;
				}
			} while(Character.isWhitespace((char)character));
			this.currentToken = "";
			if(character == '\"') {
				advanceString();
			} else if (character == '(') {
				advanceTupel();
			} else {
                advanceNormal(character);
			}
		}

        public void advanceString() throws IOException, DataFormatException {
            int character = this.inputReader.read();
            int startLine = this.currentLine;
            while( character != -1 && character != '\"' ) {
                this.currentToken += (char) character;
                character = this.inputReader.read();
            }
            if(character != '\"') {
                throw new DataFormatException("Open quote from line " + startLine + " not matched.");
            }
        }

        public void advanceTupel() throws IOException, DataFormatException {
            int character = '(';
            int startLine = this.currentLine;
            do {
                this.currentToken += (char) character;
                character = this.inputReader.read();
            } while( character != -1 && character != ')' );
            if(character != ')') {
            	throw new DataFormatException("Open parenthesis from line " + startLine + " not matched.");
            } else {
            	this.currentToken += ")";
            }
        }

        public void advanceNormal(int character) throws IOException {
            while( character != -1 && !Character.isWhitespace((char)character) &&
                    character != '\"' && character != '(' ) {
                this.currentToken += (char) character;
                character = this.inputReader.read();
            }
        }

		public boolean done() throws IOException {
			return !this.inputReader.ready();
		}
		
        public int getCurrentLine() {
            return this.currentLine;
        }
    }
    
    public static void importCSCFile(File file, ConceptualSchema schema) 
    								throws FileNotFoundException, DataFormatException {
        try {
            CSCTokenizer tokenizer = new CSCTokenizer(file);
            
            while(! tokenizer.done()) {
				if(tokenizer.getCurrentToken().equals("FORMAL_CONTEXT")) {
				    tokenizer.advance();
				    String name = tokenizer.getCurrentToken();
				    tokenizer.advance();
				    if(!tokenizer.getCurrentToken().equals("=")) {
				    	throw new DataFormatException("Syntax error in line " + tokenizer.getCurrentLine() + " - expected '='");
				    }
					Context context = importContext(tokenizer);
	                LatticeGenerator lgen = new GantersAlgorithm();
	                Lattice lattice = lgen.createLattice(context);
	                Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, name, new DefaultDimensionStrategy());
	                schema.addDiagram(diagram);
				}

                tokenizer.advance();
            }
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file", e);
        }
    }
    
    private static Context importContext(CSCTokenizer tokenizer) throws IOException, DataFormatException {
        ContextImplementation context = new ContextImplementation();
        List objects = new ArrayList();
        List attributes = new ArrayList();
        
        while(!tokenizer.getCurrentToken().equals("OBJECTS")) { // ignore everything before the objects
        	tokenizer.advance();
        }
        tokenizer.advance(); // skip "OBJECTS"

        while(!tokenizer.getCurrentToken().equals("ATTRIBUTES")) { // find objects until attributes come
            tokenizer.advance(); // skip number
            tokenizer.advance(); // skip id
            objects.add(tokenizer.getCurrentToken()); // use name
            tokenizer.advance(); // next
        }
        tokenizer.advance(); // skip "ATTRIBUTES"
        context.getObjects().addAll(objects); // we have all objects
        
        while(!tokenizer.getCurrentToken().equals("RELATION")) { // find attributes until relation comes
            tokenizer.advance(); // skip number
            tokenizer.advance(); // skip id
            attributes.add(new Attribute(tokenizer.getCurrentToken(), null)); // use name
            tokenizer.advance(); // next
        }
        tokenizer.advance(); // skip "RELATION"
        tokenizer.advance(); // skip size ...
        tokenizer.advance(); // ... both parts of it
		context.getAttributes().addAll(attributes); // we have all attributes
        
        BinaryRelationImplementation relation = context.getRelationImplementation();
        
        // create relation
        Iterator objIt = objects.iterator(); // iterate over objects/rows
        while(objIt.hasNext()) {
        	Object object = objIt.next();
        	String row = tokenizer.getCurrentToken(); // get row string
        	Iterator attrIt = attributes.iterator(); // iterate over attributes
        	int i = 0; // count pos in string
        	while(attrIt.hasNext()) {
        		Object attribute = attrIt.next();
        		if(row.charAt(i) == '*') {
        			relation.insert(object,attribute); // hit --> add to relation
        		}
        		i++;
        	}
            tokenizer.advance(); // next row
        }
         
        return context;
    }
}
