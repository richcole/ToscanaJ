package net.sourceforge.toscanaj.canvas.imagewriter;

import net.sourceforge.toscanaj.canvas.DrawingCanvas;

import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiWriter;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics2D;
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
 * This class offers only one static method to write a drawing canvas into a
 * bitmap or vector graphic file.
 */
public class ImageWriter {
    /**
     * No instances, use static method exportGraphic() instead.
     */
    private ImageWriter() {
    }

    /**
     * Saves a graphic using the given format, size and file.
     *
     * Allowed formats are FORMAT_PNG, FORMAT_JPG and FORMAT_SVG.
     */
    static public void exportGraphic(DrawingCanvas canvas, DiagramExportSettings settings, File outputFile)
           throws ImageGenerationException {
        String jimiName = null;
        if( settings.usesAutoMode() ) {
            // update information
            settings.setImageSize(canvas.getWidth(), canvas.getHeight());
            // use file name for Jimis auto-detect
            jimiName = outputFile.getName();
            // make sure that we get Batik if needed
            if(outputFile.getName().endsWith(".svg")) {
                settings.setGraphicFormat(DiagramExportSettings.FORMAT_SVG);
            }
        }
        else {
            // determine dummy name for Jimi
            if( settings.getGraphicFormat() == DiagramExportSettings.FORMAT_PNG ) {
                jimiName = "dummy.png";
            }
            else if( settings.getGraphicFormat() == DiagramExportSettings.FORMAT_JPG ) {
                jimiName = "dummy.jpg";
            }
        }
        if( settings.getGraphicFormat() == DiagramExportSettings.FORMAT_SVG ) {
            // use Batik
            // Get a DOMImplementation
            DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document
            Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
            svgGenerator.setSVGCanvasSize(new Dimension(settings.getImageWidth(),settings.getImageHeight()));

            // scale teh diagram to fit the image size
            double xScale = settings.getImageWidth()/(double)canvas.getWidth();
            double yScale = settings.getImageHeight()/(double)canvas.getHeight();
            svgGenerator.scale(xScale,yScale);

            // render the graphic into the DOM
            ToscanajGraphics2D tgraphics = new ToscanajGraphics2D( svgGenerator,
                                                                   canvas.getToscanaGraphics().getOffset(),
                                                                   canvas.getToscanaGraphics().getXScaling(),
                                                                   canvas.getToscanaGraphics().getYScaling() );
            canvas.paintCanvasItems(tgraphics);

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
        else {
            // use Jimi
            Image image = new BufferedImage(settings.getImageWidth(), settings.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = (Graphics2D) image.getGraphics();

            double xScale = settings.getImageWidth()/(double)canvas.getWidth();
            double yScale = settings.getImageHeight()/(double)canvas.getHeight();
            graphics2D.scale(xScale,yScale);

            ToscanajGraphics2D tgraphics = new ToscanajGraphics2D( graphics2D,
                                                                   canvas.getToscanaGraphics().getOffset(),
                                                                   canvas.getToscanaGraphics().getXScaling(),
                                                                   canvas.getToscanaGraphics().getYScaling() );

            // paint all items on canvas
            canvas.paintCanvasItems(tgraphics);
            try
            {
                JimiWriter writer = Jimi.createJimiWriter(jimiName);
                writer.setSource(image);
                FileOutputStream outStream = new FileOutputStream(outputFile);
                writer.putImage(outStream);
                outStream.close();
            }
            catch( JimiException e )
            {
                throw new ImageGenerationException( "Error while generating '" +
                    outputFile.getPath() + "' - Jimi error: "  + e.getMessage(), e );
            }
            catch( FileNotFoundException e )
            {
                throw new ImageGenerationException( "Error while generating '" +
                    outputFile.getPath() + "' - not found ??? "  + e.getMessage(), e );
            }
            catch( IOException e )
            {
                throw new ImageGenerationException( "Error while generating '" +
                    outputFile.getPath() + "' - IO problem: "  + e.getMessage(), e );
            }
        }
    }
}