/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

import java.io.File;
import java.io.FileOutputStream;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * This class provides a mechanism to write an XMLizable object to a file.
 */
public class XMLWriter {
    public static void write(final File file, final XMLizable object)
            throws Exception {
        // open stream on file
        final FileOutputStream out = new FileOutputStream(file);
        final XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent("    ");
        outputter.setNewlines(true);

        final Document document = new Document(object.toXML());
        outputter.output(document, out);

        out.close();
    }

}
