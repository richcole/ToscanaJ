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

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;

public class NDimDiagram extends SimpleLineDiagram {
	private Vector base;
	
    public NDimDiagram(Vector base) {
        this.base = base;
    }

    public NDimDiagram(Element element) throws XMLSyntaxError {
        this.base = new Vector();
        Element baseElem = element.getChild("projectionBase");
        Iterator it = baseElem.getChildren("vector").iterator();
        while (it.hasNext()) {
            Element vecElem = (Element) it.next();
            double x = Double.parseDouble(vecElem.getAttributeValue("x"));
            double y = Double.parseDouble(vecElem.getAttributeValue("y"));
            this.base.add(new Point2D.Double(x,y));
        }
        super.readXML(element); // do this last, since the ndim nodes rely on the initialized base
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

    protected DiagramNode createNewDiagramNode(Element diagramNode) throws XMLSyntaxError {
        return new NDimDiagramNode(this, diagramNode);
    }

    public Vector getBase() {
        return base;
    }
}
