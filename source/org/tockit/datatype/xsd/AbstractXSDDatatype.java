/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import org.jdom.Element;
import org.jdom.Namespace;
import org.tockit.datatype.AbstractDatatype;


abstract class AbstractXSDDatatype extends AbstractDatatype {
    private static final Namespace XSD_NAMESPACE = 
        Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema");
    
    private static final String XSD_ELEMENT_RESTRICTION = "restriction";
    private static final String XSD_ELEMENT_SIMPLE_TYPE = "simpleType";

    protected AbstractXSDDatatype(String name) {
        super(name);
    }
    
    public Element toXML() {
        Element retVal = createElement(XSD_ELEMENT_SIMPLE_TYPE);
        retVal.setAttribute("name", getName());
        Element restElem = createElement(XSD_ELEMENT_RESTRICTION);
        restElem.setAttribute("base", getBaseType());
        addRestrictions(restElem);
        retVal.addContent(restElem);
        return retVal;
    }

    protected abstract String getBaseType();

    protected abstract void addRestrictions(Element restrictionElement);
    
    protected Element createElement(String tagName) {
        return new Element(tagName, XSD_NAMESPACE);
    }
}
