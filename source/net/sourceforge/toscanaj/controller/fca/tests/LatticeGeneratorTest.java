/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.tests;

import java.util.Arrays;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.model.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.Lattice;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LatticeGeneratorTest extends TestCase {
    final static Class THIS = LatticeGeneratorTest.class;

    protected static final String CONTEXT_TITLE = "animals";
    protected static final Attribute[] ATTRIBUTES = new Attribute[]{
        new Attribute("small"),
        new Attribute("medium"),
        new Attribute("big"),
        new Attribute("twolegs"),
        new Attribute("fourlegs"),
        new Attribute("feathers"),
        new Attribute("hair"),
        new Attribute("fly"),
        new Attribute("hunt"),
        new Attribute("run"),
        new Attribute("swim"),
        new Attribute("mane"),
        new Attribute("hooves")
    };
    protected static final String[] OBJECTS = new String[]{
        "dove", 
        "hen",
        "duck",
        "goose",
        "owl", // 4
        "hawk",
        "eagle",
        "fox",
        "dog",
        "wolf", // 9
        "cat",
        "tiger",
        "lion",
        "horse",
        "zebra",
        "cow"
    };

    protected ContextImplementation context;

    public LatticeGeneratorTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }
    
    public void testGantersAlgorithmOnValidContext() {
        LatticeGenerator lgen = new GantersAlgorithm();

        Lattice lattice = lgen.createLattice(this.context);

        checkLatticeSizes(lattice);
    }

    protected void checkLatticeSizes(Lattice lattice) {
        assertEquals(31, lattice.getConcepts().length);
        
        assertEquals(16, lattice.getTop().getExtentSize());
        assertEquals(0, lattice.getTop().getIntentSize());
        assertEquals(0, lattice.getTop().getObjectContingentSize());
        assertEquals(0, lattice.getTop().getAttributeContingentSize());
        
        assertEquals(0, lattice.getBottom().getExtentSize());
        assertEquals(13, lattice.getBottom().getIntentSize());
        assertEquals(0, lattice.getBottom().getObjectContingentSize());
        assertEquals(0, lattice.getBottom().getAttributeContingentSize());
    }

    protected void setUp() {
    	ContextImplementation cont = new ContextImplementation();

        // taken from Bastian Wormuth's example
    	cont.setName(CONTEXT_TITLE); 
        cont.getObjects().addAll(Arrays.asList(OBJECTS));
        cont.getAttributes().addAll(Arrays.asList(ATTRIBUTES));

		BinaryRelationImplementation relation = cont.getRelationImplementation();
		
        relation.insert(OBJECTS[0], ATTRIBUTES[0]);
        relation.insert(OBJECTS[0], ATTRIBUTES[3]);
        relation.insert(OBJECTS[0], ATTRIBUTES[5]);
        relation.insert(OBJECTS[0], ATTRIBUTES[7]);

        relation.insert(OBJECTS[1], ATTRIBUTES[0]);
        relation.insert(OBJECTS[1], ATTRIBUTES[3]);
        relation.insert(OBJECTS[1], ATTRIBUTES[5]);

        relation.insert(OBJECTS[2], ATTRIBUTES[0]);
        relation.insert(OBJECTS[2], ATTRIBUTES[3]);
        relation.insert(OBJECTS[2], ATTRIBUTES[5]);
        relation.insert(OBJECTS[2], ATTRIBUTES[7]);
        relation.insert(OBJECTS[2], ATTRIBUTES[10]);

        relation.insert(OBJECTS[3], ATTRIBUTES[0]);
        relation.insert(OBJECTS[3], ATTRIBUTES[3]);
        relation.insert(OBJECTS[3], ATTRIBUTES[5]);
        relation.insert(OBJECTS[3], ATTRIBUTES[7]);
        relation.insert(OBJECTS[3], ATTRIBUTES[10]);

        relation.insert(OBJECTS[4], ATTRIBUTES[0]);
        relation.insert(OBJECTS[4], ATTRIBUTES[3]);
        relation.insert(OBJECTS[4], ATTRIBUTES[5]);
        relation.insert(OBJECTS[4], ATTRIBUTES[7]);
        relation.insert(OBJECTS[4], ATTRIBUTES[8]);

        relation.insert(OBJECTS[5], ATTRIBUTES[0]);
        relation.insert(OBJECTS[5], ATTRIBUTES[3]);
        relation.insert(OBJECTS[5], ATTRIBUTES[5]);
        relation.insert(OBJECTS[5], ATTRIBUTES[7]);
        relation.insert(OBJECTS[5], ATTRIBUTES[8]);

        relation.insert(OBJECTS[6], ATTRIBUTES[1]);
        relation.insert(OBJECTS[6], ATTRIBUTES[3]);
        relation.insert(OBJECTS[6], ATTRIBUTES[5]);
        relation.insert(OBJECTS[6], ATTRIBUTES[7]);
        relation.insert(OBJECTS[6], ATTRIBUTES[8]);

        relation.insert(OBJECTS[7], ATTRIBUTES[1]);
        relation.insert(OBJECTS[7], ATTRIBUTES[4]);
        relation.insert(OBJECTS[7], ATTRIBUTES[6]);
        relation.insert(OBJECTS[7], ATTRIBUTES[8]);
        relation.insert(OBJECTS[7], ATTRIBUTES[9]);

        relation.insert(OBJECTS[8], ATTRIBUTES[1]);
        relation.insert(OBJECTS[8], ATTRIBUTES[4]);
        relation.insert(OBJECTS[8], ATTRIBUTES[6]);
        relation.insert(OBJECTS[8], ATTRIBUTES[9]);

        relation.insert(OBJECTS[9], ATTRIBUTES[1]);
        relation.insert(OBJECTS[9], ATTRIBUTES[4]);
        relation.insert(OBJECTS[9], ATTRIBUTES[6]);
        relation.insert(OBJECTS[9], ATTRIBUTES[8]);
        relation.insert(OBJECTS[9], ATTRIBUTES[9]);
        relation.insert(OBJECTS[9], ATTRIBUTES[11]);

        relation.insert(OBJECTS[10], ATTRIBUTES[0]);
        relation.insert(OBJECTS[10], ATTRIBUTES[4]);
        relation.insert(OBJECTS[10], ATTRIBUTES[6]);
        relation.insert(OBJECTS[10], ATTRIBUTES[8]);
        relation.insert(OBJECTS[10], ATTRIBUTES[9]);

        relation.insert(OBJECTS[11], ATTRIBUTES[2]);
        relation.insert(OBJECTS[11], ATTRIBUTES[4]);
        relation.insert(OBJECTS[11], ATTRIBUTES[6]);
        relation.insert(OBJECTS[11], ATTRIBUTES[8]);
        relation.insert(OBJECTS[11], ATTRIBUTES[9]);

        relation.insert(OBJECTS[12], ATTRIBUTES[2]);
        relation.insert(OBJECTS[12], ATTRIBUTES[4]);
        relation.insert(OBJECTS[12], ATTRIBUTES[6]);
        relation.insert(OBJECTS[12], ATTRIBUTES[8]);
        relation.insert(OBJECTS[12], ATTRIBUTES[9]);
        relation.insert(OBJECTS[12], ATTRIBUTES[11]);

        relation.insert(OBJECTS[13], ATTRIBUTES[2]);
        relation.insert(OBJECTS[13], ATTRIBUTES[4]);
        relation.insert(OBJECTS[13], ATTRIBUTES[6]);
        relation.insert(OBJECTS[13], ATTRIBUTES[9]);
        relation.insert(OBJECTS[13], ATTRIBUTES[11]);
        relation.insert(OBJECTS[13], ATTRIBUTES[12]);

        relation.insert(OBJECTS[14], ATTRIBUTES[2]);
        relation.insert(OBJECTS[14], ATTRIBUTES[4]);
        relation.insert(OBJECTS[14], ATTRIBUTES[6]);
        relation.insert(OBJECTS[14], ATTRIBUTES[9]);
        relation.insert(OBJECTS[14], ATTRIBUTES[11]);
        relation.insert(OBJECTS[14], ATTRIBUTES[12]);

        relation.insert(OBJECTS[15], ATTRIBUTES[2]);
        relation.insert(OBJECTS[15], ATTRIBUTES[4]);
        relation.insert(OBJECTS[15], ATTRIBUTES[6]);
        relation.insert(OBJECTS[15], ATTRIBUTES[12]);

        this.context = cont;
    }
}
