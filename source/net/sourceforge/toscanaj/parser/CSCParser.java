/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAObjectImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;

public class CSCParser {
    private static final int TARGET_DIAGRAM_HEIGHT = 460;
    private static final int TARGET_DIAGRAM_WIDTH = 600;
    
    /// @todo now we start hacking...
    private static Hashtable sectionIdMap = new Hashtable();
    
    protected static final class QueryMap {
    	public String name;
    	public Hashtable map = new Hashtable();
    }
    
    protected static final class SectionTypeNotSupportedException
        extends DataFormatException {

        public SectionTypeNotSupportedException() {
            super();
        }

        public SectionTypeNotSupportedException(String message) {
            super(message);
        }

        public SectionTypeNotSupportedException(
            String message,
            Throwable cause) {
            super(message, cause);
        }

        public SectionTypeNotSupportedException(Throwable cause) {
            super(cause);
        }

    }
    protected static class CSCTokenizer {
        private BufferedReader inputReader;
        private String currentToken;
        private int currentLine = 1;
        private boolean newLineStarted = true;

		public CSCTokenizer(File file) throws FileNotFoundException, IOException, DataFormatException {
		    this.inputReader = new BufferedReader(new FileReader(file));
		    advance();
		}
		
		public String getCurrentToken() {
			return this.currentToken;
		}
		
