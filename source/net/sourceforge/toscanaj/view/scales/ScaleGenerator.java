/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;

public interface ScaleGenerator {
    String getScaleName();
    boolean canHandleColumns(TableColumnPair[] columns);
    Diagram2D generateScale(TableColumnPair[] columns);
    Diagram2D generateScale(Diagram2D oldVersion);
}
