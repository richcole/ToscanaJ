package concept.context;

public interface BinaryRelation {

    int getRowCount();

    int getColCount();

    /**
     gets value of cell with coordinates (x, y)
     @param x - x  coordinate
     @param y - y coordinate
     @return value
     */

    boolean getRelationAt(int row, int col);

    /**
     returns Set of values
     @param row
     */
    Set getSet(int row);

    boolean equals(Object other);

    ModifiableBinaryRelation makeModifiableCopy();
}