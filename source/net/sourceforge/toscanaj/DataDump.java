/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.events.EventBroker;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
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
 * 6: filter clause given for non-DB file
 */
public class DataDump {
    /**
     * The main procedure.
     */
    protected static void dumpData(File file, String filterClause, boolean includeLists) {
        // parse input
        ConceptualSchema schema = null;
        EventBroker broker = new EventBroker();
        DatabaseConnection.initialize(broker);
        try {
            schema = CSXParser.parse(new EventBroker(), file);
        } catch (DataFormatException e) {
            System.err.println("Could not parse input.");
            System.err.println("- " + e.getMessage());
            if (e.getOriginal() != null) {
                System.err.println("Detail:");
                System.err.println("- " + e.getOriginal().getMessage());
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
        // create concept for filtering if needed
        DatabaseConnectedConcept filterConcept = null;
        /// @todo don't generate multiple connections, instead send the same connection object.
        if (filterClause != null) {
            try {
                filterConcept = new DatabaseConnectedConcept(schema.getDatabaseInfo());
            } catch (Exception e) {
                System.err.println("Couldn't create filter for database");
                System.exit(4);
            }
            filterConcept.setObjectClause(filterClause);
        }
        // create output structure
        Document output = new Document(new Element("csxDump"));
        // dump all diagrams
        for (int diag = 0; diag < schema.getNumberOfDiagrams(); diag++) {
            dumpDiagram(filterDiagram(schema.getDiagram(diag), filterConcept),
                    output.getRootElement(), includeLists);
        }
        // write XML to stdout
        XMLOutputter outputter = new XMLOutputter("    ", true);
        try {
            outputter.output(output, System.out);
        } catch (Exception e) {
            System.err.println("Could not write output.");
            e.printStackTrace();
            System.exit(5);
        }
    }

    /**
     * Dumps a single diagram into the given JDOM element.
     */
    protected static void dumpDiagram(Diagram2D diagram, Element targetElement, boolean includeLists) {
        Element diagElem = new Element("diagram");
        targetElement.addContent(diagElem);
        diagElem.setAttribute("title", diagram.getTitle());
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            Concept cur = diagram.getNode(i).getConcept();

            Element conceptElem = new Element("concept");
            diagElem.addContent(conceptElem);

            Element intentElem = new Element("intent");
            intentElem.setAttribute("size", Integer.toString(cur.getIntentSize()));
            conceptElem.addContent(intentElem);

            Element extentElem = new Element("extent");
            extentElem.setAttribute("size", Integer.toString(cur.getExtentSize()));
            conceptElem.addContent(extentElem);

            Element attrContElem = new Element("attributeContingent");
            attrContElem.setAttribute("size", Integer.toString(cur.getAttributeContingentSize()));
            conceptElem.addContent(attrContElem);

            Element objContElem = new Element("objectContingent");
            objContElem.setAttribute("size", Integer.toString(cur.getObjectContingentSize()));
            conceptElem.addContent(objContElem);

            if (includeLists) {
                Iterator it;
                it = cur.getIntentIterator();
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("attribute");
                    newElem.addContent(name);
                    intentElem.addContent(newElem);
                }

                it = cur.getExtentIterator();
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("object");
                    newElem.addContent(name);
                    extentElem.addContent(newElem);
                }

                it = cur.getAttributeContingentIterator();
                while (it.hasNext()) {
                    String name = it.next().toString();
                    Element newElem = new Element("attribute");
                    newElem.addContent(name);
                    attrContElem.addContent(newElem);
                }

                it = cur.getObjectContingentIterator();
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
     * Creates a filtered diagram.
     *
     * This is copy&paste code of DiagramController.getSimpleDiagram(int) -- we
     * might want to get a common code base here. If the filterConcept is null,
     * a copy of the input is returned.
     */
    protected static Diagram2D filterDiagram(Diagram2D inputDiagram, DatabaseConnectedConcept filterConcept) {
        SimpleLineDiagram retVal = new SimpleLineDiagram();
        Hashtable nodeMap = new Hashtable();

        retVal.setTitle(inputDiagram.getTitle());
        for (int i = 0; i < inputDiagram.getNumberOfNodes(); i++) {
            DiagramNode oldNode = inputDiagram.getNode(i);
            DiagramNode newNode;
            if (filterConcept == null) {
                newNode = new DiagramNode("filtered:" + oldNode.getIdentifier(),
                        oldNode.getPosition(),
                        oldNode.getConcept(),
                        oldNode.getAttributeLabelInfo(),
                        oldNode.getObjectLabelInfo(),
                        null);
            } else {
                newNode = new DiagramNode("filtered:" + oldNode.getIdentifier(),
                        oldNode.getPosition(),
                        oldNode.getConcept().filterByContingent(filterConcept),
                        oldNode.getAttributeLabelInfo(),
                        oldNode.getObjectLabelInfo(),
                        null);
            }
            retVal.addNode(newNode);
            nodeMap.put(oldNode, newNode);
        }
        for (int i = 0; i < inputDiagram.getNumberOfLines(); i++) {
            DiagramLine line = inputDiagram.getLine(i);
            DiagramNode from = (DiagramNode) nodeMap.get(line.getFromNode());
            DiagramNode to = (DiagramNode) nodeMap.get(line.getToNode());
            retVal.addLine(from, to);

            // add direct neighbours to concepts
            AbstractConceptImplementation concept1 =
                    (AbstractConceptImplementation) from.getConcept();
            AbstractConceptImplementation concept2 =
                    (AbstractConceptImplementation) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        // build transitive closures for each concept
        for (int i = 0; i < retVal.getNumberOfNodes(); i++) {
            ((AbstractConceptImplementation) retVal.getNode(i).getConcept()).buildClosures();
        }

        return retVal;
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