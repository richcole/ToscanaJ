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
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;

import java.awt.*;
import java.awt.geom.Point2D;

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
        } catch (XMLSyntaxError error) {
            fail(error.toString());
        }

    }

    public void testEquals() {
        LabelInfo one = new LabelInfo();
        LabelInfo two = new LabelInfo();
        assertEquals(one, two);

        assertEquals(false, one.equals(null));
        assertEquals(false, one.equals(new Object()));

        one.setBackgroundColor(new Color(1, 2, 3));
        assertEquals(false, one.equals(two));

        one = new LabelInfo();
        one.setTextColor(new Color(1, 2, 3));
        assertEquals(false, one.equals(two));

        one = new LabelInfo();
        one.setTextAlignment(LabelInfo.ALIGNRIGHT);
        assertEquals(false, one.equals(two));

        one = new LabelInfo();
        one.setOffset(new Point2D.Double(0, 10));
        assertEquals(false, one.equals(two));

        // can't test for attaching node, because it is not in public interface

    }


}
