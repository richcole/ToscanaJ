/*
 * Created by IntelliJ IDEA.
 * User: Serhiy Yevtushenko
 * Date: 22.04.2002
 * Time: 1:23:50
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package concept.context;

import util.Assert;


public class ContextFunctions {
    //TODO: think about variant with attribute mask
    public static int stability(Set s, Context cxt) {

        BinaryRelation rel = cxt.getRelation();
        final int colCount = rel.getColCount();
        int[] attributeStabilities = new int[colCount];
        final int rowCount = rel.getRowCount();
        boolean isFull = (s.elementCount() == colCount);

        if (isFull) {
            int extentSize = 0;
            for (int i = 0; i < rowCount; i++) {
                if (rel.getSet(i).equals(s)) {
                    extentSize++;
                }
                ;
            }
            return extentSize;
        }

        for (int i = 0; i < rowCount; i++) {
            Set currObj = rel.getSet(i);
            if (s.isSubsetOf(currObj)) {
                for (int j = 0; j < colCount; j++) {
                    if (!currObj.in(j)) {
                        attributeStabilities[j]++;
                    }
                }
            }
        }
        int minStability = 0;
        for (int j = 0; j < colCount; j++) {
            if (!s.in(j)) {
                if (minStability == 0) {
                    minStability = attributeStabilities[j];
                } else if (minStability > attributeStabilities[j]) {
                    minStability = attributeStabilities[j];
                }
            }
        }
        return minStability;
    }

    public static int idealSize(Set queryIntent, Context cxt, Set attributeMask) {
        Assert.assert(queryIntent.size() == attributeMask.size());
        Assert.assert(queryIntent.isSubsetOf(attributeMask));
        BinaryRelation relation = cxt.getRelation();
        ModifiableSet temp = ContextFactoryRegistry.createSet(queryIntent.size());
        int ret = 0;
        for(int i=0; i<relation.getRowCount(); i++){
            temp.copy(relation.getSet(i));
            temp.and(attributeMask);
            if(queryIntent.isSubsetOf(temp)){
               ret++;
            }
        }
        return ret;
    }

    public static int contingentSize(Set queryIntent, Context cxt, Set attributeMask){
        BinaryRelation relation = cxt.getRelation();
        ModifiableSet temp = ContextFactoryRegistry.createSet(queryIntent.size());
        int ret = 0;
        for(int i=0; i<relation.getRowCount(); i++){
            temp.copy(relation.getSet(i));
            temp.and(attributeMask);
            if(queryIntent.isEquals(temp)){
               ret++;
            }
        }
        return ret;
    }

}
