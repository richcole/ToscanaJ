/*
 * Created by IntelliJ IDEA.
 * User: sergey
 * Date: Dec 2, 2001
 * Time: 8:11:27 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package concept.context;

public interface ModifiableSet extends Set, ModifiableFragment {
//-------------------------
    /**
     *      puts element in set
     */

    void put(int elId);

//-------------------------
    /**
     * removes element from set
     *
     */
    void remove(int elId);

    /**
     *   removes all elements from set
     *
     */
    void clearSet();

    /**
     *   puts in set elements with id less and equal to till
     *
     */

    void fillByOne(int till);

    /**
     *   performs inplace or operation
     *
     */

    void or(Set set);


    /**
     * resizes set
     * Creation date: (22.07.01 0:05:07)
     * @param newSize int
     */
    void resize(int newSize);

    /**
     * removes element from set
     * and decreases set capacity
     */
    void exclude(int elId);

    /**
     * Insert the method's description here.
     * Creation date: (16.10.00 23:51:20)
     * @param addition concept.context.Set
     */
    void append(Set addition);
}
