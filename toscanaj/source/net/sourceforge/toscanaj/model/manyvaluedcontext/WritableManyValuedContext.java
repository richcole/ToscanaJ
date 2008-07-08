/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import net.sourceforge.toscanaj.model.context.FCAElement;

import org.tockit.datatype.Datatype;
import org.tockit.datatype.Value;

public interface WritableManyValuedContext extends ManyValuedContext {
    void add(FCAElement object);

    void add(ManyValuedAttribute attribute);

    void add(Datatype type);

    void remove(FCAElement object);

    void remove(ManyValuedAttribute attribute);

    void remove(Datatype type);

    void setRelationship(FCAElement object, ManyValuedAttribute attribute,
            Value value);

    void update();
}
