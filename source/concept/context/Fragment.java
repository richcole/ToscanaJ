/*
 * User: sergey
 * Date: Jan 16, 2002
 * Time: 8:02:34 PM
 */
package concept.context;

public interface Fragment extends Cloneable {
    public Object clone();

    boolean intersects(Fragment other);

    boolean isEquals(Fragment obj);

    boolean isSupersetOf(Fragment other);

    boolean isSubsetOf(Fragment s);

    boolean isEmpty();

    ModifiableFragment makeModifiableFragment();
}
