package concept.context.enumcallbacks;

import concept.context.Set;

// Generated by Together

public class ConceptNumCallback extends DefaultConceptEnumCallback {
    int conCnt;

//-----------------------------------------------------------------
    public int getConceptCount() {
        return conCnt;
    }
//-----------------------------------------------------------------
    public void startCalc() {
        conCnt = 0;
    }


//-----------------------------------------------------------------
    public void addConcept(Set obj, Set attr) {
        conCnt++;
        //*DBG*/ System.out.println("Added concept j="+j+" ("+obj+"  " +attr+")");
    }
}