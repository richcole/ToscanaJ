/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.TestCase;
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.List;

public abstract class ConceptTest extends TestCase {
    private DatabaseInfo dbInfo = new DatabaseInfo();

    public ConceptTest(String s) {
        super(s);
    }

    public void testObjectNumberQueryOnConceptWithEmptyExtentAndContigent() {
        Concept concept = makeConceptWithEmptyContingentAndExtent();
        DatabaseInfo.DatabaseQuery query = dbInfo.createAggregateQuery("Number of Objects", "");
        query.insertQueryColumn("Count", "0", null, "count(*)");
        List result = concept.executeQuery(query, false);
        assertEquals(true, result.isEmpty());

        result = concept.executeQuery(query, true);
        assertEquals(true, result.isEmpty());
    }

    public void testObjectListQueryOnConceptWithEmptyExtentAndContigent() {
        Concept concept = makeConceptWithEmptyContingentAndExtent();
        DatabaseInfo.DatabaseQuery query = dbInfo.createListQuery("List of Objects", "", false);
        query.insertQueryColumn("Object Name", null, null, "unknown");
        List result = concept.executeQuery(query, false);
        assertEquals(true, result.isEmpty());
        result = concept.executeQuery(query, true);
        assertEquals(true, result.isEmpty());
    }

    protected abstract Concept makeConceptWithEmptyContingentAndExtent();
}
