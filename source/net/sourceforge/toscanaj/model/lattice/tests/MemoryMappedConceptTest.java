/*
 * Date: 13.04.2002
 * Time: 22:06:38
 * To change template for new class use
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.MemoryMappedConcept;

public class MemoryMappedConceptTest extends ConceptTest {
    public MemoryMappedConceptTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(MemoryMappedConceptTest.class);
    }

    protected Concept makeConceptWithEmptyContingentAndExtent() {
        return new MemoryMappedConcept();
    }
}
