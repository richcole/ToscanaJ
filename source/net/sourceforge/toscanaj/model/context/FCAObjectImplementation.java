/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.context;

import org.jdom.Element;

/**
 * @todo consider using a cache to reuse existing FCAObjects, i.e. don't have two FCAObjects with the same data (and description).
 * Should descriptions be functionally dependend on the data? How to model this in CSX?
 */
public class FCAObjectImplementation implements WritableFCAObject {
	private Object data;
	private Element description;

	public FCAObjectImplementation(Object data) {
		this(data,null);
	}

	public FCAObjectImplementation(Object data, Element description) {
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

	public void setData(Object data) {
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

	public int compareTo(Object o) {
		FCAObject other = (FCAObject) o;
		if(this.data instanceof Comparable) {
			Comparable comparableData = (Comparable) this.data;
			return comparableData.compareTo(other.getData());
		}
        if(this.equals(other)) { // equals should return 0
            return 0;
        } else { // otherwise return some arbitrary but fixed non-zero value (using hashCode() might hit duplicates)
		    return (int)((long)System.identityHashCode(this) - System.identityHashCode(other));
        }
	}
	
	public boolean equals(Object other) {
		if(this.getClass() != other.getClass()) {
			return false;
		}
		FCAObjectImplementation otherImp = (FCAObjectImplementation) other;
		return this.data.equals(otherImp.data);
	}
	
	public int hashCode() {
		return this.data.hashCode();
	}
}
