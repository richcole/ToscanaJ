/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;


/**
 * This serialization interface is used for the ElbaMainPanel model. All
 * elements of the model support this interface and in addition have a
 * constructor that callls readXML.
 */
public abstract class XMLHelper {

    static public Element insertElement(String name, Element parent) {
        Element elem = new Element(name);
        parent.addContent(elem);
        return elem;
    }

    static public void addOptionalAttribute(Element target, String attributeName, String attributeValue) {
        if (attributeName != null && attributeValue != null) {
            target.setAttribute(attributeName, attributeValue);
        }
    }

    static public Element getMandatoryChild(Element element, String name)
            throws XMLSyntaxError {
        Element child = element.getChild(name);
        if (child == null) {
            String reason = "Expected element '" +
                    name + "' in '" + element.getName() + "'";
            throw new XMLSyntaxError(reason);
        }
        return child;
    }

    static public boolean contains(Element parent, String name) {
        return (null != parent.getChild(name));
    }

    static public void checkName(Element element, String name) throws XMLSyntaxError {
        if (!element.getName().equals(name)) {
            throw new XMLSyntaxError("Expected element <" + name + ">. Found <" +
            		element.getName() + "> instead.");
        }

    }

    static public Attribute getAttribute(Element elem, String attributeName) throws XMLSyntaxError {
        Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XMLSyntaxError("Expected attribute " + attributeName + " in element " + elem.getName());
        }
        return attribute;
    }

    static public int getIntAttribute(Element elem, String attributeName) throws XMLSyntaxError {
        Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XMLSyntaxError("Expected attribute " + attributeName + " in element " + elem.getName());
        }
        try {
            return attribute.getIntValue();
        } catch (DataConversionException e) {
            throw new XMLSyntaxError("Expected integer value for attribute " + attributeName
                    + " in element " + elem.getName());
        }
    }

    static public double getDoubleAttribute(Element elem, String attributeName) throws XMLSyntaxError {
        Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XMLSyntaxError("Expected attribute " + attributeName + " in element " + elem.getName());
        }
        try {
            return attribute.getDoubleValue();
        } catch (DataConversionException e) {
            throw new XMLSyntaxError("Expected double value for attribute " + attributeName
                    + " in element " + elem.getName());
        }
    }


}
