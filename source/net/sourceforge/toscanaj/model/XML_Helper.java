package net.sourceforge.toscanaj.model;

import  net.sourceforge.toscanaj.model.XML_SyntaxError;
import  net.sourceforge.toscanaj.model.XML_Serializable;
import  org.jdom.Element;


/**
 * This serialization interface is used for the AnacondaJMainPanel model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML.
 */
public abstract class XML_Helper {

    static public Element insertElement(String name, Element parent)
    {
        Element elem = new Element(name);
        parent.addContent(elem);
        return elem;
    }

    static public Element mustbe(String name, Element parent)
            throws XML_SyntaxError
    {
        Element child = parent.getChild(name);
        if ( child == null ) {
            String reason = "Expected element '" +
                    name + "' in '" + parent.getName() + "'";
            throw new XML_SyntaxError(reason);
        }
        return child;
    }

}

