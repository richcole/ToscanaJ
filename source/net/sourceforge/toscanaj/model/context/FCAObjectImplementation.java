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
public class FCAObjectImplementation implements WritableFCAObject, XMLizable {
	private Object data;
	private Element description;
	private static final String OBJECT_ELEMENT_NAME = "object";
	private static final String DESCRIPTION_ELEMENT_NAME = "description";
	private static final String DATA_ELEMENT_NAME = "data";
	private static final String CLASS_ATTRIBUTE_NAME = "class";

	public FCAObjectImplementation(Object data) {
		this(data,null);
	}

	public FCAObjectImplementation(Object data, Element description) {
		this.data = data;
		this.description = description;
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
	
	public boolean equals(Object other) {
        if(other == null) {
            return false;
        }
		if(this.getClass() != other.getClass()) {
			return false;
		}
		FCAObjectImplementation otherImp = (FCAObjectImplementation) other;
		return this.data.equals(otherImp.data);
	}
	
	public int hashCode() {
		return this.data.hashCode();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#toXML()
	 * @todo think about base64 encoding for serializable data objects, currently
	 *       works only for String and XMLizable data objects
	 */
	public Element toXML() {
		Element retVal = new Element(OBJECT_ELEMENT_NAME);
		if (description != null) {
			Element descriptionElement = new Element(DESCRIPTION_ELEMENT_NAME);
			descriptionElement.addContent(description);
			retVal.addContent(descriptionElement);
		}
		Element dataElement = new Element(DATA_ELEMENT_NAME);
		if (data instanceof XMLizable) {
			dataElement=((XMLizable)data).toXML();
			dataElement.setAttribute(CLASS_ATTRIBUTE_NAME, data.getClass().getName());
		} else if (data != null){
			dataElement.addContent(data.toString());
			dataElement.setAttribute(CLASS_ATTRIBUTE_NAME, String.class.getName());
		}
		retVal.addContent(dataElement);
		return retVal;
	}

	public void readXML(Element elem) throws XMLSyntaxError {
		// TODO Auto-generated method stub
		description = XMLHelper.getMandatoryChild(elem, DESCRIPTION_ELEMENT_NAME);
		Element dataElement = XMLHelper.getMandatoryChild(elem, DATA_ELEMENT_NAME);
		String className = XMLHelper.getAttribute(elem, CLASS_ATTRIBUTE_NAME).getValue();
		if (className.equals(String.class.getName())) {
			data = dataElement.getTextTrim();
		} else {
			Constructor construct;
			try {
				construct = Class.forName(className).getConstructor(new Class[] {Element.class});
				data = construct.newInstance(new Object[] {dataElement});
			} catch (Exception e) {
				throw new XMLSyntaxError("Initialization of object of type " + className + "failed.", e);
			}
		}
	}
}
