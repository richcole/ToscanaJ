/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.Vector;

import net.sourceforge.toscanaj.model.manyvaluedcontext.*;

/**
 * @todo this is pretty much a hack since the class exposes its member in a writable fashion
 */
public class CernatoModel {
    private ManyValuedContextImplementation context = new ManyValuedContextImplementation();
    private Vector types = new Vector();
    private Vector views = new Vector();

    public CernatoModel() {
    }

    public ManyValuedContextImplementation getContext() {
        return context;
    }

    public Vector getTypes() {
        return types;
    }

    public Vector getViews() {
        return views;
    }
}
