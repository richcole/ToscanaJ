/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.gradients;

import java.awt.Color;

public interface Gradient {
    /**
     * Returns the color at a certain position.
     * 
     * @param position
     *            value in [0,1] giving the position within the gradient
     * @return the color at the position
     * @throws IllegalArgumentException
     *             iff paramter is out of range
     */
    Color getColor(double position);
}
