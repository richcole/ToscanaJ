package concept.context.enumcallbacks;

import concept.context.ConceptEnumCallback;
import concept.context.Set;

/**
 * Insert the type's description here.
 * Creation date: (14.07.01 19:32:49)
 * @author:
 */
public class DefaultConceptEnumCallback implements ConceptEnumCallback {

    /*****************************************
     * this function is called for adding new
     * concept to concept lattice
     * @param obj - set of objects of concept
     * @param attr - set of attributes of concept
     *****************************************/
    public void addConcept(Set obj, Set attr) {
    }


    /*****************************************
     * this function is called at the end of
     * calculation of concept set
     *****************************************/
    public void finishCalc() {
    }


    /**
     * Insert the method's description here.
     * Creation date: (14.07.01 20:05:23)
     * @param rel concept.context.BinaryRelation
     */
    public void setRelation(concept.context.BinaryRelation rel) {
    }


    /*****************************************
     * this function is called at the beginning of
     * calculation of concept set
     *****************************************/
    public void startCalc() {
    }
}