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
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.Lattice;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LatticeGeneratorTest extends TestCase {
    final static Class THIS = LatticeGeneratorTest.class;
    
    protected Context context;

    public LatticeGeneratorTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }
    
    public void testGantersAlgorithm() {
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

    	cont.setName("animals"); // taken from Bastian Wormuth's example
    	
        String[] objects = new String[]{
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
        };  // the last one was originally two identical strings "cow", but that is of course not valid
        
        cont.getObjects().addAll(Arrays.asList(objects));
        
        Attribute[] attributes = new Attribute[]{
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
        
        cont.getAttributes().addAll(Arrays.asList(attributes));

		BinaryRelationImplementation relation = cont.getRelationImplementation();
		
        relation.insert(objects[0], attributes[0]);
        relation.insert(objects[0], attributes[3]);
        relation.insert(objects[0], attributes[5]);
        relation.insert(objects[0], attributes[7]);

        relation.insert(objects[1], attributes[0]);
        relation.insert(objects[1], attributes[3]);
        relation.insert(objects[1], attributes[5]);

        relation.insert(objects[2], attributes[0]);
        relation.insert(objects[2], attributes[3]);
        relation.insert(objects[2], attributes[5]);
        relation.insert(objects[2], attributes[7]);
        relation.insert(objects[2], attributes[10]);

        relation.insert(objects[3], attributes[0]);
        relation.insert(objects[3], attributes[3]);
        relation.insert(objects[3], attributes[5]);
        relation.insert(objects[3], attributes[7]);
        relation.insert(objects[3], attributes[10]);

        relation.insert(objects[4], attributes[0]);
        relation.insert(objects[4], attributes[3]);
        relation.insert(objects[4], attributes[5]);
        relation.insert(objects[4], attributes[7]);
        relation.insert(objects[4], attributes[8]);

        relation.insert(objects[5], attributes[0]);
        relation.insert(objects[5], attributes[3]);
        relation.insert(objects[5], attributes[5]);
        relation.insert(objects[5], attributes[7]);
        relation.insert(objects[5], attributes[8]);

        relation.insert(objects[6], attributes[1]);
        relation.insert(objects[6], attributes[3]);
        relation.insert(objects[6], attributes[5]);
        relation.insert(objects[6], attributes[7]);
        relation.insert(objects[6], attributes[8]);

        relation.insert(objects[7], attributes[1]);
        relation.insert(objects[7], attributes[4]);
        relation.insert(objects[7], attributes[6]);
        relation.insert(objects[7], attributes[8]);
        relation.insert(objects[7], attributes[9]);

        relation.insert(objects[8], attributes[1]);
        relation.insert(objects[8], attributes[4]);
        relation.insert(objects[8], attributes[6]);
        relation.insert(objects[8], attributes[9]);

        relation.insert(objects[9], attributes[1]);
        relation.insert(objects[9], attributes[4]);
        relation.insert(objects[9], attributes[6]);
        relation.insert(objects[9], attributes[8]);
        relation.insert(objects[9], attributes[9]);
        relation.insert(objects[9], attributes[11]);

        relation.insert(objects[10], attributes[0]);
        relation.insert(objects[10], attributes[4]);
        relation.insert(objects[10], attributes[6]);
        relation.insert(objects[10], attributes[8]);
        relation.insert(objects[10], attributes[9]);

        relation.insert(objects[11], attributes[2]);
        relation.insert(objects[11], attributes[4]);
        relation.insert(objects[11], attributes[6]);
        relation.insert(objects[11], attributes[8]);
        relation.insert(objects[11], attributes[9]);

        relation.insert(objects[12], attributes[2]);
        relation.insert(objects[12], attributes[4]);
        relation.insert(objects[12], attributes[6]);
        relation.insert(objects[12], attributes[8]);
        relation.insert(objects[12], attributes[9]);
        relation.insert(objects[12], attributes[11]);

        relation.insert(objects[13], attributes[2]);
        relation.insert(objects[13], attributes[4]);
        relation.insert(objects[13], attributes[6]);
        relation.insert(objects[13], attributes[9]);
        relation.insert(objects[13], attributes[11]);
        relation.insert(objects[13], attributes[12]);

        relation.insert(objects[14], attributes[2]);
        relation.insert(objects[14], attributes[4]);
        relation.insert(objects[14], attributes[6]);
        relation.insert(objects[14], attributes[9]);
        relation.insert(objects[14], attributes[11]);
        relation.insert(objects[14], attributes[12]);

        relation.insert(objects[15], attributes[2]);
        relation.insert(objects[15], attributes[4]);
        relation.insert(objects[15], attributes[6]);
        relation.insert(objects[15], attributes[12]);

        this.context = cont;
    }
}
