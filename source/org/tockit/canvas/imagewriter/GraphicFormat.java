/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

/**
 * This represents a graphic format for export.
 *
 * Instances of derived classes can be used to represent different graphic formats,
 * they store the name, file extensions and the writer for a specific graphic
 * format.
 *
 * The different formats are stored in the GraphicFormatRegistry.
 *
 * @see ImageWriter
 * @see GraphicFormatRegistry
 */
public abstract class GraphicFormat {
    /**
     * Returns the name used for display purposes.
     */
    abstract public String getName();

    /**
     * Returns the list of usual extensions, the first one being the default.
     */
    abstract public String[] getExtensions();

    /**
     * Gives the writer to use for creating one of these images.
     */
    abstract public ImageWriter getWriter();

    /**
     * Returns the name of the format.
     */
    public String toString() {
        return getName();
    }
}