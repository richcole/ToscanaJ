package concept.context.tests;

import com.mockobjects.util.AssertMo;


/**
 * Insert the type's description here.
 * Creation date: (08.07.01 3:35:45)
 * @author:
 */
public class UniqueExpectationSet extends com.mockobjects.ExpectationSet {
    /**
     * UniqueExpectationSet constructor comment.
     * @param name java.lang.String
     */
    public UniqueExpectationSet(String name) {
        super(name);
    }


    public void addActual(Object actualItem) {
        AssertMo.assertEquals("Item " + actualItem + " already was added", false, getActualCollection().contains(actualItem));
        super.addActual(actualItem);
    }
}