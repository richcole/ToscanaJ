/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.ndimdiagram;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;

public class NDimDiagram extends SimpleLineDiagram {
	private Vector base;
	
    public NDimDiagram(Vector base) {
        super();
        this.base = base;
    }

    public NDimDiagram(Element element) throws XMLSyntaxError {
        super(element);
        /// @todo XML in/out
    }
    
    public Element toXML() {
    	Element retVal = super.toXML();
    	Element baseElem = new Element("projectionBase");
    	Iterator it = this.base.iterator();
    	while (it.hasNext()) {
            Point2D baseVec = (Point2D) it.next();
            Element vecElem = new Element("vector");
    	    vecElem.setAttribute("x", String.valueOf(baseVec.getX()));
    	    vecElem.setAttribute("y", String.valueOf(baseVec.getY()));
    	    baseElem.addContent(vecElem);
        }
        retVal.addContent(baseElem);
    	return retVal;
    }

    public Vector getBase() {
        return base;
    }
}
