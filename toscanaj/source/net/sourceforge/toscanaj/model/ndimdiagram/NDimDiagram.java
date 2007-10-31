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
	/**
	 * @todo this could be an array
	 */
	private Vector<Point2D> base;
	
    public NDimDiagram(Vector<Point2D> base) {
        this.base = base;
    }

    @SuppressWarnings("unchecked")
	public NDimDiagram(Element element) throws XMLSyntaxError {
        this.base = new Vector<Point2D>();
        Element baseElem = element.getChild("projectionBase");
        Iterator<Element> it = baseElem.getChildren("vector").iterator();
        while (it.hasNext()) {
            Element vecElem = it.next();
            double x = Double.parseDouble(vecElem.getAttributeValue("x"));
            double y = Double.parseDouble(vecElem.getAttributeValue("y"));
            this.base.add(new Point2D.Double(x,y));
        }
        super.readXML(element); // do this last, since the ndim nodes rely on the initialized base
    }
    
    @Override
	public Element toXML() {
    	Element retVal = super.toXML();
    	Element baseElem = new Element("projectionBase");
    	Iterator<Point2D> it = this.base.iterator();
    	while (it.hasNext()) {
            Point2D baseVec = it.next();
            Element vecElem = new Element("vector");
    	    vecElem.setAttribute("x", String.valueOf(baseVec.getX()));
    	    vecElem.setAttribute("y", String.valueOf(baseVec.getY()));
    	    baseElem.addContent(vecElem);
        }
        retVal.addContent(baseElem);
    	return retVal;
    }

    @Override
	protected DiagramNode createNewDiagramNode(Element diagramNode) throws XMLSyntaxError {
        return new NDimDiagramNode(this, diagramNode);
    }

	public Vector<Point2D> getBase() {
		return base;
	}

	public void setBase(Vector<Point2D> base) {
		this.base = base;
	}
}
