package concept.context.tests;

import concept.context.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.beans.PropertyChangeEvent;

/**
 * JUnit test case for ContextTest
 */

public class ContextTest extends TestCase {
    private static final Class THIS = ContextTest.class;
    Context cxt;

    public ContextTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    protected void setUp() {
        cxt = SetBuilder.makeContext(new int[][]{{0, 0}, {0, 0}});
    }

    interface ContextStructureModification {
        void modifyContext(Context cxt);
    }

    public void testAddColChange() {
        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.increaseAttributes(2);
            }
        };

        cxt = SetBuilder.makeContext(new int[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
        int expectedNumberOfCalls = 1;

        expectStructureChangedCall(cxt, expectedNumberOfCalls, modification);
    }

    public void testAddRowChange() {
        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.increaseObjects(2);
            }
        };

        cxt = SetBuilder.makeContext(new int[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
        int expectedNumberOfCalls = 1;

        expectStructureChangedCall(cxt, expectedNumberOfCalls, modification);
    }

    public void testTransposeChange() {
        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.transpose();
            }
        };

        cxt = SetBuilder.makeContext(new int[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
        int expectedNumberOfCalls = 1;

        expectStructureChangedCall(cxt, expectedNumberOfCalls, modification);
    }

    public void testPurifyObjectsChange() {
        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.purifyObjects();
            }
        };

        cxt = SetBuilder.makeContext(new int[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
        int expectedNumberOfCalls = 1;

        expectStructureChangedCall(cxt, expectedNumberOfCalls, modification);

    }

    public void testPurifyAttributesChange() {
        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.purifyAttributes();
            }
        };

        cxt = SetBuilder.makeContext(new int[][]{{0, 1, 0, 1}, {0, 1, 0, 1}, {0, 1, 0, 1}});
        int expectedNumberOfCalls = 1;

        expectStructureChangedCall(cxt, expectedNumberOfCalls, modification);

    }



    private static void expectStructureChangedCall(Context cxt, int expectedNumberOfCalls, ContextStructureModification modification) {
        MockContextListener mock = new MockContextListener() {
            public void contextStructureChanged() {
                counter.inc();
            }
        };
        cxt.addContextListener(mock);
        mock.setExpectedCalls(expectedNumberOfCalls);
        modification.modifyContext(cxt);
        mock.verify();
    }

    public void testStructureChangeInContextListener() {
        MockContextListener mock = new MockContextListener() {
            public void contextStructureChanged() {
                counter.inc();
            }
        };

        mock.setExpectedCalls(1);
        cxt.addContextListener(mock);
        cxt.setDimension(3, 2);
        mock.verify();
        assertEquals(3, cxt.getObjectCount());
        assertEquals(2, cxt.getAttributeCount());

        mock.setExpectedCalls(1);
        cxt.removeObject(1);
        mock.verify();
        assertEquals(2, cxt.getObjectCount());
        assertEquals(2, cxt.getAttributeCount());


        mock.setExpectedCalls(1);
        cxt.increaseObjects(1);
        mock.verify();
        assertEquals(3, cxt.getObjectCount());
        assertEquals(2, cxt.getAttributeCount());

        mock.setExpectedCalls(1);
        cxt.setDimension(2, 2);
        mock.verify();
        assertEquals(2, cxt.getObjectCount());
        assertEquals(2, cxt.getAttributeCount());


        mock.setExpectedCalls(1);
        cxt.increaseAttributes(1);
        mock.verify();
        assertEquals(2, cxt.getObjectCount());
        assertEquals(3, cxt.getAttributeCount());

        mock.setExpectedCalls(1);
        cxt.removeAttribute(1);
        mock.verify();
        assertEquals(2, cxt.getObjectCount());
        assertEquals(2, cxt.getAttributeCount());
    }

    public void testRelationChangeInContextListener() {
        MockContextListener mock = new MockContextListener() {
            public void relationChanged() {
                counter.inc();
            }
        };

        mock.setExpectedCalls(1);
        cxt.addContextListener(mock);
        cxt.setRelationAt(0, 0, true);
        mock.verify();
        assertEquals(true, cxt.getRelationAt(0, 0));

        mock.setExpectedCalls(0);
        cxt.setRelationAt(0, 0, true);
        mock.verify();

        mock.setExpectedCalls(0);
        cxt.removeContextListener(mock);
        cxt.setRelationAt(0, 0, false);
        mock.verify();
        assertEquals(false, cxt.getRelationAt(0, 0));
    }

    public void testObjectNameChangeInContextListener() {
        MockContextListener mockObjectListener = makeObjectChangeListener();
        MockContextListener mockAttributeListener = makeAttributeChangeListener();


        cxt.addContextListener(mockObjectListener);
        cxt.addContextListener(mockAttributeListener);

        mockObjectListener.setExpectedCalls(1);
        mockAttributeListener.setExpectedCalls(0);
        final String newObjectName = "New OName";
        assertEquals(false, newObjectName.equals(cxt.getObject(0).getName()));
        cxt.getObject(0).setName(newObjectName);
        assertEquals(newObjectName, cxt.getObject(0).getName());
        mockObjectListener.verify();
        mockAttributeListener.verify();

        mockObjectListener.setExpectedCalls(0);
        mockAttributeListener.setExpectedCalls(1);
        final String newAttributeName = "New AName";
        assertEquals(false, newAttributeName.equals(cxt.getAttribute(0).getName()));
        cxt.getAttribute(0).setName(newAttributeName);
        assertEquals(newAttributeName, cxt.getAttribute(0).getName());
        mockObjectListener.verify();
        mockAttributeListener.verify();

        mockObjectListener.setExpectedCalls(0);
        mockAttributeListener.setExpectedCalls(0);
        ContextObject obj2 = cxt.getObject(1);
        final String newSecondObjectName = "New SOName";
        assertEquals(false, newSecondObjectName.equals(obj2.getName()));
        cxt.removeObject(1);
        obj2.setName(newSecondObjectName);
        assertEquals(newSecondObjectName, obj2.getName());
        mockObjectListener.verify();
        mockAttributeListener.verify();

        mockObjectListener.setExpectedCalls(0);
        mockAttributeListener.setExpectedCalls(0);
        ContextObject attr2 = cxt.getAttribute(1);
        final String newSecondAttributeName = "New SAName";
        assertEquals(false, newSecondAttributeName.equals(obj2.getName()));
        cxt.removeAttribute(1);
        attr2.setName(newSecondAttributeName);
        assertEquals(newSecondAttributeName, attr2.getName());
        mockObjectListener.verify();
        mockAttributeListener.verify();

    }

    private MockContextListener makeObjectChangeListener() {
        return new MockContextListener() {
            public void objectNameChanged(PropertyChangeEvent evt) {
                counter.inc();
            }

        };
    }

    private MockContextListener makeAttributeChangeListener() {
        return new MockContextListener() {
            public void attributeNameChanged(PropertyChangeEvent evt) {
                counter.inc();
            }
        };
    }

    public void testEquals() {

        final int[][] relation = new int[][]{{1, 0},
                                             {0, 1}};
        Context cxt1 = SetBuilder.makeContext(relation);
        Context cxt2 = SetBuilder.makeContext(relation);

        assertEquals(cxt1, cxt2);

        assertEquals(false, cxt1.equals(new Object()));

        assertEquals(false, cxt1.equals(null));

        cxt1.setRelationAt(0, 1, true);

        assertEquals(false, cxt1.equals(cxt2));

        cxt1.setRelationAt(0, 1, false);
        assertEquals(cxt1, cxt2);

        String oldName = cxt1.getAttribute(0).getName();
        cxt1.getAttribute(0).setName("Not a default name for attribute");
        assertEquals(false, cxt1.equals(cxt2));

        cxt1.getAttribute(0).setName(oldName);
        assertEquals(cxt1, cxt2);

        cxt1.getObject(0).setName("Not a default name for object");
        assertEquals(false, cxt1.equals(cxt2));

    }

    public void testAddAttribute() {
        cxt = new Context(0, 0);

        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.addAttribute(new ContextObject("One", false));
            }
        };

        expectStructureChangedCall(cxt, 1, modification);

        assertEquals(1, cxt.getAttributeCount());

        MockContextListener listener = makeAttributeChangeListener();
        cxt.addContextListener(listener);
        listener.setExpectedCalls(1);
        cxt.getAttribute(0).setName("Other name");
        listener.verify();
    }

    public void testAddObject() {
        cxt = new Context(0, 0);
        ContextStructureModification modification = new ContextStructureModification() {
            public void modifyContext(Context cxt) {
                cxt.addObject(new ContextObject("New object", true));
            }
        };

        expectStructureChangedCall(cxt, 1, modification);

        assertEquals(1, cxt.getObjectCount());

        MockContextListener listener = makeObjectChangeListener();
        cxt.addContextListener(listener);
        listener.setExpectedCalls(1);
        cxt.getObject(0).setName("Other object name");
        listener.verify();
    }

    MockContextListener makeAttributeInsertionListener(){
        return new MockContextListener(){
            public void attributeChanged(ContextChangeEvent changeEvent) {
                if(changeEvent.getType()==ContextChangeEvent.ATTRIBUTE_ADDED){
                    counter.inc();
                }
            }
        };
    }

    MockContextListener makeAttributeRemovalListener(){
        return new MockContextListener(){
            public void attributeChanged(ContextChangeEvent changeEvent) {
                if(changeEvent.getType()==ContextChangeEvent.ATTRIBUTE_REMOVED){
                    counter.inc();
                }
            }
        };
    }

    public void testAttributeAddRemoveNotification(){
        cxt = new Context(2,3);

        MockContextListener insertionListener = makeAttributeInsertionListener();
        cxt.addContextListener(insertionListener);

        MockContextListener removalListener = makeAttributeRemovalListener();
        cxt.addContextListener(removalListener);

        insertionListener.setExpectedCalls(2);
        cxt.increaseAttributes(2);
        insertionListener.verify();

        insertionListener.setExpectedCalls(1);
        cxt.addAttribute(new ContextObject("New Attribute", false));
        insertionListener.verify();

        removalListener.setExpectedCalls(1);
        cxt.removeAttribute(0);
        removalListener.verify();

        assertEquals(5, cxt.getAttributeCount());
        removalListener.setExpectedCalls(2);
        cxt.setDimension(1, 3);
        removalListener.verify();

        insertionListener.setExpectedCalls(3);
        cxt.setDimension(2, 6);
        insertionListener.verify();

    }

}