/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

/**
 * Denotes an exception converting between DataValues or from Strings.
 * 
 * This is similar to an IllegalArgumentException, but checked.
 */
public class ConversionException extends Exception {
    public ConversionException() {
        super();
    }

    public ConversionException(final String message) {
        super(message);
    }

    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConversionException(final Throwable cause) {
        super(cause);
    }
}
