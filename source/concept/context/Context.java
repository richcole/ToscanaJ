package concept.context;

import util.Assert;

import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *  Description of the Class
 *
 *@author     Sergey Yevtushenko
 *@created    8 N=L 2000 3.
 */
public class Context implements AttributeInformationSupplier, ContextEditingInterface {
    private List objects = new ArrayList();
    //---------------------------------------------------------------

    private List attributes = new ArrayList();
    //---------------------------------------------------------------
    private ModifiableBinaryRelation rel;

    private String formAttributeName(int hintForName) {
        //TODO - extract string into properties
        return MessageFormat.format("Attr {0}", new Object[]{new Integer(hintForName)});
    }

    private String formObjectName(int hint) {
        //TODO - extract string into properties
        return MessageFormat.format("Obj {0}", new Object[]{new Integer(hint)});
    }


    public String makeUniqueAttributeName() {
        int startValue = getAttributeCount();
        String candName;
        do {
            candName = formAttributeName(startValue++);
        } while (hasAttributeWithName(candName));

        return candName;
    }

    public String makeUniqueObjectName() {
        int startValue = getObjectCount();
        String candName;
        do {
            candName = formObjectName(startValue++);
        } while (hasObjectWithName(candName));

        return candName;
    }

    public boolean hasAttributeWithName(String cand) {
        for (int i = 0; i < getAttributeCount(); i++) {
            if (getAttribute(i).getName().equals(cand)) {
                return true;
            }
        }
        return false;
    }


    public boolean hasObjectWithName(String cand) {
        for (int i = 0; i < getObjectCount(); i++) {
            if (getObject(i).getName().equals(cand)) {
                return true;
            }
        }
        return false;
    }


    private ContextObject makeAttributeObject(int hintForName) {
        return new ContextObject(formAttributeName(hintForName));
    }


    private transient ContextListenerSupport contextListenersSupport;


    protected transient ContextObjectListener objectNameListener = new DefaultContextObjectListener() {
        public void nameChanged(PropertyChangeEvent evt) {
            getContextListenersSupport().fireObjectNameChanged(evt);
        }
    };

    protected transient ContextObjectListener attributeNameListener = new DefaultContextObjectListener() {
        public void nameChanged(PropertyChangeEvent evt) {
            getContextListenersSupport().fireAttributeNameChanged(evt);
        }
    };

    //---------------------------------------------------------------
    public Context(int objCnt, int arrCnt) {
        createDummyObjectsAndAttribs(objCnt, arrCnt);
        allocateRelation(objCnt, arrCnt);
    }

    //---------------------------------------------------------------
    private void allocateRelation(int sizeX, int sizeY) {
        if (null == rel) {
            rel = ContextFactoryRegistry.createRelation(sizeX, sizeY);
        } else {
            rel.setDimension(sizeX, sizeY);
        }
    }

    //---------------------------------------------------------------
    private void ensureRelationsSizes(int rows, int cols) {
        rel.setDimension(rows, cols);
    }

    //---------------------------------------------------------------
    public ContextObject getAttribute(int index) {
        return (ContextObject) attributes.get(index);
    }

    //---------------------------------------------------------------
    public int getAttributeCount() {
        return attributes.size();
    }

    //---------------------------------------------------------------
    public ContextObject getObject(int index) {
        return (ContextObject) objects.get(index);
    }

    //---------------------------------------------------------------
    public int getObjectCount() {
        return objects.size();
    }

    //---------------------------------------------------------------
    public BinaryRelation getRelation() {
        return rel;
    }

    //---------------------------------------------------------------
    public boolean getRelationAt(int x, int y) {
        return rel.getRelationAt(x, y);
    }

    //---------------------------------------------------------------
    public void increaseAttributes(int incrAttr) {
        util.Assert.isTrue(incrAttr > 0, "Attrib increment should be greater than zero");
        int oldColCnt = rel.getColCount();
        int newColCnt = oldColCnt + incrAttr;
        ensureRelationsSizes(rel.getRowCount(), newColCnt);
        addAttributesWithDefaultNamesInRange(oldColCnt, newColCnt);
        getContextListenersSupport().fireContextStructureChanged();
    }

