/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;

import java.util.NoSuchElementException;

public class DiagramHistoryTest extends TestCase {
    final static Class THIS = DiagramHistoryTest.class;
    private DiagramHistory diagramHistory;

    public DiagramHistoryTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testAddToDiagramHistory() {
        assertEquals(0, this.diagramHistory.getSize());

        addDiagram();
        assertEquals(1, this.diagramHistory.getSize());

        addDiagram();
        assertEquals(true, this.diagramHistory.hasFutureDiagrams());

        assertEquals(0, this.diagramHistory.getNestingLevel());
        this.diagramHistory.setNestingLevel(1);
        assertEquals(false, this.diagramHistory.hasFutureDiagrams());
    }

    public void testNextForActiveDiagramWithoutFutureDiagrams() {
        addDiagram();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        try {
        	this.diagramHistory.next(null);
        	fail();
        } catch(RuntimeException e) {
        	// this is what should happen
        }
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testNextForActiveDiagramWithFutureDiagrams() {
        addDiagram();
        addDiagram();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        this.diagramHistory.next(null);
        assertTrue(this.diagramHistory.isInPast(0));
        assertTrue(this.diagramHistory.isInCurrent(1));
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testNextForFirstLevelOfNesting() {
        addDiagram();
        this.diagramHistory.setNestingLevel(1);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        this.diagramHistory.next(null);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        this.diagramHistory.next(null);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testNextForFirstLevelOfNestingWithManyFutureDiagrams() {
        addDiagram();
        this.diagramHistory.setNestingLevel(1);
        addDiagram();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        addDiagram();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertTrue(this.diagramHistory.isInFuture(2));
        this.diagramHistory.next(null);
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertTrue(this.diagramHistory.isInPast(0));
        assertTrue(this.diagramHistory.isInCurrent(1));
        assertTrue(this.diagramHistory.isInCurrent(2));
        assertTrue(this.diagramHistory.isInFuture(3));
    }

    public void testNextForEmptyDiagramHistory() {
        assertEquals(0, this.diagramHistory.getSize());
        assertEquals(0, this.diagramHistory.getNumberOfCurrentDiagrams());
        try {
            this.diagramHistory.next(null);
            fail();
        } catch(RuntimeException e) {
            // this is what should happen
        }
        assertEquals(0, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    private void addDiagram() {
        Diagram2D diagram = new SimpleLineDiagram();
        this.diagramHistory.addDiagram(diagram);
    }

    private void addDiagrams(int count) {
        for (int i = count; --i >= 0;) {
            addDiagram();
        }
    }

    public void testReset() {
        addDiagrams(2);
        assertEquals(false, this.diagramHistory.isEmpty());
        assertTrue(this.diagramHistory.getNumberOfCurrentDiagrams() > 0);
        this.diagramHistory.reset();
        assertTrue(this.diagramHistory.isEmpty());
        assertEquals(0, this.diagramHistory.getSize());
        assertEquals(0, this.diagramHistory.getNumberOfCurrentDiagrams());

    }

    public void testRemoveLastDiagramWithNesting() {
        addDiagrams(2);

        this.diagramHistory.setNestingLevel(1);
        assertEquals(2, this.diagramHistory.getSize());
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());

        this.diagramHistory.next(null);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        this.diagramHistory.removeLastDiagram();
        assertEquals(1, this.diagramHistory.getSize());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testRemoveLastDiagramWithNestingWithCurrentNestedDiagram() {
        addDiagrams(2);

        this.diagramHistory.setNestingLevel(1);
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());

        this.diagramHistory.removeLastDiagram();
        assertEquals(1, this.diagramHistory.getSize());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
    }


    public void testRemoveLastDiagramWithoutNesting() {
        addDiagrams(2);
        this.diagramHistory.setNestingLevel(0);
        this.diagramHistory.next(null);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(true, this.diagramHistory.isInPast(0));
        this.diagramHistory.removeLastDiagram();
        assertEquals(1, this.diagramHistory.getSize());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());

    }

    public void testRemoveLastDiagramWhenHaveFutureDiagrams() {
        addDiagrams(2);
        this.diagramHistory.setNestingLevel(1);
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
        this.diagramHistory.addDiagram(new SimpleLineDiagram());
        assertEquals(true, this.diagramHistory.isInFuture(2));
        this.diagramHistory.removeLastDiagram();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
    }

    public void testRemoveLastDiagramWhenHistoryIsEmpty() {
        assertEquals(0, this.diagramHistory.getSize());

        try {
            this.diagramHistory.removeLastDiagram();
            fail();
        } catch (NoSuchElementException e) {
            assertTrue("Should throw exception", true);
        }
    }

    public void testGetCurrentDiagramSize() {
        assertEquals(0, this.diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        this.diagramHistory.removeLastDiagram();
        assertEquals(0, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testBackForEmptyDiagram() {
        assertEquals(0, this.diagramHistory.getSize());
        try {
            this.diagramHistory.back();
            fail("DiagramHIstory throws exception, when no diagrams are left to go back");
        } catch (NoSuchElementException e) {
            assertTrue("Assertion expected", true);
        }
    }

    public void testBackWithoutNesting() {
        addDiagrams(2);
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());

        this.diagramHistory.next(null);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(1, this.diagramHistory.getFirstCurrentDiagramPosition());

        this.diagramHistory.back();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
    }

    public void testBackWithNesting() {
        addDiagrams(3);
        this.diagramHistory.next(null);
        this.diagramHistory.setNestingLevel(1);
        this.diagramHistory.next(null);
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(2, this.diagramHistory.getFirstCurrentDiagramPosition());
        this.diagramHistory.back();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(1, this.diagramHistory.getFirstCurrentDiagramPosition());

        this.diagramHistory.back();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());

        this.diagramHistory.back();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
    }

    public void testCanMoveUp() {
        assertTrue(this.diagramHistory.isEmpty());
        assertEquals(false, this.diagramHistory.canMoveUp());
        addDiagram();
        assertEquals(false, this.diagramHistory.canMoveUp());
        addDiagram();
        this.diagramHistory.next(null);
        assertEquals(true, this.diagramHistory.canMoveUp());
        this.diagramHistory.back();
        this.diagramHistory.setNestingLevel(1);
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(false, this.diagramHistory.canMoveUp());
        this.diagramHistory.back();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());
        assertEquals(false, this.diagramHistory.canMoveUp());
        this.diagramHistory.next(null);
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, this.diagramHistory.getFirstCurrentDiagramPosition());

    }

    public void testAddDiagramSideEffects() {
        addDiagram();
        this.diagramHistory.setNestingLevel(1);
        addDiagram();
        assertEquals(2, this.diagramHistory.getNumberOfCurrentDiagrams());
        this.diagramHistory.back();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        assertEquals(1, this.diagramHistory.getNumberOfCurrentDiagrams());
    }

    protected void setUp() {
        this.diagramHistory = new DiagramHistory();
    }

}
