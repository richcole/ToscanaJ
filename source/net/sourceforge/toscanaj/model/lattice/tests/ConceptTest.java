/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.TestCase;
import net.sourceforge.toscanaj.model.ObjectListQuery;
import net.sourceforge.toscanaj.model.ObjectNumberQuery;
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.List;

public abstract class ConceptTest extends TestCase {
    public ConceptTest(String s) {
        super(s);
    }

    public void testObjectNumberQueryOnConceptWithEmptyExtentAndContigent() {
        Concept concept = makeConceptWithEmptyContingentAndExtent();
        Query query = new ObjectNumberQuery("not important");
        List result = concept.executeQuery(query, false);
        assertEquals(true, result.isEmpty());

        result = concept.executeQuery(query, true);
        assertEquals(true, result.isEmpty());
    }

    public void testObjectListQueryOnConceptWithEmptyExtentAndContigent() {
        Concept concept = makeConceptWithEmptyContingentAndExtent();
        Query query = new ObjectListQuery("not important");
        List result = concept.executeQuery(query, false);
        assertEquals(true, result.isEmpty());
        result = concept.executeQuery(query, true);
        assertEquals(true, result.isEmpty());
    }

    protected abstract Concept makeConceptWithEmptyContingentAndExtent();
}
