/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;


import java.util.ArrayList;
import java.util.List;

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
	
	
}
