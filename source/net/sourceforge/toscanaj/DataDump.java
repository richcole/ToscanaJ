/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.tockit.events.EventBroker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * This is an executable application that takes an CSX file and dumps the data
 * into a resolved format.
 *
 * The call looks like this:
 *
 *   DataDump [options] filename.csx
 *
 * And the dump will end up on stdout. Run "DataDump -help" for options.
 *
 * Return values:
 * 0: everything went well
 * 1: error in parameters
 * 2: could not find/parse input
 * 3: reserved
 * 4: could not connect to database
 * 5: could not write output
 * 6: unknown problem
 */
public class DataDump {
    /**
     * The main procedure.
     */
    protected static void dumpData(File inputFile, String filterClause, boolean includeLists) {
        // parse input
        ConceptualSchema schema = null;
        EventBroker broker = new EventBroker();
        DatabaseConnection.initialize(broker);
        try {
            schema = CSXParser.parse(new EventBroker(), inputFile);
        } catch (DataFormatException e) {
            System.err.println("Could not parse input.");
            System.err.println("- " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Detail:");
                System.err.println("- " + e.getCause().getMessage());
            }
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Could not parse input.");
            System.err.println("- " + e.getMessage());
            System.exit(2);
        }
        try {
            DatabaseConnection.getConnection().connect(schema.getDatabaseInfo());
        } catch (DatabaseException e) {
            System.err.println("Could not connect to the database.");
            System.err.println("- " + e.getMessage());
            System.exit(4);
        }
        try {
        	dumpData(schema, System.out, filterClause, includeLists, includeLists);
        } catch (IOException e) {
            System.err.println("Could not write output.");
            e.printStackTrace();
            System.exit(5);
        } catch (Exception e) {
            System.err.println("Unknown problem occured.");
            e.printStackTrace();
            System.exit(6);
        }
    }

    public static void dumpData(ConceptualSchema schema, OutputStream outputStream, String filterClause,
                                  boolean includeContingentLists, boolean includeIntentExtent) 
    					throws IOException {
	    DiagramHistory diagramHistory = new DiagramHistory();
        // create output structure
        Document output = new Document(new Element("csxDump"));

	    if (filterClause != null && filterClause.length() != 0) {
	        diagramHistory.addDiagram(new SimpleLineDiagram());
	        diagramHistory.addDiagram(new SimpleLineDiagram());
	        ConceptImplementation filterConcept = new ConceptImplementation();
	        filterConcept.addObject(new FCAElementImplementation(filterClause));
	        diagramHistory.next(filterConcept);
        	output.getRootElement().setAttribute("filterClause", filterClause);
        }

        // dump all diagrams
        for (int diag = 0; diag < schema.getNumberOfDiagrams(); diag++) {
            dumpDiagram(schema.getDiagram(diag), schema.getDatabaseInfo(), diagramHistory,
                    output.getRootElement(), includeContingentLists, includeIntentExtent);
        }

        // write XML to file
        XMLOutputter outputter = new XMLOutputter("    ", true);
        outputter.output(output, outputStream);
    }

    /**
     * Dumps a single diagram into the given JDOM element.
     */
    protected static void dumpDiagram(Diagram2D diagram, DatabaseInfo dbInfo, DiagramHistory diagramHistory, 
    								    Element targetElement, boolean includeContingentLists, boolean includeIntentExtent) {
    	ConceptInterpreter interpreter = new DatabaseConnectedConceptInterpreter(dbInfo);
        ConceptInterpretationContext extContext =
                    new ConceptInterpretationContext(diagramHistory, new EventBroker());
        extContext.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
        ConceptInterpretationContext contContext =
                    new ConceptInterpretationContext(diagramHistory, new EventBroker());
        contContext.setObjectDisplayMode(ConceptInterpretationContext.CONTINGENT);
        
        Element diagElem = new Element("diagram");
        targetElement.addContent(diagElem);
        diagElem.setAttribute("title", diagram.getTitle());
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            Concept cur = diagram.getNode(i).getConcept();

            Element conceptElem = new Element("concept");
            diagElem.addContent(conceptElem);
            if(interpreter.isRealized(cur, extContext)) {
            	conceptElem.setAttribute("realized", "true");
            } else {
                conceptElem.setAttribute("realized", "false");
            }

            Element intentElem = new Element("intent");
            intentElem.setAttribute("size", Integer.toString(cur.getIntentSize()));
            conceptElem.addContent(intentElem);

            Element extentElem = new Element("extent");
            extentElem.setAttribute("size", Integer.toString(interpreter.getObjectCount(cur, extContext)));
            conceptElem.addContent(extentElem);

            Element attrContElem = new Element("attributeContingent");
            attrContElem.setAttribute("size", Integer.toString(interpreter.getAttributeCount(cur, contContext)));
            conceptElem.addContent(attrContElem);

            Element objContElem = new Element("objectContingent");
            objContElem.setAttribute("size", Integer.toString(interpreter.getObjectCount(cur, contContext)));
            conceptElem.addContent(objContElem);

            if (includeIntentExtent) {
                Iterator it;
                it = cur.getIntentIterator();
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("attribute");
                    newElem.addContent(name);
                    intentElem.addContent(newElem);
                }

                it = interpreter.getObjectSetIterator(cur, extContext);
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("object");
                    newElem.addContent(name);
                    extentElem.addContent(newElem);
                }
            }
            
            if (includeContingentLists) {
                Iterator it;
                it = interpreter.getAttributeSetIterator(cur, contContext);
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("attribute");
                    newElem.addContent(name);
                    attrContElem.addContent(newElem);
                }

                it = interpreter.getObjectSetIterator(cur, contContext);
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("object");
                    newElem.addContent(name);
                    objContElem.addContent(newElem);
                }
            }
        }
    }

    /**
     * Prints the usage information.
     */
    protected static void printUsage(PrintStream stream) {
        stream.println("Usage:");
        stream.println("  DataDump [options] filename.csx");
        stream.println("Options:");
        stream.println("  -help / -? : print this message and exit");
        stream.println("  -filter \"[SQL-Clause]\" : filter all queries with the given clause");
        stream.println("  -include-lists : include all lists of objects/attributes in the output");
    }

    /**
     * The main method called from command line.
     */
    public static void main(String[] args) {
        boolean includeLists = false;
        String filename = null;
        String filterClause = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-include-lists")) {
                includeLists = true;
            } else if (args[i].equals("-help") || args[i].equals("-?")) {
                printUsage(System.out);
                System.exit(0);
            } else if (args[i].equals("-filter")) {
                if (i == args.length - 1) {
                    System.err.println("No filter clause given for '-filter' option");
                    printUsage(System.err);
                    System.exit(1);
                }
                i++;
                filterClause = "(" + args[i] + ")";
            } else if (args[i].charAt(0) != '-') {
                filename = args[i];
            } else {
                System.err.println("Unknown parameter");
                printUsage(System.err);
                System.exit(1);
            }
        }
        if (filename == null) {
            System.err.println("No file name given");
            printUsage(System.err);
            System.exit(1);
        }
        dumpData(new File(filename), filterClause, includeLists);
        System.exit(0);
    }
}
