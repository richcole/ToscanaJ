/*
 * User: sergey
 * Date: Jan 16, 2002
 * Time: 9:34:41 PM
 */
package concept.context;

public interface ModifiableFragment extends Fragment {
    void copy(Fragment fragment);

    /**
     *   performs inplace and operation
     */
    void and(Fragment set);

    /**
     *      performs inplace andNot operation
     */
    void andNot(Fragment set);

}
