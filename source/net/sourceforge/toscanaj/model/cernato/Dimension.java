/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.Vector;

public class Dimension {
    private Vector path;

    public Dimension(Vector path) {
        this.path = path;
    }

    public Vector getPath() {
        return path;
    }
}
