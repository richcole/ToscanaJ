/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

public class DatabaseViewerInitializationException extends Exception {
    /**
     * This can be used to get the original Exception.
     */
    private Exception exception = null;

    /**
     * Constructs an exception without detail message.
     */
    public DatabaseViewerInitializationException() {
        super();
    }

    /**
     * Constructs an exception with detail message.
     */
    public DatabaseViewerInitializationException(String s) {
        super(s);
    }

    /**
     * Constructs an exception with detail message and embedded exception.
     */
    public DatabaseViewerInitializationException(String s, Exception e) {
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
