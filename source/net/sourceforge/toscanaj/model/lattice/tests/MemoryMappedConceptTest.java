/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
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
