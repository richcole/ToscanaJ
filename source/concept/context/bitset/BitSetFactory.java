package concept.context.bitset;

import concept.context.ContextFactory;
import concept.context.ModifiableBinaryRelation;
import concept.context.ModifiableSet;
import concept.context.SetRelation;

/**
 *  Description of the Class
 *
 *@author     Sergey
 *@created    24 N;L 2000 3.
 */
public class BitSetFactory implements ContextFactory {

    public ModifiableBinaryRelation createRelation(int rowCount, int colCount) {
        return new SetRelation(rowCount, colCount);
    }

    public ModifiableSet createSet(int size) {
        return new BitSet(size);
    }
}