/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram.tests;

import com.mockobjects.ExpectationCounter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import java.awt.*;

public class DiagramViewTest extends TestCase {
    public DiagramViewTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(DiagramViewTest.class);
    }

    static class TestDiagramView extends DiagramView {

        boolean repaintCalled = false;

        public TestDiagramView() {
        }

        public boolean isScreenTransformDirty() {
            return super.isScreenTransformDirty();
        }

        public void makeScreenTransformClear() {
            super.makeScreenTransformClear();
        }

        public void clearRepaintFlag(){
            repaintCalled = false;
        }

        public void repaint() {
            repaintCalled = true;
            // super.p;
        }

    }

    public void testChangeOfScreenTransform() {
        TestDiagramView view = new TestDiagramView();
        view.setSize(100, 100);
        assertEquals(new Dimension(100, 100), view.getSize());
        view.makeScreenTransformClear();
        assertEquals(false, view.isScreenTransformDirty());
        view.clearRepaintFlag();
        view.setSize(200, 200);
        assertEquals(new Dimension(200, 200), view.getSize());
        assertEquals(true, view.isScreenTransformDirty());
        assertEquals(true, view.repaintCalled);
    }
}
