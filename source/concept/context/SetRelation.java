package concept.context;

import util.Assert;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SetRelation implements ModifiableBinaryRelation {
// implementation of concept.context.BinaryRelation interface
    private ModifiableSet[] relation;
    int sizeX;
    int sizeY;

    public SetRelation(int _sizeX, int _sizeY) {
        super();
        setDimension(_sizeX, _sizeY);
    }

    public void clearRelation() {
        for (int j = sizeX; --j >= 0;) {
            relation[j].clearSet();
        }
    }

    public ModifiableBinaryRelation makeModifiableCopy() {
        SetRelation ret = new SetRelation(sizeX, sizeY);
        for (int i = 0; i < sizeX; i++) {
            ret.getModifiableSet(i).copy(getSet(i));
        }
        return ret;
    }

    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 23:22:43)
     */
    public Object clone() {
        return makeModifiableCopy();
    }

    public int getColCount() {
        return sizeY;
    }

    /**
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return value of cell with coordinates(x,y)
     */
    public boolean getRelationAt(int x, int y) {
        return relation[x].in(y);
    }

    public int getRowCount() {
        return sizeX;
    }

    public ModifiableSet getModifiableSet(int row) {
        return relation[row];
    }

    public Set getSet(int x) {
        return getModifiableSet(x);
    }


    public void removeCol(int col) {
        util.Assert.isTrue(0 <= col, "removeCol: col should be greater or equal to zero");
        util.Assert.isTrue(col < sizeY, "removeCol: col = " + col + "  should be less then relation column count");
        if (1 == sizeY) {
            //we have only one element
            clearRelation();
        } else {
            for (int j = sizeX; --j >= 0;) {
                relation[j].exclude(col);
            }
            --sizeY;
        }
    }

    public void removeRow(int row) {
        util.Assert.isTrue(0 <= row, "removeRow: row should be greater or equal to zero");
        util.Assert.isTrue(row < sizeX, "removeRow: row should be less then relation row count");
        int lastElIndex = sizeX - 1;
        if (row != lastElIndex) {
            System.arraycopy(relation, row + 1, relation, row, lastElIndex - row);
        }
        relation[lastElIndex] = null;
        sizeX = lastElIndex;
    }

    public synchronized void setDimension(int rows, int cols) {
        if (rows < 0) {
            throw new IndexOutOfBoundsException("Dimension X of relation should be nonnegative");
        } // end of if ()
        if (cols < 0) {
            throw new IndexOutOfBoundsException("Dimension Y of relation should be nonnegative");
        } // end of if ()
        if (null == relation) {
            relation = new concept.context.ModifiableSet[rows];
            for (int j = 0; j < rows; j++) {
                relation[j] = ContextFactoryRegistry.createSet(cols);
            }
        } else {
            if (sizeX < rows) {
                ModifiableSet[] temp = new concept.context.ModifiableSet[rows];
                System.arraycopy(relation, 0, temp, 0, sizeX);
                relation = temp;
                for (int j = sizeX; j < rows; j++) {
                    relation[j] = ContextFactoryRegistry.createSet(cols);
                }
            }
        }
        int bound = Math.min(sizeX, rows);

        sizeX = rows;

        if (cols != sizeY) {
            for (int i = 0; i < bound; i++) {
                relation[i].resize(cols);
            }
        }
        sizeY = cols;


    }

    /**
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param value new value for cell with coordinates(x,y)
     */
    public void setRelationAt(int x, int y, boolean value) {
        Assert.isTrue(x < sizeX, "Dimension X of relation " + x + " should be less then sizeX=" + sizeX);
        // else indexOutOfBounds will be thrown from Java standart classes
        if (value) {
            relation[x].put(y);
        } else {
            relation[x].remove(y);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (04.08.01 7:12:40)
     * @return boolean
     * @param obj java.lang.Object
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this)
            return true;
        if (!(obj instanceof BinaryRelation)) {
            return false;
        }
        BinaryRelation other = (BinaryRelation) obj;
        if (other.getColCount() != this.getColCount()) {
            return false;
        }
        if (other.getRowCount() != this.getRowCount()) {
            return false;
        }
        for (int i = getRowCount(); --i >= 0;) {
            for (int j = getColCount(); --j >= 0;) {
                if (getRelationAt(i, j) != other.getRelationAt(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Insert the method's description here.
     * Creation date: (04.08.01 8:39:27)
     */
    public String toString() {
        StringWriter sw = new java.io.StringWriter();
        BinaryRelationUtils.logRelation(this, new PrintWriter(sw));
        return sw.getBuffer().toString();
    }


}