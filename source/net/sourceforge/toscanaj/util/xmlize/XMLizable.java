/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

import  org.jdom.Element;


/**
 * This serialization interface is used for the AnacondaJMainPanel model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML.
 */
public interface XMLizable {

    /**
     * Write this element as the content of elem.
     */
    public abstract Element toXML();

    /**
     * Read this element as the content of elem.
     */
    public abstract void readXML(Element elem) throws XMLSyntaxError;
};