/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;
import org.tockit.canvas.CanvasItem;


public interface ExtraCanvasItemFactory {
    public CanvasItem createCanvasItem(SimpleLineDiagram diagram, Element element) throws XMLSyntaxError ;
}
