package util;

public class ApplicationException extends RuntimeException {
    public ApplicationException() {
    }

    public ApplicationException(String s) {
        super(s);
    }
}