		public void advance() throws IOException, DataFormatException {
			int character;
			this.newLineStarted = false;
			do {
				character = this.inputReader.read();
				if(character == '\n') {
					this.currentLine += 1;
					this.newLineStarted = true;
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
                if(character == '\\') {
                	this.currentToken += (char) this.inputReader.read();
                    character = this.inputReader.read();
                }                	
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
        
        public boolean newLineHasStarted() {
        	return this.newLineStarted;
        }
    }
    
    protected static class CSCFileSectionParser {
    	private String token;
    	public CSCFileSectionParser(String token) {
    		this.token = token;
    	}
    	public String getStartToken() {
    		return token;
    	}
    	public Object parse(CSCTokenizer tokenizer) throws IOException, DataFormatException {
    		throw new SectionTypeNotSupportedException("parse() in " + this.getClass().getName() + " not yet implemented.");
    	}
    	protected void consumeToken(CSCTokenizer tokenizer, String token) throws IOException, DataFormatException{
    		if(!tokenizer.getCurrentToken().equals(token)) {
    			throw new DataFormatException("Expected token '" + token + "' in line " + tokenizer.currentLine);
    		}
    		tokenizer.advance();
    	}
    }
    
    protected static final CSCFileSectionParser REMARK_SECTION = new CSCFileSectionParser("REMARK");

    protected static final CSCFileSectionParser FORMAL_CONTEXT_SECTION = new CSCFileSectionParser("FORMAL_CONTEXT"){
        public Object parse(CSCTokenizer tokenizer) throws IOException, DataFormatException {
            List objects = new ArrayList();
            List attributes = new ArrayList();
			String contextTitle = tokenizer.getCurrentToken();
            sectionIdMap.put(contextTitle,contextTitle);
			
            while(!tokenizer.getCurrentToken().equals("OBJECTS")) { // ignore everything before the objects
            	if(tokenizer.getCurrentToken().equals("TITLE")) {
            	    tokenizer.advance();
            	    sectionIdMap.put(tokenizer.getCurrentToken(), contextTitle);
            	    contextTitle = tokenizer.getCurrentToken();
            	}
                tokenizer.advance();
            }
            tokenizer.advance(); // skip "OBJECTS"

            ContextImplementation context = new ContextImplementation(contextTitle);
            
            while(!tokenizer.getCurrentToken().equals("ATTRIBUTES")) { // find objects until attributes come
                tokenizer.advance(); // skip number
                tokenizer.advance(); // skip id
                objects.add(new FCAObjectImplementation(tokenizer.getCurrentToken())); // use name
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
				if(row.length() == 0) {
					throw new DataFormatException("Missing row in the relation in line " + tokenizer.currentLine);
				}
				if(row.length() < attributes.size()) {
					throw new DataFormatException("Incomplete row in the relation in line " + tokenizer.currentLine);
				}
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

			consumeToken(tokenizer,";");            

            return context;
        }
    };
    protected static final CSCFileSectionParser LINE_DIAGRAM_SECTION = new CSCFileSectionParser("LINE_DIAGRAM"){
        public Object parse(CSCTokenizer tokenizer) throws IOException, DataFormatException {
        	SimpleLineDiagram diagram = new SimpleLineDiagram();
        	
        	String identifier = tokenizer.getCurrentToken();
        	tokenizer.advance();
        	
        	consumeToken(tokenizer, "=");
        	
        	if(tokenizer.getCurrentToken().equals("TITLE")) {
        		tokenizer.advance();
        		String title = tokenizer.getCurrentToken();
        		tokenizer.advance();
        		if(!title.equals("")) {
        			identifier = title;
        		}
        	}
        	diagram.setTitle(identifier);
        	sectionIdMap.put(identifier,identifier);
        	
            while(!tokenizer.getCurrentToken().equals("POINTS")) { // we ignore remark, special list and unitlength
                tokenizer.advance();
            }
            tokenizer.advance();

            while(!tokenizer.getCurrentToken().equals("LINES")) {
            	String id = tokenizer.getCurrentToken();
            	tokenizer.advance();
            	
                double x = Double.parseDouble(tokenizer.getCurrentToken());
                tokenizer.advance();
                double y = Double.parseDouble(tokenizer.getCurrentToken());
                tokenizer.advance();
                Point2D position = new Point2D.Double(x,y);
            	
            	DiagramNode node = new DiagramNode(diagram, id, position, new ConceptImplementation(), null, null, null);
            	diagram.addNode(node);
            }
            tokenizer.advance();

            while(!tokenizer.getCurrentToken().equals("OBJECTS")) {
            	String linecode = tokenizer.getCurrentToken();
            	
                String from = linecode.substring(1,linecode.indexOf(','));
                String to = linecode.substring(linecode.indexOf(',') + 1,linecode.length() - 1);
                
                while(to.startsWith(" ")) {
                	to = to.substring(1);
                }
                
                DiagramNode fromNode = diagram.getNode(from);
                DiagramNode toNode = diagram.getNode(to);
                diagram.addLine(fromNode, toNode);
                
                ConceptImplementation fromConcept = (ConceptImplementation) fromNode.getConcept();
                ConceptImplementation toConcept = (ConceptImplementation) toNode.getConcept();
            	fromConcept.addSubConcept(toNode.getConcept());
            	toConcept.addSuperConcept(fromConcept);
            	
                tokenizer.advance();
            }
            tokenizer.advance();

            while(!tokenizer.getCurrentToken().equals("ATTRIBUTES")) { 
            	DiagramNode node = diagram.getNode(tokenizer.getCurrentToken());
                tokenizer.advance();
            	
                tokenizer.advance(); // ignore id of object
                
                String content = tokenizer.getCurrentToken();
                tokenizer.advance();
                
                LabelInfo labelInfo;
                if(!tokenizer.newLineHasStarted()) {
                	// still on the same line --> we have a formatting string
                	String formattingString = tokenizer.getCurrentToken();
                	tokenizer.advance();
                	
                	labelInfo = parseLabelInfo(formattingString);
                } else {
                	labelInfo = new LabelInfo();
                }
                
                ConceptImplementation concept = (ConceptImplementation) node.getConcept();
                concept.addObject(content);
                
                node.setObjectLabelInfo(labelInfo);
            }
            tokenizer.advance();

            while(!tokenizer.getCurrentToken().equals("CONCEPTS")) { 
                DiagramNode node = diagram.getNode(tokenizer.getCurrentToken());
                tokenizer.advance();

                tokenizer.advance(); // ignore id of attribute

                String content = tokenizer.getCurrentToken();
                tokenizer.advance();

                LabelInfo labelInfo;
                if(!tokenizer.newLineHasStarted()) {
                    // still on the same line --> we have a formatting string
                    String formattingString = tokenizer.getCurrentToken();
                    tokenizer.advance();

                    labelInfo = parseLabelInfo(formattingString);
                } else {
                    labelInfo = new LabelInfo();
                }

                ConceptImplementation concept = (ConceptImplementation) node.getConcept();
                concept.addAttribute(new Attribute(content));
                node.setAttributeLabelInfo(labelInfo);
            }
            tokenizer.advance();

            while(!tokenizer.getCurrentToken().equals(";")) { // we ignore the concept definitions 
                tokenizer.advance();
            }
            consumeToken(tokenizer, ";");
            
            for (Iterator iter = diagram.getNodes(); iter.hasNext();) {
                DiagramNode node = (DiagramNode) iter.next();
                ConceptImplementation concept = (ConceptImplementation) node.getConcept();
                concept.buildClosures();
            }
            
        	return diagram;
        }
        
        private String[] extractFormattingStringSegment(String formattingString) {
        	if(formattingString.length() == 0) {
        		return new String[]{null,""};
        	}
        	String segment, rest;
        	if(formattingString.startsWith("(")) {
        		int parPos = formattingString.indexOf(')');
        	    segment = formattingString.substring(1,parPos);
        	    rest = formattingString.substring(parPos + 1);
        	    int commaPos = rest.indexOf(',');
        	    if(commaPos != -1) {
        	        rest = rest.substring(commaPos + 1);
        	    }
        	} else {
	            int commaPos = formattingString.indexOf(',');
	            if(commaPos == -1) {
	                segment = new String(formattingString);
	                rest = "";
	            } else {
	            	segment = formattingString.substring(0,commaPos);
	            	rest = formattingString.substring(commaPos + 1);
	            }
        	}
            return new String[]{segment,rest};
        }

        private LabelInfo parseLabelInfo(String formattingString) {
        	LabelInfo retVal = new LabelInfo();
        	String[] nextSplit = extractFormattingStringSegment(formattingString);
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
            String offsetString = nextSplit[0];
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
            String alignmentString = nextSplit[0];
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
            formattingString = nextSplit[1];
            nextSplit = extractFormattingStringSegment(formattingString);
			
			// we support only offset and alignment at the moment
			if(offsetString != null && offsetString.length() != 0) {
				int commaPos = offsetString.indexOf(',');
				String xPart = offsetString.substring(0,commaPos);
				String yPart = offsetString.substring(commaPos + 1);
				retVal.setOffset(Double.parseDouble(xPart), -Double.parseDouble(yPart));
			}
			
			if(alignmentString.indexOf('l') != -1) {
				retVal.setTextAlignment(LabelInfo.ALIGNLEFT);
			} else if(alignmentString.indexOf('r') != -1) {
                retVal.setTextAlignment(LabelInfo.ALIGNRIGHT);
            } else if(alignmentString.indexOf('c') != -1) {
                retVal.setTextAlignment(LabelInfo.ALIGNCENTER);
            }
            
            return retVal;
        }
    };
    
    protected static final CSCFileSectionParser STRING_MAP_SECTION = new CSCFileSectionParser("STRING_MAP");
    protected static final CSCFileSectionParser IDENTIFIER_MAP_SECTION = new CSCFileSectionParser("IDENTIFIER_MAP");
    protected static final CSCFileSectionParser QUERY_MAP_SECTION = new CSCFileSectionParser("QUERY_MAP") {
           public Object parse(CSCTokenizer tokenizer) throws IOException, DataFormatException {
               QueryMap retval = new QueryMap();
               retval.name = tokenizer.getCurrentToken();
               tokenizer.advance();

               int line = tokenizer.getCurrentLine();
               consumeToken(tokenizer, "=");
               while(tokenizer.getCurrentLine() == line) {
                   tokenizer.advance(); // skip possible remarks
               }

               /// @todo tupels should be send as a couple of tokens
               while(!tokenizer.getCurrentToken().equals(";")) {
                   String tupel = tokenizer.getCurrentToken();
                   tokenizer.advance();
                   tupel = tupel.substring(1,tupel.length()-1);
                   int commaPos = tupel.indexOf(',');
                   String clause = resolveEscapes(tupel.substring(0,commaPos).trim());
                   clause = clause.substring(1,clause.length() -1);
                   String id = tupel.substring(commaPos + 1).trim();
                   retval.map.put(id, clause);
               }

               consumeToken(tokenizer, ";");

               return retval;
           }
       };
       
    protected static String resolveEscapes(String input) {
    	String output = "";
    	for(int i = 0; i < input.length(); i++) {
    		char curChar = input.charAt(i);
    		if(curChar == '\\') {
    			i++;
    			output += input.charAt(i);
    		} else {
    			output += curChar;
    		}
    	}
    	return output;
    }
    
    protected static final CSCFileSectionParser ABSTRACT_SCALE_SECTION = new CSCFileSectionParser("ABSTRACT_SCALE");
    protected static final CSCFileSectionParser CONCRETE_SCALE_SECTION = new CSCFileSectionParser("CONCRETE_SCALE");
    protected static final CSCFileSectionParser REALISED_SCALE_SECTION = new CSCFileSectionParser("REALISED_SCALE");
    protected static final CSCFileSectionParser DATABASE_SECTION = new CSCFileSectionParser("DATABASE");
    protected static final CSCFileSectionParser CONCEPTUAL_SCHEME_SECTION = new CSCFileSectionParser("CONCEPTUAL_SCHEME");
    protected static final CSCFileSectionParser CONCEPTUAL_FILE_SECTION = new CSCFileSectionParser("CONCEPTUAL_FILE");
    protected static final CSCFileSectionParser INCLUDE_FILE_ENTRY = new CSCFileSectionParser("#INCLUDE");
    
    protected static final CSCFileSectionParser[] CSC_FILE_SECTIONS_PARSERS = 
    								new CSCFileSectionParser[]{
    											REMARK_SECTION,
    											FORMAL_CONTEXT_SECTION,
    											LINE_DIAGRAM_SECTION,
    											STRING_MAP_SECTION,
    											IDENTIFIER_MAP_SECTION,
    											QUERY_MAP_SECTION,
    											ABSTRACT_SCALE_SECTION,
    											CONCRETE_SCALE_SECTION,
    											REALISED_SCALE_SECTION,
    											DATABASE_SECTION,
    											CONCEPTUAL_SCHEME_SECTION,
    											CONCEPTUAL_FILE_SECTION,
    											INCLUDE_FILE_ENTRY
    								}; 
    								
    protected CSCFileSectionParser currentSectionParser = null;
    
    public void importCSCFile(File file, ConceptualSchema schema) 
    								throws DataFormatException {
        try {
            CSCTokenizer tokenizer = new CSCTokenizer(file);
            
            List diagrams = new ArrayList();
            List contexts = new ArrayList();
            Hashtable queryMaps = new Hashtable();
            
            while(! tokenizer.done()) {
            	this.currentSectionParser = identifySectionParser(tokenizer);
				if(this.currentSectionParser == null) {
					// first round and we don't grok it
					throw new RuntimeException("The specified file is not a" +
												"CSC file.");
				}
            	try {
            		Object result = this.currentSectionParser.parse(tokenizer);
            		if(result instanceof Diagram2D) {
            			diagrams.add(result);
            		} else if (result instanceof ContextImplementation) {
            			contexts.add(result);
            		} else if (result instanceof QueryMap) {
            			QueryMap queryMap = (QueryMap) result;
            			queryMaps.put(queryMap.name, queryMap);
            		}
            	} catch (SectionTypeNotSupportedException e) {
            		System.err.println(e.getMessage());
            		// eat a whole section
            		while(!tokenizer.currentToken.equals(";")) {
            			tokenizer.advance();
            		}
            		tokenizer.advance();
            	}
            }
            
            List importedDiagrams = new ArrayList();
            
            for (Iterator iter = diagrams.iterator(); iter.hasNext();) {
                Object result = iter.next();
                if(result instanceof Diagram2D) {
                    Diagram2D diagram = (Diagram2D) result;
                    insertDiagram(schema, diagram, (QueryMap) queryMaps.get(sectionIdMap.get(diagram.getTitle())));
                    importedDiagrams.add(diagram.getTitle());
                }
            }

            for (Iterator iter = contexts.iterator(); iter.hasNext();) {
                Object result = iter.next();
                if(result instanceof ContextImplementation) {
                    ContextImplementation context = (ContextImplementation) result;
                	if(importedDiagrams.contains(context.getName())) {
                		continue;
                	}
                    LatticeGenerator lgen = new GantersAlgorithm();
                    Lattice lattice = lgen.createLattice(context);
                    Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, context.getName(), new DefaultDimensionStrategy());
                    insertDiagram(schema,diagram, (QueryMap) queryMaps.get(sectionIdMap.get(diagram.getTitle())));                	
                }
            }
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file", e);
        }
    }

    protected void insertDiagram(ConceptualSchema schema, Diagram2D diagram, QueryMap queryMap) {
    	if(queryMap != null) {
    		replaceObjects(diagram, queryMap);
    	}
        Diagram2D existingDiagram = schema.getDiagram(diagram.getTitle());
        rescale(diagram);
        if(existingDiagram != null) {
            schema.replaceDiagram(existingDiagram, diagram);
        } else {
        	schema.addDiagram(diagram);
        }
    }

    private void replaceObjects(Diagram2D diagram, QueryMap queryMap) {
    	Iterator nodes = diagram.getNodes();
    	while (nodes.hasNext()) {
            DiagramNode node = (DiagramNode) nodes.next();
            ConceptImplementation concept = (ConceptImplementation) node.getConcept();
            Iterator objIt = concept.getObjectContingentIterator();
            while (objIt.hasNext()) {
                Object object = objIt.next();
                if(queryMap.map.containsKey(object)) {
                	objIt.remove();
                	concept.addObject(queryMap.map.get(object));
                }
            }
        }
    }

    private void rescale(Diagram2D diagram) {
    	Rectangle2D bounds = diagram.getBounds();
        
        double scaleX;
        if(bounds.getWidth() == 0) {
            scaleX = Double.MAX_VALUE;
        } else {
            scaleX = TARGET_DIAGRAM_WIDTH / bounds.getWidth();
        }
        
        double scaleY;
        if(bounds.getHeight() == 0) {
            scaleY = Double.MAX_VALUE;
        } else {
            scaleY = TARGET_DIAGRAM_HEIGHT / bounds.getHeight();
        }
        
        double scale = (scaleX < scaleY) ? scaleX : scaleY;
        
        Iterator it = diagram.getNodes();
        while (it.hasNext()) {
            DiagramNode node = (DiagramNode) it.next();
            Point2D pos = node.getPosition();
            node.setPosition(new Point2D.Double(scale * pos.getX(), scale * pos.getY()));

            LabelInfo aLabel = node.getAttributeLabelInfo();
            if(aLabel != null) {
                Point2D offset = aLabel.getOffset();
                aLabel.setOffset(new Point2D.Double(scale * offset.getX(), scale * offset.getY()));
            }

            LabelInfo oLabel = node.getObjectLabelInfo();
            if(oLabel != null) {
                Point2D offset = oLabel.getOffset();
                oLabel.setOffset(new Point2D.Double(scale * offset.getX(), scale * offset.getY()));
            }
        }
    }
    
    protected CSCFileSectionParser identifySectionParser(CSCTokenizer tokenizer) throws IOException, DataFormatException {
    	for (int i = 0; i < CSC_FILE_SECTIONS_PARSERS.length; i++) {
            CSCFileSectionParser sectionType = CSC_FILE_SECTIONS_PARSERS[i];
            if(sectionType.getStartToken().equals(tokenizer.getCurrentToken())) {
            	tokenizer.advance();
            	return sectionType;
            }
        }
        return this.currentSectionParser;
    }
}
