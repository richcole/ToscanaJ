/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

public class NumericalType extends TypeImplementation {
    public NumericalType(String name) {
        super(name);
    }

    public void addValueGroup(ValueGroup group, String id) {
        if(group instanceof NumericalValueGroup) {
            valueGroups.put(id, group);
            return;
        }
        throw new RuntimeException("Wrong value group type");
    }
}