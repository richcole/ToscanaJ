/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.TestCase;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseQuery;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.List;

/**
 * @todo these tests are rather broken, we should test with an embedded database with some objects
 * in it.
 */
public abstract class ConceptTest extends TestCase {
    private DatabaseInfo dbInfo = new DatabaseInfo();

    public ConceptTest(String s) {
        super(s);
    }

    public void testObjectNumberQueryOnConceptWithEmptyExtentAndContigent() {
        DatabaseConnectedConcept concept = makeConceptWithEmptyContingentAndExtent();
        DatabaseQuery query = dbInfo.createAggregateQuery("Number of Objects", "");
        query.insertQueryColumn("count", "0", null, "count(*)");
        List result = query.execute(concept, false);
        assertEquals(true, result.isEmpty());

        result = query.execute(concept, true);
        assertEquals(true, result.isEmpty());
    }

    public void testObjectListQueryOnConceptWithEmptyExtentAndContigent() {
        DatabaseConnectedConcept concept = makeConceptWithEmptyContingentAndExtent();
        DatabaseQuery query = dbInfo.createListQuery("List of Objects", "", false);
        query.insertQueryColumn("list", null, null, "unknown");
        List result = query.execute(concept, false);
        assertEquals(true, result.isEmpty());
        result = query.execute(concept, true);
        assertEquals(true, result.isEmpty());
    }

    protected abstract DatabaseConnectedConcept makeConceptWithEmptyContingentAndExtent();
}
