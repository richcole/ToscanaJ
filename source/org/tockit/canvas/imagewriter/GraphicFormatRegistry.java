/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the available graphic formats.
 *
 * All derivates of ImageWriter should register their supported graphic types
 * with the method registerType(GraphicType), these types can then be
 * retrieved either with getIterator() or by querying for a specific type
 * supporting an extension using getTypeForFile(String).
 *
 * This class has no instances, only static methods.
 */
public class GraphicFormatRegistry {
    /**
     * The list of graphic types known.
     */
    static private List formats = new LinkedList();

    /**
     * No instances.
     */
    private GraphicFormatRegistry() {
    }

    /**
     * Registers a new graphic type.
     */
    static public void registerType(GraphicFormat format) {
        formats.add(format);
    }

    /**
     * Gives an iterator iterating on all types.
     */
    static public Iterator getIterator() {
        return formats.iterator();
    }

    /**
     * Returns a format supporting the given file name.
     *
     * The criterion is here just the extension, i.e. everything behind the last
     * dot.
     *
     * The return value can be null if no such type can be found. If multiple types support
     * this extension only the first one (in order of registration) will be given.
     */
    static public GraphicFormat getTypeByExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        String extension = fileName.substring(lastDot + 1);
        Iterator it = formats.iterator();
        while (it.hasNext()) {
            GraphicFormat format = (GraphicFormat) it.next();
            String[] extensions = format.getExtensions();
            for (int i = 0; i < extensions.length; i++) {
                if (extension.equals(extensions[i])) {
                    return format;
                }
            }
        }
        return null;
    }

    /**
     * Convenience method calling the string version.
     *
     * @see #getTypeByExtension(String)
     */
    static public GraphicFormat getTypeByExtension(File file) {
        return getTypeByExtension(file.getName());
    }
}