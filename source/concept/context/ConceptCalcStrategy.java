package concept.context;

/**
 *  Description of the Interface
 *
 *@author     Sergey
 *@created    8 ���� 2000 �.
 */
public interface ConceptCalcStrategy extends BinaryRelationProcessor {

    public void setCallback(ConceptEnumCallback _callback);

    public void calculateConceptSet();
}