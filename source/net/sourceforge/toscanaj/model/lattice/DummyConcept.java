/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jul 2, 2002
 * Time: 8:19:45 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.model.Query;

import java.util.List;
import java.util.Iterator;

import util.CollectionFactory;
import util.NullIterator;

public class DummyConcept extends AbstractConceptImplementation {
    public DummyConcept(List attributeContigent) {
        this.attributeContigent = attributeContigent;
    }

    public DummyConcept() {
        this(CollectionFactory.createDefaultList());
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
