/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types;

import org.jdom.Element;

import net.sourceforge.toscanaj.model.manyvaluedcontext.Scale;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableAttributeType;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

public abstract class TypeImplementation implements WritableAttributeType, XMLizable {
	
	private static final String TYPE_ELEMENT_NAME = "type";
	private static final String CLASS_ATTRIBUTE_NAME = "class";
	private static final String RANGE_ELEMENT_NAME = "range";
	private static final String NAME_ATTRIBUTE_NAME = "name";
	
    protected String name;
    protected Scale scale;

    public TypeImplementation(String name) {
        this.name = name;
        this.scale = new ScaleImplementation(name);
    }
    
    public TypeImplementation(Element element) throws XMLSyntaxError {
    	readXML(element);
    }
    
    public String getName() {
        return name;
    }

    public Scale[] getScales() {
        return new Scale[]{this.scale};
    }
    
    public void setScales(Scale scale){
    	this.scale = scale;
    }
    
    public String toString(){
    	return getName();
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    protected abstract void fillRangeElement(Element rangeElement);
    protected abstract void readRangeElement(Element rangeElement) throws XMLSyntaxError;
    
	public Element toXML() {
		Element retVal = new Element(TYPE_ELEMENT_NAME);
		retVal.setAttribute(CLASS_ATTRIBUTE_NAME, this.getClass().getName());
		retVal.setAttribute(NAME_ATTRIBUTE_NAME, this.getName());
		Element rangeElement = new Element(RANGE_ELEMENT_NAME);
		fillRangeElement(rangeElement);
		retVal.addContent(rangeElement);
		return retVal;
	}
	
	public void readXML(Element element) throws XMLSyntaxError {
		this.name = XMLHelper.getAttribute(element, NAME_ATTRIBUTE_NAME).getValue();
		Element rangeElement = element.getChild(RANGE_ELEMENT_NAME);
		readRangeElement(rangeElement);
	}
}
