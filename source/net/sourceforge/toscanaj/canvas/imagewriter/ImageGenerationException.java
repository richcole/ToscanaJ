/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.imagewriter;

/**
 * Signals that an image could not be created.
 *
 * There is a large number of reasons why this can happen, the message string
 * and the embedded exception are used to indicate what exactly went wrong.
 *
 * @todo build hierarchy for this.
 */
public class ImageGenerationException extends Exception
{
    /**
     * This can be used to get the original Exception.
     */
    private Exception exception = null;

    /**
     * Constructs an exception without detail message.
     */
    public ImageGenerationException() {
        super();
    }

    /**
     * Constructs an exception with detail message.
     */
    public ImageGenerationException(String s) {
        super(s);
    }

    /**
     * Constructs an exception with detail message and embedded exception.
     */
    public ImageGenerationException(String s, Exception e) {
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
