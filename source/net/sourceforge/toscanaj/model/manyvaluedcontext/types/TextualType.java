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

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleColumn;

public class TextualType extends TypeImplementation {
	private List valueGroupList ;
	
    public TextualType(String name) {
        super(name);
        valueGroupList = new ArrayList();
    }

    public void addValueGroup(ScaleColumn column, String id) {
        if (column instanceof TextualValueGroup) {
            scale.addColumn(column, id);
            return;
        }
        throw new RuntimeException("Wrong value group type");
    }

	public void addValue(TextualValue textualValue) {
		valueGroupList.add(textualValue);
	}
	
	public List getTextualValue(){
		return valueGroupList;
	}

	public boolean isValidValue(AttributeValue valueToTest) {
		if(!(valueToTest instanceof TextualValue)){
			return false;
		}
		Iterator valueGroupIt = valueGroupList.iterator();
		while(valueGroupIt.hasNext()){
			AttributeValue value = (AttributeValue) valueGroupIt.next();
			if(value.toString().equals(valueToTest.toString())){
				return true;
			}
		}
		return false;
	}
	public void removeValue(int selectedIndex) {
		valueGroupList.remove(selectedIndex);
	}
	public void move(int startIndex, int endIndex) {	
		TextualValue textualValue = (TextualValue) valueGroupList.get(startIndex);
		valueGroupList.remove(startIndex);
		valueGroupList.add(endIndex,textualValue);
	}

	public void replaceValue(TextualValue textualValue, int selectedIndex) {
		valueGroupList.set(selectedIndex, textualValue);
	}
	
	public boolean isDuplicatedValue(AttributeValue valueToTest){
		Iterator valueGroupIt = valueGroupList.iterator();
		while(valueGroupIt.hasNext()){
			AttributeValue value = (AttributeValue) valueGroupIt.next();
			if(value.toString().equalsIgnoreCase(valueToTest.toString())){
				return true;
			}
		}
		return false;
	}

}
