package concept.context.calculationstrategies;

import concept.context.*;
import util.Assert;

/**
 *@author     Sergey
 *@created    24 Èþëü 2000 ã.
 */
public class NextClosedSetCalculator extends AbstractConceptCalcStrategy implements LatticeCalcStrategy {
    //-----------------------------------------------------
    int objInImpl;
    private ModifiableSet attrSet;
    private ModifiableSet closedObjects;
    private ModifiableSet tempAttrSet;
    private ModifiableSet tempAttrSet2;
    private ModifiableSet allAttrSet;


    /**
     *  Constructor for the NextClosedSetCalculator object
     */
    public NextClosedSetCalculator() {
        super();
    }

    //-----------------------------------------------------
    //-----------------------------------------------------
    private boolean closure(ModifiableSet set, int j) {
        boolean ret = true;
        int i = set.length();
        Assert.isTrue(i > 0);
        //calculating dash operator
        i--;
        attrSet.copy(rel.getSet(i));
        i--;
        for (; i >= 0; i--) {
            if (set.in(i)) {
                attrSet.and(rel.getSet(i));
            }
        }
        //calculating reverse dash operator
        int numObj = rel.getRowCount();
        i = 0;
        while (i < numObj) {
            if (attrSet.isSubsetOf(rel.getSet(i))) {
                if (!set.in(i)) {
                    set.put(i);
                    if (i < j) {
                        ret = false;
                    }
                }
            }
            i++;
        }
        return ret;
    }
    //-----------------------------------------------------
    private boolean closureAttr(concept.context.ModifiableSet set, int j, concept.context.ModifiableSet notJ) {
        tempAttrSet.copy(allAttrSet);
        set.put(j);
        closedObjects.clearSet();
        for (int i = rel.getRowCount(); --i >= 0;) {
            Set tmp = rel.getSet(i);
            if (set.isSubsetOf(tmp)) {
                tempAttrSet.and(tmp);
                closedObjects.put(i);
            }
        }
        tempAttrSet2.copy(tempAttrSet);
        tempAttrSet2.andNot(notJ);
        set.remove(j);
        return set.equals(tempAttrSet2);
    }
    //-----------------------------------------------------
    public void calculateConceptSet() {
        startCalc();
        //*DBG*/ rel.printDebugData();
        callback.startCalc();
        nextClosedSetAttr();
        callback.finishCalc();
    }
    //-----------------------------------------------------
    private boolean isAttrSetClosed(concept.context.Set set) {
        tempAttrSet.copy(allAttrSet);
        objInImpl = 0;
        for (int i = rel.getRowCount(); --i >= 0;) {
            Set tmp = rel.getSet(i);
            if (set.isSubsetOf(tmp)) {
                tempAttrSet.and(tmp);
                objInImpl++;
            }
        }
        return tempAttrSet.equals(set);
    }

    //-----------------------------------------------------
    private void nextClosedSet() {
        int numObj = rel.getRowCount();
        concept.context.ModifiableSet A = zeroClosure();
        callback.addConcept(A, attrSet);

        concept.context.ModifiableSet b = ContextFactoryRegistry.createSet(numObj);
        concept.context.ModifiableSet notJ = ContextFactoryRegistry.createSet(numObj);

        int j = numObj - 1;
        while (j >= 0) {
            //that is a not equal g
            j = numObj - 1;
            notJ.clearSet();
            for (; j >= 0; j--) {
                notJ.put(j);
                if (!A.in(j)) {
                    //DBG System.out.println("****************************");
                    //DBG System.out.println("Next closed set A="+A);
                    b.copy(A);
                    b.andNot(notJ);
                    b.put(j);
                    boolean res = closure(b, j);
                    //DBG System.out.println("J="+j+" : after closure b "+b);
                    if (res) {
                        callback.addConcept(b, attrSet);
                        A.copy(b);
                        break;
                    }
                }
            }
        }
    }
    //-----------------------------------------------------
    private void nextClosedSetAttr() {
        final int numAttr = rel.getColCount();
        concept.context.ModifiableSet b = ContextFactoryRegistry.createSet(numAttr);
        concept.context.ModifiableSet notJ = ContextFactoryRegistry.createSet(numAttr);
        zeroClosureAttr();
        callback.addConcept(closedObjects, attrSet);
        int j = numAttr - 1;
        while (j >= 0) {
            //that is a not equal g
            j = numAttr - 1;
            notJ.clearSet();
            for (; j >= 0; j--) {
                notJ.put(j);
                if (!attrSet.in(j)) {
                    b.copy(attrSet);
                    b.andNot(notJ);
                    if (closureAttr(b, j, notJ)) {
                        attrSet.copy(tempAttrSet);
                        callback.addConcept(closedObjects, tempAttrSet);
                        break;
                    }
                }
            }
        }
    }
    //-----------------------------------------------------
    /**
     *  Description of the Method
     */
    void startCalc() {
        int col = rel.getColCount();
        closedObjects = ContextFactoryRegistry.createSet(rel.getRowCount());
        attrSet = ContextFactoryRegistry.createSet(col);
        allAttrSet = ContextFactoryRegistry.createSet(col);
        allAttrSet.fillByOne(rel.getColCount());
        tempAttrSet = ContextFactoryRegistry.createSet(col);
        tempAttrSet2 = ContextFactoryRegistry.createSet(col);
    }
    //-----------------------------------------------------
    private ModifiableSet zeroClosure() {
        int numObj = rel.getRowCount();
        concept.context.ModifiableSet ret = ContextFactoryRegistry.createSet(numObj);
        attrSet.copy(allAttrSet);
        for (int i = 0; i < numObj; i++) {
            if (attrSet.isSubsetOf(rel.getSet(i))) {
                ret.put(i);
            }
        }
        return ret;
    }
    //-----------------------------------------------------
    private void zeroClosureAttr() {
        int numObj = rel.getRowCount();
        objInImpl = numObj;
        closedObjects.fillByOne(numObj);
        attrSet.copy(allAttrSet);
        for (int j = numObj; --j >= 0;) {
            attrSet.and(rel.getSet(j));
        }
    }
    //-----------------------------------------------------
    public void buildLattice() {
        Assert.isTrue(callback instanceof concept.context.enumcallbacks.NextClosedSetLatticeBuilderCallback);
        calculateConceptSet();
    }

    public void tearDown() {
        super.tearDown();
        attrSet = null;
        closedObjects = null;
        tempAttrSet = null;
        tempAttrSet2 = null;
        allAttrSet = null;
    }
}