package net.sourceforge.toscanaj.canvas.imagewriter;

import net.sourceforge.toscanaj.canvas.DrawingCanvas;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiWriter;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

/**
 * Saves a DrawingCanvas as SVG graphic.
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
    public void exportGraphic(DrawingCanvas canvas, DiagramExportSettings settings, File outputFile)
           throws ImageGenerationException {
        if( settings.usesAutoMode() ) {
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
        svgGenerator.setSVGCanvasSize(new Dimension(settings.getImageWidth(),settings.getImageHeight()));
        Rectangle2D bounds = new Rectangle2D.Double(
                                    0, 0, settings.getImageWidth(), settings.getImageHeight() );

        svgGenerator.setPaint(canvas.getBackground());
        svgGenerator.fill(bounds);

        canvas.scaleToFit(svgGenerator, bounds);

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
        }
        catch(Exception e) {
            throw new ImageGenerationException( "Error while generating '" +
                outputFile.getPath() + "' - writing SVG error: "  + e.getMessage(), e );
        }
    }
}