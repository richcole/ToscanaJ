/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import java.io.File;
import java.util.Iterator;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;

/**
 * Copy from org.tockit.canvas.imagewriter to use ConfigurationManager.
 * 
 * @todo move ConfigurationManager or similar functionality into Tockit\
 * and use this instead. At the moment everything is duplicated.
 */
public class DiagramExportSettings extends org.tockit.canvas.imagewriter.DiagramExportSettings {

	private static final String CONFIGURATION_SECTION_NAME = "DiagramExportSettings";

    private static final String CONFIGURATION_AUTO_MODE_ENTRY = "autoMode";
	private static final String CONFIGURATION_IMAGE_HEIGHT_ENTRY = "imageHeight";
	private static final String CONFIGURATION_IMAGE_WIDTH_ENTRY = "imageWidth";
	private static final String CONFIGURATION_COMMENTS_TO_CLIPBOARD_ENTRY = "saveCommentsToClipboard";
	private static final String CONFIGURATION_COMMENTS_TO_FILE_ENTRY = "saveCommentsToFile";
	private static final String CONFIGURATION_LAST_EXPORT_FILE_ENTRY = "lastImageExport";
	/**
     * Stores the type of graphic format to be used.
     */
    private GraphicFormat format;

    /**
     * Stores the target width.
     */
    private int width;

    /**
     * Stores the target width.
     */
    private int height;

    /**
     * Stores if the settings should be updated automatically.
     */
    private boolean autoMode;

	/**
	 * Stores if the history comments should be saved to a text file.
	 */
	private boolean saveCommentsToFile;

	/**
	 * Stores if the history comments should be copied to a clipboard.
	 */
	private boolean saveCommentsToClipboard;

	/**
	 * Stores the last exported file
	 */
	private File lastImageExportFile;

	/**
	 * Initialisation constructor.
	 *
	 * If autoMode is set, the other values will be overwritten each time a
	 * diagram gets exported.
	 */
	public DiagramExportSettings() {
		super(null,0,0,false);
		String lastImage = ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME, CONFIGURATION_LAST_EXPORT_FILE_ENTRY, null);
		if (lastImage != null) {
			this.format = GraphicFormatRegistry.getTypeByExtension(lastImage);
			this.lastImageExportFile = new File(lastImage);
		} else { 
		//no last image file so we get the first format from the graphic format registry
			Iterator formatIterator = GraphicFormatRegistry.getIterator();
			if (formatIterator.hasNext()){
				this.format = (GraphicFormat) formatIterator.next();	
			}
			// can't find last image, so set last image file to null.
			this.lastImageExportFile = null; 
		}
		this.width = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_IMAGE_WIDTH_ENTRY, 500);
		
		this.height = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_IMAGE_HEIGHT_ENTRY, 400);
		int exportAutoMode = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_AUTO_MODE_ENTRY, 1); 
		this.autoMode = true;
		if( exportAutoMode == 0 ) {
			this.autoMode = false;
		}	
		int saveCommentsToFileTrue = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_COMMENTS_TO_FILE_ENTRY, 0);
		this.saveCommentsToFile = false;
		if( saveCommentsToFileTrue == 1) {
			this.saveCommentsToFile= true;	 
		}
		int saveCommentsToClipboardTrue = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_COMMENTS_TO_CLIPBOARD_ENTRY, 0);
		this.saveCommentsToClipboard= false;
		if( saveCommentsToClipboardTrue == 1 ){
			this.saveCommentsToClipboard= true;
		}
	}

		
    /**
     * Initialisation constructor.
     *
     * If autoMode is set, the other values will be overwritten each time a
     * diagram gets exported.
     */
    public DiagramExportSettings(GraphicFormat format, int width, int height, boolean autoMode) {
    	super(format, width, height, autoMode);
        this.format = format;
        this.width = width;
        this.height = height;
        this.autoMode = autoMode;
		this.saveCommentsToFile= false;
		this.saveCommentsToClipboard= false;
		String lastImage = ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME, CONFIGURATION_LAST_EXPORT_FILE_ENTRY, null);
		this.lastImageExportFile = new File(lastImage);  
    }

    /**
     * Sets the graphic format to be used when exporting.
     */
    public void setGraphicFormat(GraphicFormat format) {
        this.format = format;
    }

    /**
     * Sets the size of the image to be created.
     */
    public void setImageSize(int width, int height) {
        this.width = width;
        this.height = height;
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_IMAGE_WIDTH_ENTRY, width);
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_IMAGE_HEIGHT_ENTRY, height);
    }

    /**
     * Sets the auto mode.
     *
     * If autoMode is set, the other values will be overwritten each time a
     * diagram gets exported.
     */
    public void setAutoMode(boolean mode) {
        this.autoMode = mode;
		int exportAutoMode = 1;
		if( mode == false ) {
			exportAutoMode = 0;
		}
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_AUTO_MODE_ENTRY, exportAutoMode); 
    }
    
    /**
     * Get the current image format
     */
    public GraphicFormat getGraphicFormat() {
        return this.format;
    }

    /**
     * Get the current image width
     */
    public int getImageWidth() {
        return this.width;
    }

    /**
     * Get the current image height
     */
    public int getImageHeight() {
        return this.height;
    }

    /**
     * Returns true if settings should be updated automatically.
     */
    public boolean usesAutoMode() {
        return this.autoMode;
    }
	
    /**
     * Debugging output.
     */
    public String toString() {
        String retVal = "DiagramExportSettings[ Size: (" + this.width + ", " + this.height + "), Format: " + this.format;
        if (this.autoMode) {
            retVal += " (AUTO)";
        }
        retVal += "]\n";
        return retVal;
    }
	/**
	 * Returns true if the history comments are to be saved to a file.
	 * @return boolean
	 */
	public boolean getSaveCommentsToFile() {
		return saveCommentsToFile;
	}

	/**
	 * Returns true if the history comments are to be saved to the clipboard.
	 * @return boolean
	 */
	public boolean getSaveCommentToClipboard() {
		return saveCommentsToClipboard;
	}

	/**
	 * Sets whether to save the history comments to a file.
	 * @param saveCommentsToFile The saveCommentsToFile to set
	 */
	public void setSaveCommentsToFile(boolean saveToFile) {
		this.saveCommentsToFile = saveToFile;
		int saveFile = 0; //false
		if(saveToFile == true){
			saveFile = 1;
		}
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_COMMENTS_TO_FILE_ENTRY,saveFile);
	}

	/**
	 * Sets whether to save the history comments to the system clipboard.
	 * @param saveCommentToClipboard The saveCommentToClipboard to set
	 */
	public void setSaveCommentToClipboard(boolean saveCommentToClipboard) {
		this.saveCommentsToClipboard = saveCommentToClipboard;
		int saveToClipboard = 0; //false
		if(saveCommentToClipboard == true){
			saveToClipboard = 1;
		}
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_COMMENTS_TO_CLIPBOARD_ENTRY, saveToClipboard);
	}

	/**
	 * Gets the last exported image File.
	 * @return File
	 */
	public File getLastImageExportFile() {
		return lastImageExportFile;
	}

	/**
	 * Sets the last exported image file.
	 * @param lastImageExportFile The lastImageExportFile to set
	 */
	public void setLastImageExportFile(File lastImageExportFile) {
		this.lastImageExportFile = lastImageExportFile;
		ConfigurationManager.storeString(CONFIGURATION_SECTION_NAME, CONFIGURATION_LAST_EXPORT_FILE_ENTRY, lastImageExportFile.getAbsolutePath());
	}

}

