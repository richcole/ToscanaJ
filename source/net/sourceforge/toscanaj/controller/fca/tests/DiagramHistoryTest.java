/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
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
        assertEquals(0, diagramHistory.getSize());

        addDiagram();
        assertEquals(1, diagramHistory.getSize());

        addDiagram();
        assertEquals(true, diagramHistory.hasFutureDiagrams());

        assertEquals(0, diagramHistory.getNestingLevel());
        diagramHistory.setNestingLevel(1);
        assertEquals(false, diagramHistory.hasFutureDiagrams());
    }

    public void testNextForActiveDiagramWithoutFutureDiagrams() {
        addDiagram();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testNextForActiveDiagramWithFutureDiagrams() {
        addDiagram();
        addDiagram();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.next(null);
        assertTrue(diagramHistory.isInPast(0));
        assertTrue(diagramHistory.isInCurrent(1));
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testNextForFirstLevelOfNesting() {
        addDiagram();
        diagramHistory.setNestingLevel(1);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testNextForFirstLevelOfNestingWithManyFutureDiagrams() {
        addDiagram();
        diagramHistory.setNestingLevel(1);
        addDiagram();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        addDiagram();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertTrue(diagramHistory.isInFuture(2));
        diagramHistory.next(null);
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertTrue(diagramHistory.isInPast(0));
        assertTrue(diagramHistory.isInCurrent(1));
        assertTrue(diagramHistory.isInCurrent(2));
        assertTrue(diagramHistory.isInFuture(3));
    }

    public void testNextForEmptyDiagramHistory() {
        assertEquals(0, diagramHistory.getSize());
        assertEquals(0, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.next(null);
        assertEquals(0, diagramHistory.getNumberOfCurrentDiagrams());
    }

    private void addDiagram() {
        Diagram2D diagram = new SimpleLineDiagram();
        diagramHistory.addDiagram(diagram);
    }

    private void addDiagrams(int count){
        for(int i=count; --i>=0;){
            addDiagram();
        }
    }

    public void testReset(){
        addDiagrams(2);
        assertEquals(false, diagramHistory.isEmpty());
        assertTrue(diagramHistory.getNumberOfCurrentDiagrams()>0);
        diagramHistory.reset();
        assertTrue(diagramHistory.isEmpty());
        assertEquals(0, diagramHistory.getSize());
        assertEquals(0, diagramHistory.getNumberOfCurrentDiagrams());

    }

    public void testRemoveLastDiagramWithNesting() {
        addDiagrams(2);

        diagramHistory.setNestingLevel(1);
        assertEquals(2, diagramHistory.getSize());
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());

        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.removeLastDiagram();
        assertEquals(1, diagramHistory.getSize());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testRemoveLastDiagramWithNestingWithCurrentNestedDiagram() {
        addDiagrams(2);

        diagramHistory.setNestingLevel(1);
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());

        diagramHistory.removeLastDiagram();
        assertEquals(1, diagramHistory.getSize());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
    }


    public void testRemoveLastDiagramWithoutNesting() {
        addDiagrams(2);
        diagramHistory.setNestingLevel(0);
        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(true, diagramHistory.isInPast(0));
        diagramHistory.removeLastDiagram();
        assertEquals(1, diagramHistory.getSize());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());

    }

    public void testRemoveLastDiagramWhenHaveFutureDiagrams() {
        addDiagrams(2);
        diagramHistory.setNestingLevel(1);
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
        diagramHistory.addDiagram(new SimpleLineDiagram());
        assertEquals(true, diagramHistory.isInFuture(2));
        diagramHistory.removeLastDiagram();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
    }

    public void testRemoveLastDiagramWhenHistoryIsEmpty() {
        assertEquals(0, diagramHistory.getSize());

        try {
            diagramHistory.removeLastDiagram();
            fail();
        } catch (NoSuchElementException e) {
            assertTrue("Should throw exception", true);
        }
    }

    public void testGetCurrentDiagramSize(){
        assertEquals(0, diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        diagramHistory.removeLastDiagram();
        assertEquals(0, diagramHistory.getNumberOfCurrentDiagrams());
    }

    public void testBackForEmptyDiagram(){
        assertEquals(0, diagramHistory.getSize());
        try {
            diagramHistory.back();
            fail("DiagramHIstory throws exception, when no diagrams are left to go back");
        } catch (NoSuchElementException e) {
            assertTrue("Assertion expected", true);
        }
    }

    public void testBackWithoutNesting(){
        addDiagrams(2);
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());

        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(1, diagramHistory.getFirstCurrentDiagramPosition());

        diagramHistory.back();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
    }

    public void testBackWithNesting(){
        addDiagrams(3);
        diagramHistory.next(null);
        diagramHistory.setNestingLevel(1);
        diagramHistory.next(null);
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(2, diagramHistory.getFirstCurrentDiagramPosition());
        diagramHistory.back();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(1, diagramHistory.getFirstCurrentDiagramPosition());

        diagramHistory.back();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());

        diagramHistory.back();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
    }

    public void testCanMoveUp(){
        assertTrue(diagramHistory.isEmpty());
        assertEquals(false, diagramHistory.canMoveUp());
        addDiagram();
        assertEquals(false, diagramHistory.canMoveUp());
        addDiagram();
        diagramHistory.next(null);
        assertEquals(true, diagramHistory.canMoveUp());
        diagramHistory.back();
        diagramHistory.setNestingLevel(1);
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(true, diagramHistory.canMoveUp());
        diagramHistory.back();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());
        assertEquals(false, diagramHistory.canMoveUp());
        diagramHistory.next(null);
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        assertEquals(0, diagramHistory.getFirstCurrentDiagramPosition());

    }

    public void testAddDiagramSideEffects(){
        addDiagram();
        diagramHistory.setNestingLevel(1);
        addDiagram();
        assertEquals(2, diagramHistory.getNumberOfCurrentDiagrams());
        diagramHistory.back();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
        addDiagram();
        assertEquals(1, diagramHistory.getNumberOfCurrentDiagrams());
    }

    protected void setUp() {
        diagramHistory = new DiagramHistory();
    }

}
