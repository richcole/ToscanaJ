/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.scaling;

import org.tockit.events.EventBroker;
import org.tockit.tupelware.model.TupelSet;

import net.sourceforge.toscanaj.model.ConceptualSchema;


public class TupelScaling {
    public static ConceptualSchema scaleWithTupelsAsObjects(TupelSet tupels) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());
        return schema;
    }
}
