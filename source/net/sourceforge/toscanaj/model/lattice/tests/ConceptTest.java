/*
 * Date: 14.04.2002
 * Time: 18:22:09
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.TestCase;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.ObjectNumberQuery;
import net.sourceforge.toscanaj.model.ObjectListQuery;

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
