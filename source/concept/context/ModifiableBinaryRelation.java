/*
 * Created by IntelliJ IDEA.
 * User: sergey
 * Date: Dec 16, 2001
 * Time: 1:49:31 AM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package concept.context;

public interface ModifiableBinaryRelation extends BinaryRelation {
    void clearRelation();

    void removeCol(int col);

    void removeRow(int row);

    void setDimension(int rows, int cols);

    /**
     set value of cell with coordinates (x, y)
     @param x - x  coordinate
     @param y - y coordinate
     @param value
     */
    void setRelationAt(int x, int y, boolean value);
    //ModifiableBinaryRelation

    ModifiableSet getModifiableSet(int j);
}
