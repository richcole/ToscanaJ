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

public class TextualValueGroup implements ValueGroup {
    private String name;
    private List values = new ArrayList();

    public TextualValueGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addValue(Value value) {
        if(value instanceof TextualValue) {
            values.add(value);
        }
        throw new RuntimeException("Wrong value type for textual value group");
    }

    public boolean containsValue(Value value) {
        return values.contains(value);
    }
}
