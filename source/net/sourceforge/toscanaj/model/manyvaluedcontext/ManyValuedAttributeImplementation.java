/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import java.util.Hashtable;

import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;
import org.tockit.datatype.Datatype;


public class ManyValuedAttributeImplementation implements WritableManyValuedAttribute {
    private Datatype type;
    private String name;
	private static final String MANY_VALUED_ATTRIBUTE_ELEMENT_NAME = "attribute";
	private static final String NAME_ATTRIBUTE_NAME = "name";
	private static final String TYPE_REF_ATTRIBUTE_NAME = "typeRef";

    public ManyValuedAttributeImplementation(Datatype type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public ManyValuedAttributeImplementation(Element element, Hashtable typeIdMapping) throws XMLSyntaxError {
    	readXML(element, typeIdMapping);
    }

    public Datatype getType() {
        return type;
    }
    
    public void setType(Datatype type){
    	this.type = type;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
    	return getName();
    }

	public void setName(String name) {
		this.name = name;
	}

	public Element toXML(Hashtable typesIdMapping) {
		Element retVal = new Element(MANY_VALUED_ATTRIBUTE_ELEMENT_NAME);
		retVal.setAttribute(NAME_ATTRIBUTE_NAME, name);
		retVal.setAttribute(TYPE_REF_ATTRIBUTE_NAME, (String) typesIdMapping.get(this.getType()));
		return retVal;
	}

	public void readXML(Element elem, Hashtable typesIdMapping) throws XMLSyntaxError { 
		String typeRef = XMLHelper.getAttribute(elem, TYPE_REF_ATTRIBUTE_NAME).getValue();
		this.type = (Datatype) typesIdMapping.get(typeRef);
		this.name = XMLHelper.getAttribute(elem, NAME_ATTRIBUTE_NAME).getValue();
	}
}
