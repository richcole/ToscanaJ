/*
 * Created by IntelliJ IDEA.
 * User: Serhiy Yevtushenko
 * Date: May 12, 2002
 * Time: 12:59:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package util;

public class DoubleUtil {
    public static double getRate(int nominator, int denominator) {
        if (denominator == 0) {
            return 0;
        }
        return (double) nominator / (double) denominator;
    }

}
