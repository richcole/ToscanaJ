package util;

/**
 * Insert the type's description here.
 * Creation date: (19.04.01 22:34:28)
 * @author:
 */
public class MemoryUtil {
    //---------------------------------------------------------------
    /**
     *  Description of the Method
     */
    public static void fullGc() {
        Runtime rt = Runtime.getRuntime();
        long isFree = rt.freeMemory();
        long wasFree;
        do {
            wasFree = isFree;
            rt.gc();
            isFree = rt.freeMemory();
        } while (isFree > wasFree);
        rt.runFinalization();
    }

    /**
     * Insert the method's description here.
     * Creation date: (13.07.01 10:28:43)
     * @return long
     */
    public static long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}