/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * This class provides a mechanism to read an XMLizable object from a
 * filename.
 */
public class XMLWriter {
    public static void write(File file, XMLizable object)
            throws Exception {
        // open stream on file
        FileOutputStream out = new FileOutputStream(file);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent(true);
        outputter.setNewlines(true);

        Document document = new Document(object.toXML());
        outputter.output(document, out);
        
        out.close();
    }

}