    public void addAttribute(ContextObject newAttrib) {
        final int newAttributeIndex = rel.getColCount();
        ensureRelationsSizes(rel.getRowCount(), newAttributeIndex + 1);
        addAttributeToAttributeList(newAttrib, newAttributeIndex);
        getContextListenersSupport().fireContextStructureChanged();
    }

    public void addObject(ContextObject newObject) {
        ensureRelationsSizes(rel.getRowCount() + 1, rel.getColCount());
        addObjectToObjectList(newObject);
        getContextListenersSupport().fireContextStructureChanged();
    }

    public void addObjectWithNameAndIntent(String name, Set intent) {
        int objIndex = getObjectCount();
        increaseObjects(1);
        getObject(objIndex).setName(name);
        rel.getModifiableSet(objIndex).copy(intent);
    }

    private void addAttributesWithDefaultNamesInRange(int from, int till) {
        for (int j = from; j < till; j++) {
            addAttributeToAttributeList(makeAttributeObject(j + 1), j);
        }
    }

    private void addAttributeToAttributeList(ContextObject newAttribute, int attributeIndex) {
        Assert.isTrue(!newAttribute.isObject());
        newAttribute.setContextObjectListener(attributeNameListener);
        attributes.add(newAttribute);
        getContextListenersSupport().fireAttributeInserted(attributeIndex);
    }

    //---------------------------------------------------------------
    public void increaseObjects(int incrObjects) {
        util.Assert.isTrue(incrObjects > 0, "Objects increment should be greater than zero");
        int oldRowCnt = rel.getRowCount();
        int newRowCnt = oldRowCnt + incrObjects;
        ensureRelationsSizes(newRowCnt, rel.getColCount());
        addObjectsWithDefaultNamesInRange(oldRowCnt, newRowCnt);
        getContextListenersSupport().fireContextStructureChanged();
    }

    private void addObjectsWithDefaultNamesInRange(int from, int till) {
        for (int j = from; j < till; j++) {
            int hint = (j + 1);
            ContextObject newObject = new ContextObject(formObjectName(hint), true);
            addObjectToObjectList(newObject);
        }
    }

    private void addObjectToObjectList(ContextObject newObject) {
        Assert.isTrue(newObject.isObject());
        newObject.setContextObjectListener(objectNameListener);
        objects.add(newObject);
    }

    //---------------------------------------------------------------
    public void purifyAttributes() {
        doPurifyAttributes();
        getContextListenersSupport().realisePostponedStructureChange();
    }

    private void doPurifyAttributes() {
        int numObj = rel.getRowCount();
        int numAttr = rel.getColCount();
        ModifiableSet toClear = ContextFactoryRegistry.createSet(numAttr);
        calcReducibleAttributes(numAttr, numObj, toClear);
        for (int j = toClear.length(); --j >= 0;) {
            if (toClear.in(j)) {
                doRemoveAttribute(j);
            }
        }
    }

    //---------------------------------------------------------------
    public void purifyObjects() {
        doPurifyObjects();
        getContextListenersSupport().realisePostponedStructureChange();

    }

    private void doPurifyObjects() {
        int bound = rel.getRowCount();
        for (int i = 0; i < bound; i++) {
            Set curr = rel.getSet(i);
            for (int j = i + 1; j < bound; j++) {
                Set other = rel.getSet(j);
                if (curr.equals(other)) {
                    doRemoveObject(j);
                    j--;
                    bound--;
                }
            }
        }
    }

    //---------------------------------------------------------------
    public void removeAttribute(int index) {
        doRemoveAttribute(index);
        getContextListenersSupport().fireContextStructureChanged();
    }

    protected void doRemoveAttribute(int index) {
        rel.removeCol(index);
        ContextObject attr = (ContextObject) attributes.get(index);
        attr.setContextObjectListener(null);
        attributes.remove(index);

        getContextListenersSupport().fireAttributeRemoved(index);
        getContextListenersSupport().madePostponedStructureChange();
    }

