/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import java.util.List;
import java.util.Iterator;

import util.CollectionFactory;
import util.NullIterator;
import org.jdom.Element;

public class DummyConcept extends AbstractConceptImplementation {
    public DummyConcept(List attributeContigent) {
        this.attributeContigent = attributeContigent;
    }

    public DummyConcept() {
        this(CollectionFactory.createDefaultList());
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    }

    List attributeContigent;

    public int getAttributeContingentSize() {
        return attributeContigent.size();
    }

    public int getObjectContingentSize() {
        return 0;
    }

    public Iterator getAttributeContingentIterator() {
        return attributeContigent.iterator();
    }

    public List executeQuery(Query query, boolean contingentOnly) {
        return CollectionFactory.createDefaultList();
    }

    public Iterator getObjectContingentIterator() {
        return NullIterator.makeNull();
    }

    public Concept filterByExtent(Concept other) {
        return this;
    }

    public Concept filterByContingent(Concept other) {
        return this;
    }

    public Concept getCollapsedConcept() {
        return this;
    }
}
