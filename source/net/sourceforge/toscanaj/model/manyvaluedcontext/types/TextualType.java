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


public class TextualType extends TypeImplementation {
	private List valueList ;
	private static final String VALUE_ELEMENT_NAME = null;
	
    public TextualType(String name) {
        super(name);
        valueList = new ArrayList();
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
		List valueListElements = rangeElement.getChildren();
		for (Iterator iter = valueListElements.iterator(); iter.hasNext();) {
			Element valueElement = (Element) iter.next();
			valueList.add(valueElement.getText());
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType#toValue(org.jdom.Element)
	 */
	public AttributeValue toValue(Element element) {
		// TODO Auto-generated method stub
		return null;
	}
}
