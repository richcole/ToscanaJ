package net.sourceforge.toscanaj.controller.db;

/**
 * Signals a problem with a database connection.
 *
 * There is a large number of reasons why this can happen, the message string
 * and the embedded exception are used to indicate what exactly went wrong.
 *
 * @todo build hierarchy for this.
 */
public class DatabaseException extends Exception
{
    /**
     * This can be used to get the original Exception.
     */
    private Exception exception = null;

    /**
     * Constructs an exception without detail message.
     */
    public DatabaseException() {
        super();
    }

    /**
     * Constructs an exception with detail message.
     */
    public DatabaseException(String s) {
        super(s);
    }

    /**
     * Constructs an exception with detail message and embedded exception.
     */
    public DatabaseException(String s, Exception e) {
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
