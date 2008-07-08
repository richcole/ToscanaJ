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

    static public Element insertElement(final String name, final Element parent) {
        final Element elem = new Element(name);
        parent.addContent(elem);
        return elem;
    }

    static public void addOptionalAttribute(final Element target,
            final String attributeName, final String attributeValue) {
        if (attributeName != null && attributeValue != null) {
            target.setAttribute(attributeName, attributeValue);
        }
    }

    static public Element getMandatoryChild(final Element element,
            final String name) throws XMLSyntaxError {
        final Element child = element.getChild(name);
        if (child == null) {
            final String reason = "Expected element '" + name + "' in '"
                    + element.getName() + "'";
            throw new XMLSyntaxError(reason);
        }
        return child;
    }

    static public boolean contains(final Element parent, final String name) {
        return (null != parent.getChild(name));
    }

    static public void checkName(final Element element, final String name)
            throws XMLSyntaxError {
        if (!element.getName().equals(name)) {
            throw new XMLSyntaxError("Expected element <" + name + ">. Found <"
                    + element.getName() + "> instead.");
        }

    }

    static public Attribute getAttribute(final Element elem,
            final String attributeName) throws XMLSyntaxError {
        final Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XMLSyntaxError("Expected attribute " + attributeName
                    + " in element " + elem.getName());
        }
        return attribute;
    }

    static public int getIntAttribute(final Element elem,
            final String attributeName) throws XMLSyntaxError {
        final Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XMLSyntaxError("Expected attribute " + attributeName
                    + " in element " + elem.getName());
        }
        try {
            return attribute.getIntValue();
        } catch (final DataConversionException e) {
            throw new XMLSyntaxError("Expected integer value for attribute "
                    + attributeName + " in element " + elem.getName());
        }
    }

    static public double getDoubleAttribute(final Element elem,
            final String attributeName) throws XMLSyntaxError {
        final Attribute attribute = elem.getAttribute(attributeName);
        if (attribute == null) {
            throw new XMLSyntaxError("Expected attribute " + attributeName
                    + " in element " + elem.getName());
        }
        try {
            return attribute.getDoubleValue();
        } catch (final DataConversionException e) {
            throw new XMLSyntaxError("Expected double value for attribute "
                    + attributeName + " in element " + elem.getName());
        }
    }

}
