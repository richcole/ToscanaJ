package concept.context;

import java.util.LinkedList;

/**
 * Insert the type's description here.
 * Creation date: (09.03.01 23:30:36)
 * @author:
 */
public class BinaryRelationUtils {
    /**
     * Insert the method's description here.
     * Creation date: (08.07.01 2:31:15)
     * @return java.lang.String
     * @deprecated
     */
    public static String describeRelation(BinaryRelation rel) {
        StringBuffer ret = new StringBuffer();
        ret.append("Rows;");
        ret.append(rel.getRowCount());
        ret.append(";Cols;");
        ret.append(rel.getColCount());
        int cnt = calculateFilledCells(rel);
        ret.append(";Filled cells;");
        ret.append(cnt);
        ret.append(";");
        return ret.toString();
    }


    public static int calculateFilledCells(BinaryRelation rel) {
        int cnt = 0;
        for (int i = rel.getRowCount(); --i >= 0;) {
            for (int j = rel.getColCount(); --j >= 0;) {
                if (rel.getRelationAt(i, j)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }


    /**
     * Insert the method's description here.
     * Creation date: (04.08.01 6:54:31)
     * @return concept.context.BinaryRelation
     * @param rel concept.context.BinaryRelation
     */
    public static BinaryRelation lexSort(BinaryRelation rel) {
        LinkedList zeros = new LinkedList();
        LinkedList ones = new LinkedList();

        LinkedList work = new LinkedList();
        for (int i = rel.getRowCount(); --i >= 0;) {
            work.add(rel.getSet(i));
        }
        for (int j = rel.getColCount(); --j >= 0;) {
            zeros.clear();
            ones.clear();
            for (int i = rel.getRowCount(); --i >= 0;) {
                Set s = (Set) work.removeFirst();
                if (s.in(j)) {
                    ones.add(s);
                } else {
                    zeros.add(s);
                }
            }
            work.addAll(zeros);
            work.addAll(ones);
        }
        ModifiableBinaryRelation ret = ContextFactoryRegistry.createRelation(rel.getRowCount(), rel.getColCount());

        for (int i = 0; i < ret.getRowCount(); i++) {
            Set s = (Set) work.removeFirst();
            ret.getModifiableSet(i).copy(s);
        }
        return ret;
    }


    /**
     * Insert the method's description here.
     * Creation date: (14.07.01 7:42:59)
     * @param rel concept.context.BinaryRelation
     * @param pw java.io.PrintWriter
     */
    public static void logRelation(BinaryRelation rel, java.io.PrintWriter pw) {
        pw.println("===============================================");
        pw.println(" Rows " + rel.getRowCount());
        pw.println(" Cols " + rel.getColCount());
        for (int i = 0; i < rel.getRowCount(); i++) {
            pw.println(rel.getSet(i));
        }
        pw.println("===============================================");
    }


    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 23:31:56)
     * @param rel concept.context.BinaryRelation
     */
    public static void makeSymmetric(ModifiableBinaryRelation rel) {
        if (rel.getRowCount() != rel.getColCount()) {
            throw new IllegalArgumentException("make Symmetric works only with square relations");
        }
        final int size = rel.getRowCount();
        for (int i = size; --i >= 0;) {
            for (int j = size; --j >= 0;) {
                if (rel.getRelationAt(i, j)) {
                    rel.setRelationAt(j, i, true);
                }
            }
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (28.07.01 23:41:57)
     */
    public static ModifiableBinaryRelation makeTransposedRelation(BinaryRelation rel) {
        ModifiableBinaryRelation newRel = ContextFactoryRegistry.createRelation(rel.getColCount(), rel.getRowCount());
        for (int i = rel.getColCount(); --i >= 0;) {
            for (int j = rel.getRowCount(); --j >= 0;) {
                newRel.setRelationAt(i, j, rel.getRelationAt(j, i));
            }
        }
        return newRel;
    }


    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 23:32:28)
     * @param rel concept.context.BinaryRelation
     */
    public static void transitiveClosure(ModifiableBinaryRelation rel) {
        if (rel.getRowCount() != rel.getColCount()) {
            throw new IllegalArgumentException("transitiveClosure can be applyied only to square relations");
        }
        final int size = rel.getRowCount();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j && rel.getRelationAt(i, j)) {
                    rel.getModifiableSet(j).or(rel.getSet(i));
                }
            }
        }
    }

    public static BinaryRelation calcAttributesOrder(BinaryRelation relation) {
        final int size = relation.getColCount();

        ModifiableBinaryRelation ret = ContextFactoryRegistry.createRelation(size, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ret.setRelationAt(i, j, isAttributeSubsetOf(relation, i, j));
            }
        }
        return ret;
    }

    protected static boolean isAttributeSubsetOf(BinaryRelation rel, int attr1, int attr2) {
        for (int k = rel.getRowCount(); --k >= 0;) {
            if (rel.getRelationAt(k, attr1) && !rel.getRelationAt(k, attr2)) {
                return false;
            }
        }
        return true;
    }

}