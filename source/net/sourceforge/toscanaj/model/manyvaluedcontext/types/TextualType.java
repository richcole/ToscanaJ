/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleColumn;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;


public class TextualType extends TypeImplementation {
	private List valueList ;
	
    public TextualType(String name) {
        super(name);
        valueList = new ArrayList();
    }
    
    public TextualType(Element elem) throws XMLSyntaxError {
    	super(elem);
    }

    public void addValueGroup(ScaleColumn column, String id) {
        if (column instanceof TextualValueGroup) {
            scale.addColumn(column, id);
            return;
        }
        throw new RuntimeException("Wrong value group type");
    }

	public void addValue(TextualValue textualValue) {
		valueList.add(textualValue);
	}

	public boolean isValidValue(AttributeValue valueToTest) {
		if(!(valueToTest instanceof TextualValue)){
			return false;
		}
		Iterator valueListIt = valueList.iterator();
		while(valueListIt.hasNext()){
			AttributeValue value = (AttributeValue) valueListIt.next();
			if(value.toString().equals(valueToTest.toString())){
				return true;
			}
		}
		return false;
	}
	public void removeValue(int selectedIndex) {
		valueList.remove(selectedIndex);
	}
	public void move(int startIndex, int endIndex) {	
		TextualValue textualValue = (TextualValue) valueList.get(startIndex);
		valueList.remove(startIndex);
		valueList.add(endIndex,textualValue);
	}

	public void replaceValue(TextualValue textualValue, int selectedIndex) {
		valueList.set(selectedIndex, textualValue);
	}
	
	public boolean isDuplicatedValue(AttributeValue valueToTest){
		Iterator valueGroupIt = valueList.iterator();
		while(valueGroupIt.hasNext()){
			AttributeValue value = (AttributeValue) valueGroupIt.next();
			if(value.toString().equalsIgnoreCase(valueToTest.toString())){
				return true;
			}
		}
		return false;
	}
	
	public AttributeValue[] getValueRange() {
		AttributeValue[] attribute = new AttributeValue[valueList.size()];
		for(int i = 0 ; i < valueList.size() ; i++){
			attribute[i] = (AttributeValue) valueList.get(i);
		}
		return attribute;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.model.manyvaluedcontext.types.TypeImplementation#fillRangeElement(org.jdom.Element)
	 */
	protected void fillRangeElement(Element rangeElement) {
		for (Iterator iter = valueList.iterator(); iter.hasNext();) {
			TextualValue element = (TextualValue) iter.next();
			Element valueElement = new Element(VALUE_ELEMENT_NAME);
			valueElement.addContent(element.getDisplayString());
			rangeElement.addContent(valueElement);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.model.manyvaluedcontext.types.TypeImplementation#readRangeElement(org.jdom.Element)
	 */
	protected void readRangeElement(Element rangeElement) {
		this.valueList = new ArrayList();
		List valueListElements = rangeElement.getChildren();
		for (Iterator iter = valueListElements.iterator(); iter.hasNext();) {
			Element valueElement = (Element) iter.next();
			valueList.add(new TextualValue(valueElement.getText()));
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType#toValue(org.jdom.Element)
	 */
	public AttributeValue toValue(Element element) {
		return new TextualValue(element.getTextTrim());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType#toElement(net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue)
	 */
	public Element toElement(AttributeValue value) {
		Element retVal = new Element(VALUE_ELEMENT_NAME);
		retVal.addContent(((TextualValue) value).getDisplayString());
		return retVal;
	}
}
