package net.sourceforge.toscanaj.model;

/**
 * This serialization interface is used for the AnacondaJ model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML. 
 */

public class XML_SyntaxError extends Exception {

    public XML_SyntaxError(String reason) {
	super(reason);
    }
};
