/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.adapters.DOMAdapter;
import org.jdom.input.DOMBuilder;

import java.io.File;
import java.io.FileInputStream;

/**
 * This class provides a mechanism to read an XMLizable object from a
 * filename.
 */
public class XMLReader {
    /**
     * The constructor does the reading from a file and throws if there is a *
     * problem.
     */
    public XMLReader(File file, XMLizable object) throws
            XMLSyntaxError {
        try {
            // open stream on file
            FileInputStream in;
            in = new FileInputStream(file);

            DOMAdapter domAdapter = new org.jdom.adapters.XercesDOMAdapter();
            org.w3c.dom.Document w3cdoc = domAdapter.getDocument(in, false);

            // create JDOM document
            DOMBuilder builder =
                    new DOMBuilder("org.jdom.adapters.XercesDOMAdapter");
            Document document = builder.build(w3cdoc);
            Element element = document.getRootElement();
            object.readXML(element);
        } catch (XMLSyntaxError ex) {
            throw ex;
        } catch (Exception ex) {
            throw new XMLSyntaxError(ex.getMessage());
        }
    }

}