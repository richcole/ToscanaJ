/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import concept.context.Context;
import org.jdom.Element;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

public class DefaultScale implements Scale {
    private Context context = new Context(0,0);

    public Element toXML() {
        return new Element("Scale");
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    }
}
