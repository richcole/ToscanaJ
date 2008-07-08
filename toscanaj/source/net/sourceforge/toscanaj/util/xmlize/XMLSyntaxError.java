/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.xmlize;

public class XMLSyntaxError extends Exception {
    public XMLSyntaxError(final Throwable cause) {
        super(cause);
    }

    public XMLSyntaxError() {
        super();
    }

    public XMLSyntaxError(final String reason, final Throwable cause) {
        super(reason, cause);
    }

    public XMLSyntaxError(final String reason) {
        super(reason);
    }
}
