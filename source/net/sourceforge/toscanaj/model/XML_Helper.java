package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.XML_Serializable;
import org.jdom.*;


/**
 * This serialization interface is used for the AnacondaJMainPanel model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML.
 */
public abstract class XML_Helper {

    static public Element insertElement(String name, Element parent) {
        Element elem = new Element(name);
        parent.addContent(elem);
        return elem;
    }

    static public Element mustbe(String name, Element parent)
            throws XML_SyntaxError {
        Element child = parent.getChild(name);
        if (child == null) {
            String reason = "Expected element '" +
                    name + "' in '" + parent.getName() + "'";
            throw new XML_SyntaxError(reason);
        }
        return child;
    }

    static public boolean contains(Element parent, String name) {
        return (null != parent.getChild(name));
    }

    static public void checkName(String name, Element element) throws XML_SyntaxError {
        if (!element.getName().equals(name)) {
            throw new XML_SyntaxError("Expected Element: " + name);
        }

    }

    static public Attribute getAttribute(Element elem, String attributeName) throws XML_SyntaxError {
        Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XML_SyntaxError("Expected Attribute " + attributeName + " in Element " + elem.getName());
        }
        return attribute;
    }

    static public int getIntAttribute(Element elem, String attributeName) throws XML_SyntaxError {
        Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XML_SyntaxError("Expected Attribute " + attributeName + " in Element " + elem.getName());
        }
        try {
            return attribute.getIntValue();
        } catch (DataConversionException e) {
            throw new XML_SyntaxError("Expected Integer Value for Attribute " + attributeName
                    + " in Element " + elem.getName());
        }
    }

    static public double getDoubleAttribute(Element elem, String attributeName) throws XML_SyntaxError {
        Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XML_SyntaxError("Expected Attribute " + attributeName + " in Element " + elem.getName());
        }
        try {
            return attribute.getDoubleValue();
        } catch (DataConversionException e) {
            throw new XML_SyntaxError("Expected Double Value for Attribute " + attributeName
                    + " in Element " + elem.getName());
        }
    }


}

