/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import org.jdom.Element;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TypeImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;


public class ManyValuedAttributeImplementation implements WritableManyValuedAttribute, XMLizable {
    private AttributeType type;
    private String name;
	private static final String MANY_VALUED_ATTRIBUTE_ELEMENT_NAME = "manyValuedAttribute";
	private static final String NAME_ATTRIBUTE_NAME = "name";

    public ManyValuedAttributeImplementation(AttributeType type, String name) {
        this.type = type;
        this.name = name;
    }

    public AttributeType getType() {
        return type;
    }
    
    public void setType(AttributeType type){
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

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#toXML()
	 */
	public Element toXML() {
/*
*     private AttributeType type;
    private String name;

 */		// TODO Auto-generated method stub
		Element retVal = new Element(MANY_VALUED_ATTRIBUTE_ELEMENT_NAME);
		retVal.setAttribute(NAME_ATTRIBUTE_NAME, name);
		retVal.addContent(((TypeImplementation) type).toXML());
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#readXML(org.jdom.Element)
	 */
	public void readXML(Element elem) { //throws XMLSyntaxError {
		// TODO Auto-generated method stub
		
	}
}
