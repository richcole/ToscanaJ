/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 30, 2002
 * Time: 5:17:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.List;

import net.sourceforge.toscanaj.model.lattice.Concept;


/*
 * & - intersection
 * | - union
 * &x:X - intersection with x ranging over X
 *
 * i - inner concept
 * z - zoomed concept
 * n - nested concept
 *
 * interp(i,Z,N) = interp(i,displayMode) &
 *    &n:N interp(n,CONTINGENT) &z:Z interp(z,filterMode)
 *
 * The above equation gives a formular for the interpretation
 * of a concept, i which exists in a nesting N and a zooming Z.
 * N and Z are considered sequences.
 *
 * The above equation is manifest in the program by constucting
 * new concept interpreters using the functions:
 *
 *     createNestedInterpreter
 *     createFilteredInterperter
 */

public interface ConceptInterpreter {

    /** @note is Dependent on displayMode and filterMode */
    List getObjectSet(Concept concept);

    /** @note is Dependent on displayMode and filterMode */
    List getAttributeSet(Concept concept);

    /** @note is Dependent on displayMode and filterMode */
    int  getObjectCount(Concept concept);

    /** @note is Dependent on displayMode and filterMode */
    int  getAttributeCount(Concept concept);

    /** @note these are independent of displayMode and dependent on filterMode */
    float getRelativeIntentSize(Concept concept);

    /** @note these are independent of displayMode and dependent on filterMode */
    float getRelativeExtentSize(Concept concept);

    /** @note see equation above for the effect of this change */
    void setDisplayMode(boolean isContingent);

    /** @note see equation above for the effect of this change */
    void setFilterMode(boolean isContingent);

    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean CONTINGENT = true;

    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean EXTENT = false;

    /**
     * Construct a new interpreter that adds concept to N.
     * The new interpreter doesn't reference the old interpreter.
     */
    public ConceptInterpreter createNestedInterpreter(Concept concept);

    /* Construct a new interpreter that adds concept to Z.
     * The new interpreter doesn't reference the old interpreter.
     */
    public ConceptInterpreter createFilteredInterpreter(Concept concept);
}
