/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.lattice.DummyConcept;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;

import java.awt.geom.Point2D;
import java.awt.*;

import org.jdom.Element;
import util.StringUtil;

public class LabelInfoTest extends TestCase {
    final static Class THIS = LabelInfoTest.class;
    public LabelInfoTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testReadWriteFromXML() {

        LabelInfo node = new LabelInfo();

        Element xmlDesc = node.toXML();

        try {
            LabelInfo other = new LabelInfo(xmlDesc);
            assertEquals(node, other);
        } catch (XML_SyntaxError error) {
            fail(StringUtil.stackTraceToString(error));
        }

    }

    public void testEquals(){
        LabelInfo one = new LabelInfo();
        LabelInfo two = new LabelInfo();
        assertEquals(one, two);

        assertEquals(false, one.equals(null));
        assertEquals(false, one.equals(new Object()));

        one.setBackgroundColor(new Color(1, 2, 3));
        assertEquals(false, one.equals(two));

        one = new LabelInfo();
        one.setTextColor(new Color(1,2,3));
        assertEquals(false, one.equals(two));

        one  = new LabelInfo();
        one.setTextAlignment(LabelInfo.ALIGNRIGHT);
        assertEquals(false, one.equals(two));

        one = new LabelInfo();
        one.setOffset(new Point2D.Double(0, 10));
        assertEquals(false, one.equals(two));

        // can't test for attaching node, because it is not in public interface

    }


}
