/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.tockit.canvas.Canvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Saves a Canvas as SVG graphic.
 */
public class BatikImageWriter implements ImageWriter {
    /**
     * A format representing SVG.
     */
    static protected class GraphicFormatSVG extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Scalable Vector Graphics";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            String[] retVal = new String[1];
            retVal[0] = "svg";
            return retVal;
        }

        /**
         * Implements GraphicFormat.getWriter().
         */
        public ImageWriter getWriter() {
            return singleton;
        }
    }

    /**
     * The only instance of this class.
     */
    static private BatikImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private BatikImageWriter() {
    }

    /**
     * Registers our graphic format and sets up the instance.
     */
    static public void initialize() {
        singleton = new BatikImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatSVG());
    }

    /**
     * Saves the canvas using the settings to the file.
     */
    public void exportGraphic(Canvas canvas, DiagramExportSettings settings, File outputFile)
            throws ImageGenerationException {
        if (settings.usesAutoMode()) {
            // update information
            settings.setImageSize(canvas.getWidth(), canvas.getHeight());
        }
        // use Batik
        // Get a DOMImplementation
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new Dimension(settings.getImageWidth(), settings.getImageHeight()));
        Rectangle2D bounds = new Rectangle2D.Double(
                0, 0, settings.getImageWidth(), settings.getImageHeight());

        svgGenerator.setPaint(canvas.getBackground());
        svgGenerator.fill(bounds);

        AffineTransform transform = canvas.scaleToFit(svgGenerator, bounds);
        svgGenerator.transform(transform);

        // render the graphic into the DOM
        canvas.paintCanvas(svgGenerator);

        // Finally, stream out SVG to the standard output using UTF-8
        // character to byte encoding
        boolean useCSS = true; // we want to use CSS style attribute
        try {
            FileOutputStream outStream = new FileOutputStream(outputFile);
            Writer out = new OutputStreamWriter(outStream, "UTF-8");
            svgGenerator.stream(out, useCSS);
            outStream.close();
        } catch (Exception e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - writing SVG error: " + e.getMessage(), e);
        }
    }
}