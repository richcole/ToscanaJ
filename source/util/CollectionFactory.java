/*
 * User: Serhiy Yevtushenko
 * Date: May 29, 2002
 * Time: 9:06:30 PM
 */
package util;

import java.util.*;

public class CollectionFactory {
    public static List createDefaultList(){
        return new ArrayList();
    }

    public static LinkedList createFastInsertDeleteList(){
        return new LinkedList();
    }

    public static Set createDefaultSet(){
        return new HashSet();
    }

    public static Map createDefaultMap(){
        return new HashMap();
    }

}
