package net.sourceforge.toscanaj;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.parser.CSXParser;

import java.io.File;
import java.io.PrintStream;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.XMLOutputter;

/**
 * This is an executable application that takes an CSX file and dumps the data
 * into a resolved format.
 *
 * The call looks like this:
 *
 *   DataDump [-include-lists] filename.csx
 *
 * And the dump will end up on stdout.
 */
public class DataDump {
    /**
     * The main procedure.
     */
    protected static void dumpData(File file, boolean includeLists) {
        // parse input
        ConceptualSchema schema = null;
        try {
            schema = CSXParser.parse(file);
        }
        catch(Exception e) {
            System.err.println("Could not parse input.");
            e.printStackTrace();
            return;
        }
        // create output structure
        Document output = new Document(new Element("csxDump"));
        // dump all diagrams
        for(int diag = 0; diag < schema.getNumberOfDiagrams(); diag++) {
            dumpDiagram(schema.getDiagram(diag), output.getRootElement(), includeLists);
        }
        // write XML to stdout
        XMLOutputter outputter = new XMLOutputter("    ", true);
        try {
            outputter.output(output,System.out);
        }
        catch(Exception e) {
            System.err.println("Could not write output.");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Dumps a single diagram into the given JDOM element.
     */
    protected static void dumpDiagram(Diagram2D diagram, Element targetElement, boolean includeLists) {
        Element diagElem = new Element("diagram");
        targetElement.addContent(diagElem);
        diagElem.addAttribute("title",diagram.getTitle());
        for(int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            Concept cur = diagram.getNode(i).getConcept();

            Element conceptElem = new Element("concept");
            diagElem.addContent(conceptElem);

            Element intentElem = new Element("intent");
            intentElem.addAttribute("size", Integer.toString(cur.getIntentSize()));
            conceptElem.addContent(intentElem);

            Element extentElem = new Element("extent");
            extentElem.addAttribute("size", Integer.toString(cur.getExtentSize()));
            conceptElem.addContent(extentElem);

            Element attrContElem = new Element("attributeContingent");
            attrContElem.addAttribute("size", Integer.toString(cur.getAttributeContingentSize()));
            conceptElem.addContent(attrContElem);

            Element objContElem = new Element("objectContingent");
            objContElem.addAttribute("size", Integer.toString(cur.getObjectContingentSize()));
            conceptElem.addContent(objContElem);

            if(includeLists) {
                Iterator it;
                it = cur.getIntentIterator();
                while(it.hasNext()) {
                    String name = (String) it.next();
                    Element newElem = new Element("attribute");
                    newElem.addContent(name);
                    intentElem.addContent(newElem);
                }

                it = cur.getExtentIterator();
                while(it.hasNext()) {
                    String name = (String) it.next();
                    Element newElem = new Element("object");
                    newElem.addContent(name);
                    extentElem.addContent(newElem);
                }

                it = cur.getAttributeContingentIterator();
                while(it.hasNext()) {
                    String name = (String) it.next();
                    Element newElem = new Element("attribute");
                    newElem.addContent(name);
                    attrContElem.addContent(newElem);
                }

                it = cur.getObjectContingentIterator();
                while(it.hasNext()) {
                    String name = (String) it.next();
                    Element newElem = new Element("object");
                    newElem.addContent(name);
                    objContElem.addContent(newElem);
                }
            }
        }
    }

    /**
     * Prints the usage information.
     *
     * @todo implement
     */
    protected static void printUsage(PrintStream stream) {
        stream.println("Usage:");
        stream.println("  DataDump [options] filename.csx");
        stream.println("Options:");
        stream.println("  -include-lists : include all lists of objects/attributes in the output");
    }

    /**
     * The main method called from command line.
     */
    public static void main(String[] args) {
        boolean includeLists = false;
        String filename = null;
        for(int i = 0; i < args.length; i++ ) {
            if(args[i].equals("-include-lists")) {
                includeLists = true;
            }
            else if(args[i].charAt(0)!='-') {
                filename = args[i];
            }
            else {
                System.err.println("Unknown parameter");
                printUsage(System.err);
                return;
            }
        }
        if(filename == null) {
            System.err.println("No file name given");
            printUsage(System.err);
            return;
        }
        dumpData(new File(filename), includeLists);
        return;
    }
}