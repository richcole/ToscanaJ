/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram.tests;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

public class DiagramViewTest extends TestCase {
    public DiagramViewTest(final String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(DiagramViewTest.class);
    }

    static class TestDiagramView extends DiagramView {

        public TestDiagramView() {
            super();
        }

        @Override
        public boolean isScreenTransformDirty() {
            return super.isScreenTransformDirty();
        }

        @Override
        public void makeScreenTransformClear() {
            super.makeScreenTransformClear();
        }

    }

    public void testChangeOfScreenTransform() {
        final TestDiagramView view = new TestDiagramView();
        view.setSize(100, 100);
        assertEquals(new Dimension(100, 100), view.getSize());
        view.makeScreenTransformClear();
        assertEquals(false, view.isScreenTransformDirty());
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                assertEquals(true, view.isScreenTransformDirty());
            }
        });
        view.setSize(200, 200);
        assertEquals(new Dimension(200, 200), view.getSize());
    }
}
