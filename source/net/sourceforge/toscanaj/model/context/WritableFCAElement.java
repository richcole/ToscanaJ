/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */	
package net.sourceforge.toscanaj.model.context;

import org.jdom.Element;

public interface WritableFCAElement extends FCAElement {
	void setData(Object data);
    void setDescription(Element description);
    void setContextPosition(int contextPosition);
}