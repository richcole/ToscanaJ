package concept.context;

public interface ConceptEnumCallback {

//------------------------------------------
    /*****************************************
     * this function is called for adding new
     * concept to concept lattice
     * @param obj - set of objects of concept
     * @param attr - set of attributes of concept
     *****************************************/
    void addConcept(Set obj, Set attr);


//------------------------------------------
    /*****************************************
     * this function is called at the end of
     * calculation of concept set
     *****************************************/
    void finishCalc();


//-----------------------------------------
    /*****************************************
     * this function is called at the beginning of
     * calculation of concept set
     *****************************************/
    void startCalc();

    /**
     * Insert the method's description here.
     * Creation date: (14.07.01 20:05:08)
     * @param rel concept.context.BinaryRelation
     */
    void setRelation(BinaryRelation rel);
}