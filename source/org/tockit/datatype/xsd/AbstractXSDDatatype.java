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
import org.tockit.datatype.DatatypeFactory;


public abstract class AbstractXSDDatatype extends AbstractDatatype {
    protected static final Namespace XSD_NAMESPACE = 
        Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema");
    
    private static final String XSD_ELEMENT_RESTRICTION = "restriction";
    private static final String XSD_ELEMENT_SIMPLE_TYPE = "simpleType";
    
    public static void registerTypeCreators() {
        DatatypeFactory.registerTypeCreator(StringType.getTypeCreator());
        DatatypeFactory.registerTypeCreator(DecimalType.getTypeCreator());
    }

    protected abstract static class TypeCreator implements DatatypeFactory.TypeCreator {
        private String baseType;
        
        public TypeCreator(String baseType) {
            this.baseType = baseType;
        }
        
        public boolean accepts(Element element) {
            Element restElem = getRestrictionElement(element);
            if(restElem == null) {
                return false;
            }
            return restElem.getAttributeValue("base", XSD_NAMESPACE).equals(this.baseType);
        }
        
        protected Element getRestrictionElement(Element element) {
            return element.getChild(XSD_ELEMENT_RESTRICTION, XSD_NAMESPACE);
        }
        
        protected String getTypeName(Element element) {
            return element.getAttributeValue("name");
        }
    }

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
