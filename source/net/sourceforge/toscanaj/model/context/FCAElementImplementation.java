/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.context;

import java.lang.reflect.Constructor;

import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;

/**
 * @todo consider using a cache to reuse existing FCAObjects, i.e. don't have two FCAObjects with the same data (and description).
 * Should descriptions be functionally dependend on the data? How to model this in CSX?
 */
public class FCAElementImplementation implements WritableFCAElement, XMLizable, Comparable {
	private Object data;
	private Element description;
    private int contextPosition = -1; // -1 means "not set"
	private static final String OBJECT_ELEMENT_NAME = "object";
	private static final String DESCRIPTION_ELEMENT_NAME = "description";
	private static final String DATA_ELEMENT_NAME = "data";
	private static final String CLASS_ATTRIBUTE_NAME = "class";
    private static final String CONTEXT_POSITION_ATTRIBUTE_NAME = "contextPosition";

	public FCAElementImplementation(Object data) {
		this(data,null);
	}

	public FCAElementImplementation(Object data, Element description) {
		this.data = data;
		this.description = description;
	}
	
	public FCAElementImplementation(Element xmlelement) throws XMLSyntaxError {
		readXML(xmlelement);
	}

	public Object getData() {
		return this.data;
	}

	public Element getDescription() {
		return this.description;
	}

	public String toString() {
		return this.data.toString();
	}

	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @todo in ConceptualSchema.setDescription(Element) we clone the parameter,
	 * here we don't --> check why
	 * @todo notify schema that something has changed
	 */
	public void setDescription(Element description) {
		this.description = description;
	}
	
    public int getContextPosition() {
        return this.contextPosition;
    }
    
    public void setContextPosition(int contextPosition) {
        this.contextPosition = contextPosition;
    }
    
	public boolean equals(Object other) {
        if(other == null) {
            return false;
        }
		if(this.getClass() != other.getClass()) {
			return false;
		}
		FCAElementImplementation otherImp = (FCAElementImplementation) other;
		return this.data.equals(otherImp.data);
	}
	
	public int hashCode() {
		return this.data.hashCode();
	}

	/**
	 * @todo think about base64 encoding for serializable data objects, currently
	 *       works only for String and XMLizable data objects
	 */
	public Element toXML() {
		Element retVal = new Element(OBJECT_ELEMENT_NAME);
		if (description != null) {
			Element descriptionElement = new Element(DESCRIPTION_ELEMENT_NAME);
			descriptionElement.addContent((Element)description.clone());
			retVal.addContent(descriptionElement);
		}
        /** @todo this is how it should be in 2.0
		Element dataElement = new Element(DATA_ELEMENT_NAME);
		if (data instanceof XMLizable) {
			dataElement.addContent(((XMLizable)data).toXML());
			dataElement.setAttribute(CLASS_ATTRIBUTE_NAME, data.getClass().getName());
		} else if (data != null){
			dataElement.addContent(data.toString());
			dataElement.setAttribute(CLASS_ATTRIBUTE_NAME, String.class.getName());
		}
        retVal.addContent(dataElement);
        **/
        // this is how we do it for now
        retVal.addContent(data.toString());
        if(this.contextPosition != -1) {
            retVal.setAttribute(CONTEXT_POSITION_ATTRIBUTE_NAME, String.valueOf(this.contextPosition));
        }
		return retVal;
	}

	public void readXML(Element elem) throws XMLSyntaxError {
		this.description = (Element) elem.getChild(DESCRIPTION_ELEMENT_NAME).getChildren().get(0);
        String contextPositionAttribute = elem.getAttributeValue(CONTEXT_POSITION_ATTRIBUTE_NAME);
        if(contextPositionAttribute != null) {
            this.contextPosition = Integer.parseInt(contextPositionAttribute);
        }
        // now check for old-style syntax and parse that instead if found
        if(elem.getChild(DATA_ELEMENT_NAME) == null) {
            this.data = elem.getTextTrim();
            return;
        }
		Element dataElement = XMLHelper.getMandatoryChild(elem, DATA_ELEMENT_NAME);
		String className = XMLHelper.getAttribute(dataElement, CLASS_ATTRIBUTE_NAME).getValue();
		if (className.equals(String.class.getName())) {
			this.data = dataElement.getTextTrim();
		} else {
			try {
				Constructor construct = Class.forName(className).getConstructor(new Class[] {Element.class});
				this.data = construct.newInstance(new Object[] {dataElement});
			} catch (Exception e) {
				throw new XMLSyntaxError("Initialization of object of type " + className + "failed.", e);
			}
		}
	}

    /**
     * Determines order based on the context position stored.
     * 
     * If the context position has not been set on the objects, all objects
     * will be considered equal. Objects with a context position are always
     * considered greater than those without.
     */
    public int compareTo(Object o) {
        return this.contextPosition - ((FCAElementImplementation)o).contextPosition;
    }
}
