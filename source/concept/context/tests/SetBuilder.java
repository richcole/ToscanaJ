package concept.context.tests;

import concept.context.Context;
import concept.context.ContextFactoryRegistry;
import concept.context.ModifiableBinaryRelation;
import concept.context.ModifiableSet;
import util.Assert;

/**
 * Creation date: (22.02.01 21:57:47)
 * @author: Sergey Yevtsuhenko
 */
public class SetBuilder {
    public static ModifiableBinaryRelation makeRelation(int[][] arrRelation) {

        ModifiableBinaryRelation rel;
        if (arrRelation.length > 0) {
            rel = ContextFactoryRegistry.createRelation(arrRelation.length, arrRelation[0].length);
            for (int i = arrRelation.length; --i >= 0;) {
                rel.getModifiableSet(i).or(makeSet(arrRelation[i]));
            }
        } else {
            rel = ContextFactoryRegistry.createRelation(0, 0);
        }

        return rel;
    }

    public static ModifiableSet makeSet(int[] arSet) {
        ModifiableSet set = ContextFactoryRegistry.createSet(arSet.length);
        for (int i = arSet.length; --i >= 0;) {
            if (arSet[i] != 0) {
                set.put(i);
            }
        }
        return set;
    }

    public static Context makeContext(int[][] relation) {
        final Context ret = new Context(makeRelation(relation));
        return ret;
    }

    public static Context makeContext(String[] objectNames, String[] attrNames, int[][] relation) {
        Context ret = makeContext(relation);
        Assert.isTrue(ret.getObjectCount() == objectNames.length);
        for (int i = 0; i < objectNames.length; i++) {
            ret.getObject(i).setName(objectNames[i]);
        }
        Assert.isTrue(ret.getAttributeCount() == attrNames.length);
        for (int i = 0; i < attrNames.length; i++) {
            ret.getAttribute(i).setName(attrNames[i]);
        }
        return ret;
    }


    public static UniqueExpectationSet makeExpectationSetForIntents(String name, int[][] arrContext) {
        UniqueExpectationSet ret = new UniqueExpectationSet(name);
        for (int i = arrContext.length; --i >= 0;) {
            ret.addExpected(makeSet(arrContext[i]));
        }
        return ret;
    }

}