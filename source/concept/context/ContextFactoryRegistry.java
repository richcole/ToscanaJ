package concept.context;

/**
 * Insert the type's description here.
 * Creation date: (07.03.01 21:04:24)
 * @author:
 */
public class ContextFactoryRegistry {
    private static ContextFactory factory;

    /**
     *  Description of the Method
     *
     *@param  sizeX  Number of rows in relation
     *@param  sizeY  Number of columns in relation
     *@return        new  BinaryRelation
     */
    public static ModifiableBinaryRelation createRelation(int sizeX, int sizeY) {
        return getFactory().createRelation(sizeX, sizeY);
    }

    /**
     *  Description of the Method
     *
     *@param  size  maximum number of attributes in set
     *@return       new Set
     */
    public static ModifiableSet createSet(int size) {
        return getFactory().createSet(size);
    }

    /**
     * Insert the method's description here.
     * Creation date: (07.03.01 21:11:13)
     * @return concept.context.ContextFactory
     */
    private synchronized static ContextFactory getFactory() {
        if (null == factory) {
            factory = new concept.context.bitset.BitSetFactory();
        }
        return factory;
    }

}