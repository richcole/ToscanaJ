/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import org.jdom.Element;

/**
 * This class is an abstraction of all diagram related information.
 */

public interface WriteableDiagram2D extends Diagram2D {

    /**
     * Change the title of the diagram.
     */
    public void setTitle(String title);
    
    /**
     * Gives the diagram a description.
     */
    public void setDescription(Element descriptionXML);

    /**
     * Adds a point to the diagram (at the end of the list).
     */
    public void addNode(DiagramNode node);

    /**
     * Adds a line to the diagram (at the end of the list).
     *
     * The from and to parameters are assumed to refer to some points already
     * existing in the points list.
     */
    public void addLine(DiagramNode from, DiagramNode to);
}