    //---------------------------------------------------------------
    public void removeObject(int index) {
        doRemoveObject(index);
        getContextListenersSupport().fireContextStructureChanged();
    }

    private void doRemoveObject(int index) {
        rel.removeRow(index);
        ContextObject obj = (ContextObject) objects.get(index);
        obj.setContextObjectListener(null);
        objects.remove(index);
        getContextListenersSupport().madePostponedStructureChange();
    }

    //---------------------------------------------------------------
    public void setDimension(int numObj, int numAttr) {
        int oldNumObj = getObjectCount();
        if (numObj > oldNumObj) {
            increaseObjects(numObj - oldNumObj);
        } else {
            for (int k = oldNumObj; --k >= numObj;) {
                doRemoveObject(k);
            }
        }
        int oldNumAttr = getAttributeCount();
        if (numAttr > oldNumAttr) {
            increaseAttributes(numAttr - oldNumAttr);
        } else {
            for (int k = oldNumAttr; --k >= numAttr;) {
                doRemoveAttribute(k);
            }
        }
        getContextListenersSupport().realisePostponedStructureChange();
    }

    public Context(ModifiableBinaryRelation rel) {
        this.rel = rel;
        createDummyObjectsAndAttribs(rel.getRowCount(), rel.getColCount());
    }

    private void createDummyObjectsAndAttribs(int objCnt, int attrCnt) {
        addObjectsWithDefaultNamesInRange(0, objCnt);
        addAttributesWithDefaultNamesInRange(0, attrCnt);
    }

    //---------------------------------------------------------------
    public void setRelationAt(int x, int y, boolean value) {
        if (rel.getRelationAt(x, y) != value) {
            rel.setRelationAt(x, y, value);
            getContextListenersSupport().fireRelationChanged();
        }
    }

    public void addContextListener(ContextListener lst) {
        getContextListenersSupport().addContextListener(lst);
    }


    protected synchronized ContextListenerSupport getContextListenersSupport() {
        if (null == contextListenersSupport) {
            contextListenersSupport = new ContextListenerSupport(this);
        }
        return contextListenersSupport;
    }


    public synchronized void removeContextListener(ContextListener lst) {
        if (null == contextListenersSupport) {
            return;
        }
        contextListenersSupport.removeContextListener(lst);
    }


    public synchronized boolean hasContextListener(ContextListener lst) {
        if (null == contextListenersSupport) {
            return false;
        }
        return contextListenersSupport.hasListener(lst);
    }

    public void transpose() {
        ModifiableBinaryRelation newRel = BinaryRelationUtils.makeTransposedRelation(rel);
        List tmp = objects;
        objects = attributes;
        attributes = tmp;

        rel = newRel;
        getContextListenersSupport().fireContextStructureChanged();
    }

    public void calcReducibleAttributes(int numAttr, int numObj, ModifiableSet toClear) {
        ModifiableSet outer = ContextFactoryRegistry.createSet(numAttr);
        ModifiableSet inner = ContextFactoryRegistry.createSet(numAttr);
        ModifiableSet allAttr = ContextFactoryRegistry.createSet(numAttr);
        allAttr.fillByOne(numAttr);
        for (int j = 0; j < numAttr; j++) {
            if (!toClear.in(j)) {
                inner.copy(allAttr);
                outer.clearSet();
                for (int i = numObj; --i >= 0;) {
                    Set curr = rel.getSet(i);
                    if (curr.in(j)) {
                        inner.and(curr);
                    } else {
                        outer.or(curr);
                    }
                }

                //(inner &~outer) \j = set of all attributes, equivalent to j
                inner.andNot(outer);
                inner.remove(j);
                toClear.or(inner);
            }
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Context)) {
            return false;
        }
        Context that = (Context) obj;
        if (!(this.getRelation().equals(that.getRelation()))) {
            return false;
        }
        if (!this.attributes.equals(that.attributes)) {
            return false;
        }
        if (!this.objects.equals(that.objects)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "Attributes[" + attributes + "] Objects[" + objects + "] Relation [" + getRelation() + "]";
    }
}