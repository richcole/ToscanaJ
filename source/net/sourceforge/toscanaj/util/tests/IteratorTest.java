/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.tests;
 
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import java.util.List;
import java.util.LinkedList;
import net.sourceforge.toscanaj.util.STD_Iterator;
import net.sourceforge.toscanaj.util.Iterator;
 
public class IteratorTest extends TestCase {
    public IteratorTest(String s) {
        super(s);
    }
 
    public void testIterateOverList() throws Exception {
	List list = new LinkedList();
	int  i, j;
	int  LEN = 100, POS=50;

	for(i=0;i<LEN;i++) {
	    list.add(list.size(), new Integer(i));
	}

	Iterator it = new STD_Iterator(list);
	i=0;
	while( ! it.atEnd() ) {
	    assertEquals(it.val(), new Integer(i));
	    it.next();
	    ++i;
	}
	assertTrue(it.atEnd());
	assertTrue(i == LEN);

	it.reset();
	i=0;
	while( ! it.atEnd() ) {
	    assertTrue(i < LEN);
	    assertEquals(it.val(), new Integer(i));
	    it.next();
	    ++i;
	}
	assertTrue(it.atEnd());
	assertTrue(i == LEN);

	assertEquals(((Iterator)it.clone()).count(), LEN);
	assertEquals(it.count(), LEN);

	it.reset();
	it.nextGTE(new Integer(POS));
	assertEquals((Integer)it.val(), new Integer(POS));

	it.nextGTE(new Integer(20));
	assertEquals((Integer)it.val(), new Integer(POS));

	it.nextGTE(new Integer(200));
	assertTrue(it.atEnd());
    }

    public static Test suite() { 
	TestSuite suite= new TestSuite(); 
	suite.addTest(new IteratorTest("testIterateOverList")); 
	return suite;
    }
}



