/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleColumn;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WriteableScaleColumn;
import net.sourceforge.toscanaj.model.order.Ordered;

import java.util.HashSet;
import java.util.Iterator;

public class TextualValueGroup implements WriteableScaleColumn {
    private TextualType type;
    private String name;
    private HashSet values = new HashSet();

    public TextualValueGroup(TextualType type, String name, String id) {
        this.type = type;
        this.name = name;
        type.addValueGroup(this, id);
    }

    public String getName() {
        return name;
    }

    public void addValue(AttributeValue value) {
        if (value instanceof TextualValue) {
            TextualValue textVal = (TextualValue) value;
            values.add(textVal.getDisplayString());
            return;
        }
        throw new RuntimeException("Wrong value type for textual value group");
    }

    public boolean containsValue(AttributeValue value) {
        if (value instanceof TextualValue) {
            TextualValue textVal = (TextualValue) value;
            return values.contains(textVal.getDisplayString());
        }
        return false;
    }

    public boolean isSuperSetOf(ScaleColumn otherGroup) {
        if (!(otherGroup instanceof TextualValueGroup)) {
            return false;
        }
        TextualValueGroup otherTVGroup = (TextualValueGroup) otherGroup;
        if (otherTVGroup.type != type) {
            return false;
        }
        for (Iterator iterator = otherTVGroup.values.iterator(); iterator.hasNext();) {
            String value = (String) iterator.next();
            if (!this.values.contains(value)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLesserThan(Ordered other) {
        if (!(other instanceof TextualValueGroup)) {
            return false;
        }
        TextualValueGroup otherVG = (TextualValueGroup) other;
        if (otherVG.type != type) {
            return false;
        }
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            String value = (String) iterator.next();
            if (!otherVG.values.contains(value)) {
                return false;
            }
        }
        return values.size() != otherVG.values.size();
    }

    public boolean isEqual(Ordered other) {
        if (!(other instanceof TextualValueGroup)) {
            return false;
        }
        TextualValueGroup otherVG = (TextualValueGroup) other;
        return otherVG.isSuperSetOf(this) && (values.size() == otherVG.values.size());
    }
}
