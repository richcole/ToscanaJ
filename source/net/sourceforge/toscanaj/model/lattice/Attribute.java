/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import org.jdom.Element;

public class Attribute {
    private Object data;
    private Element description;

	public Attribute(Object data) {
		this(data,null);
	}

	public Attribute(Object data, Element description) {
		this.data = data;
		this.description = description;
	}

    public Object getData() {
        return this.data;
    }

    public Element getDescription() {
        return this.description;
    }

    public String toString() {
        return this.data.toString();
    }

	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @todo in ConceptualSchema.setDescription(Element) we clone the parameter,
	 * here we don't --> check why
	 * @todo notify schema that something has changed
	 */
	public void setDescription(Element description) {
		this.description = description;
	}
}
