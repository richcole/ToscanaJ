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
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;
import org.tockit.events.EventBroker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

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
 * 0:  everything went well
 * 1:  error in parameters
 * 2:  could not find/parse input
 * 3:  reserved
 * 4:  could not connect to database
 * 5:  could not write output
 * 6:  problem writing an image
 * 99: unknown problem
 */
public class DataDump {
    private static final String allowedChars = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
    
    /**
     * The main procedure.
     */
    protected static void dumpData(File inputFile, String filterClause, boolean includeLists, String graphicFormatExtension) {
        // parse input
        ConceptualSchema schema = null;
        EventBroker broker = new EventBroker();
        DatabaseConnection.initialize(broker);
        ConceptInterpreter interpreter = new DirectConceptInterpreter();
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
            DatabaseInfo databaseInfo = schema.getDatabaseInfo();
            if(databaseInfo != null) {
                DatabaseConnection.getConnection().connect(databaseInfo);
                URL location = databaseInfo.getEmbeddedSQLLocation();
                if (location != null) {
                    DatabaseConnection.getConnection().executeScript(location);
                }
                interpreter = new DatabaseConnectedConceptInterpreter(databaseInfo);
            }
        } catch (DatabaseException e) {
            System.err.println("Could not connect to the database.");
            System.err.println("- " + e.getMessage());
            System.exit(4);
        }
        GraphicFormat graphicFormat =  null;
        if (graphicFormatExtension != null) {
            graphicFormat = GraphicFormatRegistry.getTypeByExtension(graphicFormatExtension);
        }
        try {
        	dumpData(schema, interpreter, System.out, filterClause, includeLists, includeLists, graphicFormat);
        } catch (IOException e) {
            System.err.println("Could not write output.");
            e.printStackTrace();
            System.exit(5);
        } catch (ImageGenerationException e) {
            System.err.println("Could not write image.");
            e.printStackTrace();
            System.exit(6);
        } catch (Exception e) {
            System.err.println("Unknown problem occured.");
            e.printStackTrace();
            System.exit(99);
        }
    }

    public static void dumpData(ConceptualSchema schema, ConceptInterpreter interpreter, OutputStream outputStream, String filterClause,
                                  boolean includeContingentLists, boolean includeIntentExtent, GraphicFormat graphicFormat) 
    					throws IOException, ImageGenerationException, DatabaseException {
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
            dumpDiagram(schema.getDiagram(diag), interpreter, diagramHistory,
                    output.getRootElement(), includeContingentLists, includeIntentExtent);
        }
        
        // export pictures if needed
        // @TODO doing this after the dump means querying the DB twice
        if(graphicFormat != null) {
            DiagramView view = new DiagramView();
            view.setConceptInterpreter(interpreter);
            view.setConceptInterpretationContext(new ConceptInterpretationContext(diagramHistory, new EventBroker()));
            ObjectLabelView.setDefaultQuery(AggregateQuery.COUNT_QUERY);
            DiagramExportSettings settings = new DiagramExportSettings(graphicFormat, 1280, 1024, false);
            for (int i = 0; i < schema.getNumberOfDiagrams(); i++) {
                Diagram2D diagram = schema.getDiagram(i);
                view.showDiagram(diagram);
                Properties metaData = new Properties();
                File outputFile = new File(escapeFileName(diagram.getTitle()) + "."  + graphicFormat.getExtensions()[0]);
                graphicFormat.getWriter().exportGraphic(view, settings, outputFile, metaData);
            }
        }

        // write XML to file
        XMLOutputter outputter = new XMLOutputter("    ", true);
        outputter.output(output, outputStream);
    }

    private static String escapeFileName(String title) {
        StringBuffer retVal = new StringBuffer(title.length());
        for (int i = 0; i < title.length(); i++) {
            char curChar = title.charAt(i);
            if(allowedChars.indexOf(curChar) != -1) {
                retVal.append(curChar);
            } else {
                retVal.append('_');
            }
        }
        return retVal.toString();
    }

    /**
     * Dumps a single diagram into the given JDOM element.
     * @throws DatabaseException 
     */
    protected static void dumpDiagram(Diagram2D diagram, ConceptInterpreter interpreter, DiagramHistory diagramHistory, 
    								    Element targetElement, boolean includeContingentLists, boolean includeIntentExtent) throws DatabaseException {
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
                it = interpreter.getAttributeContingentIterator(cur, contContext);
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
        stream.println("  -filter \"[SQL-clause]\" : filter all queries with the given clause");
        stream.println("  -include-lists : include all lists of objects/attributes in the output");
        stream.println("  -export-graphic \"[graphic format]\" : export in the format specified\n" +
                       "                                         (file name extension)");
    }

    /**
     * The main method called from command line.
     */
    public static void main(String[] args) {
        boolean includeLists = false;
        String filename = null;
        String filterClause = null;
        String graphicFormatExtension = null;
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
            } else if (args[i].equals("-export-graphic")) {
                if (i == args.length - 1) {
                    System.err.println("No graphic format given for '-export-graphic' option");
                    printUsage(System.err);
                    System.exit(1);
                }
                ToscanaJ.loadPlugins();
                i++;
                graphicFormatExtension = args[i];
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
        dumpData(new File(filename), filterClause, includeLists, graphicFormatExtension);
        System.exit(0);
    }
}
