package net.sourceforge.toscanaj.model;

import  net.sourceforge.toscanaj.model.XML_SyntaxError;
import  org.jdom.Element;


/**
 * This serialization interface is used for the AnacondaJ model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML. 
 */
public interface XML_Serializable {

    /**
     * Write this element as the content of elem.
     */
    public abstract void writeXML(Element elem);

    /**
     * Read this element as the content of elem.
     */
    public abstract void readXML(Element elem) throws XML_SyntaxError;
};
