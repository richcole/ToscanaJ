package util;

// Generated by Together

public class Assert {

    public static void assert(boolean condition) {
        if (!condition) {
            throw new ApplicationException();
        }
    }

    public static void assert(boolean condition, String string) {
        if (!condition) {
            throw new ApplicationException(string);
        }
    }

    public static void notImplemented() {
        Assert.assert(false, "Method not yet implemented");
    }
}