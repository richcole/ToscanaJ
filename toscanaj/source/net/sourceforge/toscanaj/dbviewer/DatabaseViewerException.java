/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

public class DatabaseViewerException extends Exception {
    /**
     * This can be used to get the original Exception.
     */
    private Exception exception = null;

    /**
     * Constructs an exception without detail message.
     */
    public DatabaseViewerException() {
        super();
    }

    /**
     * Constructs an exception with detail message.
     */
    public DatabaseViewerException(final String s) {
        super(s);
    }

    /**
     * Constructs an exception with detail message and embedded exception.
     */
    public DatabaseViewerException(final String s, final Exception e) {
        super(s);
        this.exception = e;
    }

    /**
     * Returns the originial exception if attached.
     */
    public Exception getOriginal() {
        return this.exception;
    }
}
