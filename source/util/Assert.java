package util;

public class Assert {

    public static void isTrue(boolean condition) {
        if (!condition) {
            throw new ApplicationException();
        }
    }

    public static void isTrue(boolean condition, String string) {
        if (!condition) {
            throw new ApplicationException(string);
        }
    }

    public static void notImplemented() {
        Assert.isTrue(false, "Method not yet implemented");
    }
}