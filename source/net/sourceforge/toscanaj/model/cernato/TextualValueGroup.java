/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TextualValueGroup implements ValueGroup {
    private TextualType type;
    private String name;
    private List values = new ArrayList();

    public TextualValueGroup(TextualType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addValue(Value value) {
        if(value instanceof TextualValue) {
            TextualValue textVal = (TextualValue) value;
            values.add(textVal.getDisplayString());
            return;
        }
        throw new RuntimeException("Wrong value type for textual value group");
    }

    public boolean containsValue(Value value) {
        if(value instanceof TextualValue) {
            TextualValue textVal = (TextualValue) value;
            return values.contains(textVal.getDisplayString());
        }
        return false;
    }

    public boolean isSuperSetOf(ValueGroup otherGroup) {
        if(!(otherGroup instanceof TextualValueGroup)) {
            return false;
        }
        TextualValueGroup otherTVGroup = (TextualValueGroup) otherGroup;
        if(otherTVGroup.type != type) {
            return false;
        }
        for (Iterator iterator = otherTVGroup.values.iterator(); iterator.hasNext();) {
            String value = (String) iterator.next();
            if(!this.values.contains(value)) {
                return false;
            }
        }
        return true;
    }
}