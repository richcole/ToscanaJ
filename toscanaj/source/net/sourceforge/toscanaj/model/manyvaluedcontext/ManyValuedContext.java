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
import org.tockit.util.ListSet;

public interface ManyValuedContext {
    ListSet<FCAElement> getObjects();

    ListSet<ManyValuedAttribute> getAttributes();

    ListSet<Datatype> getTypes();

    Value getRelationship(FCAElement object, ManyValuedAttribute attribute);
}
