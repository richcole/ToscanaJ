package net.sourceforge.toscanaj.parser;

/**
 * Signals that a file could not be read due to errors in the file itself.
 */
public class DataFormatException extends Exception {
    /**
     * This can be used to get the original Exception.
     */
    private Exception exception = null;

    /**
     * Constructs an exception without detail message.
     */
    public DataFormatException() {
        super();
    }

    /**
     * Constructs an exception with detail message.
     */
    public DataFormatException(String s) {
        super(s);
    }

    /**
     * Constructs an exception with detail message and embedded exception.
     */
    public DataFormatException(String s, Exception e) {
        super(s);
        exception = e;
    }

    /**
     * Returns the originial exception if attached.
     */
    public Exception getOriginal() {
        return exception;
    }
}
