/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;


public class PreconditionFailedException
        extends Exception {
    public PreconditionFailedException(String reason) {
        super(reason);
    }
}
