/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

/**
 * Signals that a file could not be read due to errors in the file itself.
 */
public class DataFormatException extends Exception {
    public DataFormatException() {
        super();
    }

    public DataFormatException(final String message) {
        super(message);
    }

    public DataFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DataFormatException(final Throwable cause) {
        super(cause);
    }
}
