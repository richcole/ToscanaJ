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

/**
 * This class can be used to save a DrawingCanvas to a bitmap file.
 */
public class JimiImageWriter implements ImageWriter {
    /**
     * A format representing PNG.
     */
    static protected class GraphicFormatPNG extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Portable Network Graphics";
        }
        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            String[] retVal = new String[1];
            retVal[0] = "png";
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
     * A format representing JPG.
     */
    static protected class GraphicFormatJPG extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Joint Picture Expert Group";
        }
        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            String[] retVal = new String[2];
            retVal[0] = "jpg";
            retVal[1] = "jpeg";
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
    static private JimiImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private JimiImageWriter() {
    }

    /**
     * Registers our graphic formats and sets up the instance.
     */
    static public void initialize() {
        singleton = new JimiImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatPNG());
        GraphicFormatRegistry.registerType(new GraphicFormatJPG());
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
        // determine dummy name for Jimi
        String jimiName = null;
        if( settings.getGraphicFormat() instanceof GraphicFormatPNG ) {
            jimiName = "dummy.png";
        }
        else if( settings.getGraphicFormat() instanceof GraphicFormatJPG ) {
            jimiName = "dummy.jpg";
        }
        else {
            throw new ImageGenerationException("Something went really wrong, call the doctor!");
        }
        // use Jimi
        Image image = new BufferedImage( settings.getImageWidth(), settings.getImageHeight(),
                                         BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();

        Rectangle2D bounds = new Rectangle2D.Double(
                                    0, 0, settings.getImageWidth(), settings.getImageHeight() );

        canvas.scaleToFit(graphics2D, bounds);

        // paint all items on canvas
        canvas.paintCanvas(graphics2D);
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
                outputFile.getPath() + "' - Jimi error " , e );
        }
        catch( FileNotFoundException e )
        {
            throw new ImageGenerationException( "Error while generating '" +
                outputFile.getPath() + "' - not found " , e );
        }
        catch( IOException e )
        {
            throw new ImageGenerationException( "Error while generating '" +
                outputFile.getPath() + "' - IO problem "  , e );
        }
    }
}