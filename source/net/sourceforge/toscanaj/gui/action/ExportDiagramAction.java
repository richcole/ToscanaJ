/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.gui.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.DiagramExportSettings;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;

import java.awt.Frame;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class ExportDiagramAction extends KeyboardMappedAction {
	private File lastImageExportFile;
	private DiagramExportSettings diagramExportSettings;
	private DiagramView diagramView;
	private Frame frame;
	 
	
	/**
	 *  @note
	 *     If you don't want to specify mnemonics
	 *     then use the other constructor.
	 * @todo if you want another conmbination then write another constructor.
	 */
	public ExportDiagramAction (
			Frame frame,
			DiagramExportSettings diagExpSettings,
			DiagramView diagramView) {
		super(frame, "Export Diagram...");
		this.lastImageExportFile = diagExpSettings.getLastImageExportFile();
		this.diagramExportSettings = diagExpSettings;
		this.diagramView = diagramView;
		this.frame = frame;
	}

	public ExportDiagramAction (
			Frame frame,
			DiagramExportSettings diagExpSettings,
			DiagramView diagramView,
			int mnemonic,
			KeyStroke keystroke
			) {
		super(frame, "Export Diagram...", mnemonic, keystroke);
		this.lastImageExportFile = diagExpSettings.getLastImageExportFile();
		this.diagramExportSettings = diagExpSettings;
		this.diagramView = diagramView;
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent e) {
		exportImage();
	}

	public void exportImage() {
	    if (this.lastImageExportFile == null) {
	        this.lastImageExportFile =
	            new File(System.getProperty("user.dir"));
	    }

	    if(this.diagramExportSettings.usesAutoMode()) {
	        exportImageWithAutoMode();
	    } else {
	        exportImageWithManualMode();
	    }
	}
	
	private void exportImageWithAutoMode() {
		final CustomJFileChooser saveDialog =
			new CustomJFileChooser(this.lastImageExportFile);
		// populate the file extension combo box in the dialog
		// with all the possible file formats 
		FileFilter defaultFilter = saveDialog.getFileFilter();
		Iterator formatIterator = GraphicFormatRegistry.getIterator();
		while (formatIterator.hasNext()) {
			GraphicFormat graphicFormat = (GraphicFormat) formatIterator.next();
			ExtensionFileFilter fileFilter = new ExtensionFileFilter(graphicFormat.getExtensions(),graphicFormat.getName());
			saveDialog.addChoosableFileFilter(fileFilter);
			if(graphicFormat == this.diagramExportSettings.getGraphicFormat()) {
				defaultFilter = fileFilter;
			}
		}
		saveDialog.setFileFilter(defaultFilter);
		// Check if user keys in an extension. If not, add it automatically 
		// according to the file type selected
		boolean formatDefined;
		do {
			formatDefined = true;
			int rv = saveDialog.showSaveDialog(frame);
			if (rv == JFileChooser.APPROVE_OPTION) {
				File selectedFile = saveDialog.getSelectedFile();
				FileFilter fileFilter = saveDialog.getFileFilter();
				if(fileFilter instanceof ExtensionFileFilter) {
					ExtensionFileFilter extFileFilter = (ExtensionFileFilter) fileFilter;
					String[] extensions = extFileFilter.getExtensions();
					if (selectedFile.getName().indexOf('.') == -1) {
						selectedFile = new File(selectedFile.getAbsolutePath() + "." + extensions[0]);
					}
				}
				GraphicFormat gFormat =
					GraphicFormatRegistry.getTypeByExtension(
						selectedFile);
				if (gFormat != null) {
					this.diagramExportSettings.setGraphicFormat(gFormat);
				} else {
					if(selectedFile.getName().indexOf('.') != -1) {
						JOptionPane.showMessageDialog(
							frame,
							"Sorry, no type with this extension known.\n"
								+ "Please use either another extension or try\n"
								+ "manual settings.",
							"Export failed",
							JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(
							frame,
							"No extension given.\n" +
							"Please give an extension or pick a file type\n" +
							"from the options.",
							"Export failed",
							JOptionPane.ERROR_MESSAGE);
					}	
					formatDefined = false;
				}

				if (formatDefined) {
					exportImage(selectedFile);
				}
			}
		} while (formatDefined == false);
	}
 
	private void exportImageWithManualMode() {
		GraphicFormat format = this.diagramExportSettings.getGraphicFormat();
		final CustomJFileChooser saveDialog =
			new CustomJFileChooser(this.lastImageExportFile);
		ExtensionFileFilter manualFileFilter = new ExtensionFileFilter(format.getExtensions(),format.getName());
		saveDialog.addChoosableFileFilter(manualFileFilter);
		int rv = saveDialog.showSaveDialog(frame);
		if (rv == JFileChooser.APPROVE_OPTION) {
			File selectedFile = saveDialog.getSelectedFile();
			exportImage(selectedFile);
		}
	}
	
	private void exportImage(File selectedFile){
		try {
			// Get title of the current diagram
			String title = "";
			String description = "";
			String lineSeparator = System.getProperty("line.separator");
			DiagramController diagramController = DiagramController.getController();
		    DiagramHistory diagramHistory = diagramController.getDiagramHistory();
			if(diagramHistory.getNumberOfCurrentDiagrams() != 0) {
				int numCurDiag = diagramHistory.getNumberOfCurrentDiagrams();
				int firstCurrentPos = diagramHistory.getFirstCurrentDiagramPosition();
				for(int i=0; i<numCurDiag; i++) { 
					title += diagramHistory.getElementAt(i+firstCurrentPos).toString();
					if( i < numCurDiag-1 ){
					title += " / ";
					}
					if ( (i == numCurDiag-1) && numCurDiag >1) {
						title += " ( Outer diagram / Inner diagram )";
					}
				}			
				// Get description of the diagrams
				description = diagramController.getDiagramHistory().getTextualDescription();
			} else {
				title = this.diagramView.getDiagram().getTitle();
			}
			// Set title and desc in properties
			Properties metadata = new Properties();
			metadata.setProperty("title", title);
			metadata.setProperty("description", description.trim());
			this
				.diagramExportSettings
				.getGraphicFormat()
				.getWriter()
				.exportGraphic(
				this.diagramView,
				this.diagramExportSettings,
				selectedFile,
				metadata);
				if(this.diagramExportSettings.getSaveCommentsToFile()==true){
					try{
						PrintWriter out = new PrintWriter(new FileWriter(new File(selectedFile.getAbsolutePath()+".txt")));
						out.println("The diagram(s) you have viewed for the resulting image: "+System.getProperty("line.separator")+selectedFile.getAbsolutePath());
						DateFormat dateFormatter = DateFormat.getDateTimeInstance();
						out.println("as at "+dateFormatter.format(new Date(System.currentTimeMillis()))+" is(are): ");
						out.println();
						out.println(description);
						out.close();
					}catch(IOException e){
						ErrorDialog.showError(frame, e.getMessage(), "Exporting text file error");
					}
				}
				if(this.diagramExportSettings.getSaveCommentToClipboard()==true){
					DateFormat dateFormatter = DateFormat.getDateTimeInstance();
					String header ="The diagram(s) you have viewed for the resulting image:\n"
					+selectedFile.getAbsolutePath()+"\n"
					+"as at "+dateFormatter.format(new Date(System.currentTimeMillis()))+" is(are): \n";
					StringSelection comments = new StringSelection(header + "\n" + description);
					Clipboard systemClipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
					systemClipboard.setContents(comments,null);
				}
		} catch (ImageGenerationException e) {
			ErrorDialog.showError(frame, e.getMessage(), "Exporting image error");
		} catch (OutOfMemoryError e) {
			ErrorDialog.showError(
				frame,
				"Out of memory",
				"Not enough memory available to export\n"
					+ "the diagram in this size");
		}
		this.diagramExportSettings.setLastImageExportFile(selectedFile);
		}
	
	/**
	 * The custom file chooser will check whether the file exists and shows
	 * the appropriate warning message (if applicable)
	 * 
	 */
	private class CustomJFileChooser extends JFileChooser{
		private CustomJFileChooser(File selectedFile){
			super(selectedFile);
		}
		public void approveSelection(){
			File selectedFile = getSelectedFile();
			if(selectedFile.getName().indexOf('.') == -1) { // check for extension
				// add default
				FileFilter filter = getFileFilter();
				if(filter instanceof ExtensionFileFilter) {
					ExtensionFileFilter extFileFilter = (ExtensionFileFilter) filter;
					String[] extensions = extFileFilter.getExtensions();
					selectedFile = new File(selectedFile.getAbsolutePath() + "."+extensions[0]);
					setSelectedFile(selectedFile);
				}	
			}
			if (selectedFile != null && selectedFile.exists()) {
				String warningMessage = "The image file '"	+ selectedFile.getName() + "' already exists.\nDo you want to overwrite the existing file?";
				if(diagramExportSettings.getSaveCommentsToFile()==true) {
					File textFile = new File(selectedFile.getAbsoluteFile()+".txt");
					if(textFile.exists()) {
					warningMessage = "The files '"	+ selectedFile.getName() + "' and '" + textFile.getName()+ "' already exist.\nDo you want to overwrite the existing files?";
					}
				}
				int response =
					JOptionPane.showOptionDialog(
						this,
						warningMessage,
						"File Export Warning: File exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,null,new Object[] {"Yes", "No"}, "No");
				if (response != JOptionPane.YES_OPTION) {
					return;
				}
			}
			if(selectedFile!=null && !selectedFile.exists() && diagramExportSettings.getSaveCommentsToFile()==true){
				File textFile = new File(selectedFile.getAbsoluteFile()+".txt");
				if(textFile.exists()) {
					int response =
						JOptionPane.showOptionDialog(
							this,
							"The text file '" + textFile.getName()
							+ "' already exists.\n"
							+"Do you want to overwrite the existing file?",
							"File Export Warning: File exists",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,null,new Object[] {"Yes", "No"}, "No");
						if (response != JOptionPane.YES_OPTION) {
							return;
						}
				}
			}
			super.approveSelection();
		}
	}

}
