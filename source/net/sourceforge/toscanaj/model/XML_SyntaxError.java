/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

/**
 * This serialization interface is used for the AnacondaJMainPanel model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML.
 */

public class XML_SyntaxError extends Exception {

    public XML_SyntaxError(String reason) {
	super(reason);
    }
};
