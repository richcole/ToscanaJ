/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

import org.jdom.Element;

/**
 * This serialisation interface is used for the model. All elements of the model
 * support this interface and in addition have a constructor that calls readXML.
 * 
 * @todo separate this aspect of the code from the model. Turning the model into
 *       XML and back should not be part of the models interface but of some
 *       other classes, maybe using introspection and some kind of mapping
 *       information. JAXB would be a good candidate.
 */
public interface XMLizable {

    /**
     * Write this element as an XML DOM element.
     */
    Element toXML();

    /**
     * Read this element as the content of the given element.
     */
    void readXML(Element elem) throws XMLSyntaxError;
}
