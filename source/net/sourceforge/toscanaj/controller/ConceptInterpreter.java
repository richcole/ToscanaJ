/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 30, 2002
 * Time: 5:17:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.controller;

import org.w3c.dom.Node;

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
    public List getObjectSet(Concept concept);

    /** @note is Dependent on displayMode and filterMode */
    public List getAttributeSet(Concept concept);

    /** @note is Dependent on displayMode and filterMode */
    public int  getObjectCount(Concept concept);

    /** @note is Dependent on displayMode and filterMode */
    public int  getAttributeCount(Concept concept);

    /** @note these are independent of displayMode and dependent on filterMode */
    public float getRelativeIntentSize(Concept concept);

    /** @note these are independent of displayMode and dependent on filterMode */
    public float getRelativeExtentSize(Concept concept);

    /** @note see equation above for the effect of this change */
    public void setDisplayMode(boolean isContingent);

    /** @note see equation above for the effect of this change */
    public void setFilterMode(boolean isContingent);

    /** Constant value which may be used to set displayMode or filterMode */
    const boolean CONTINGENT = true;

    /** Constant value which may be used to set displayMode or filterMode */
    const boolean EXTENT = false;

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
