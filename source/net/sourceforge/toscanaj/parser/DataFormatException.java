package net.sourceforge.toscanaj.parser;

/**
 * Signals that a file could not be read due to errors in the file itself.
 */
public class DataFormatException extends Exception {
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
}
