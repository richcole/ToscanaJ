/*
 * Date: 13.04.2002
 * Time: 22:06:38
 * To change template for new class use 
 */
package net.sourceforge.toscanaj.view.diagram.tests;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import java.awt.*;

import com.mockobjects.ExpectationCounter;

public class DiagramViewTest extends TestCase{
    public DiagramViewTest(String s) {
        super(s);
    }

    public static Test suite(){
        return new TestSuite(DiagramViewTest.class);
    }

    static class TestDiagramView extends DiagramView{

        public TestDiagramView() {
        }

        public boolean isScreenTransformDirty() {
            return super.isScreenTransformDirty();
        }

        public void makeScreenTransformClear() {
            super.makeScreenTransformClear();
        }

        public ExpectationCounter repaintCounter = new ExpectationCounter("Expected calls to repaint");

        public void repaint(){
            repaintCounter.inc();
           // super.p;
        }
    }

    public void testChangeOfScreenTransform(){
          TestDiagramView view = new TestDiagramView();
          view.setSize(100, 100);
          assertEquals(new Dimension(100, 100), view.getSize());
          view.makeScreenTransformClear();
          assertEquals(false, view.isScreenTransformDirty());
          view.repaintCounter.setExpected(1);
          view.setSize(200, 200);
          assertEquals(new Dimension(200, 200), view.getSize());
          assertEquals(true, view.isScreenTransformDirty());
          view.repaintCounter.verify();
    }
}
