/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.rdql;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.tupleware.model.TupleSet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;

public class RdfQueryDialog extends JDialog {
	private static final String CONFIGURATION_SECTION_NAME = "RdfQueryDialog";
	private static final String CONFIGURATION_FILE_STRING = "RDQL Query File";
	
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 350;
	
	private WizardPanel rdfQueryPanel;
	private WizardPanel openFilePanel;
	private JLabel stepLabel;
	private WizardPanel currentStep;
	private JButton nextButton;
	
	private Model rdfModel;
	private TupleSet tupleSet;

	abstract class WizardPanel extends JPanel {
		WizardPanel() {
			super();
			setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		}
		abstract void updateContents();
		abstract String getTitle();
		abstract String getNextButtonText();
		abstract boolean executeStep();
		abstract WizardPanel getNextPanel();
	}
    
	class RdfModelCreationPanel extends WizardPanel {
		private JTextField fileLocationField = new JTextField();
		
		public RdfModelCreationPanel() {
			super();
			rdfModel = null;
			
			JLabel fileLabel = new JLabel("RDF or N3 File Location:");
			JButton fileButton = new JButton("Browse...");
			final String[] fileExtensions = {"rdf","n3"};
			fileButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String lastOpenedFileName = ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME, CONFIGURATION_FILE_STRING, null);
					getFileURL(fileLocationField, fileExtensions, "RDF and N3 files (*.rdf, *.n3)", lastOpenedFileName);
				}
			});
			fileButton.setMnemonic('f');

			updateContents();
			this.setLayout(new GridBagLayout());

			this.add(fileLabel,new GridBagConstraints(
					0,0,2,1,1,0,
					GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5),
					2,2));

			this.add(fileLocationField,new GridBagConstraints(
					0,1,1,1,1,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets( 5, 15, 5, 5),
					2,2));
			this.add(fileButton,new GridBagConstraints(
					0,2,1,1,0,0,
					GridBagConstraints.NORTHEAST,
					GridBagConstraints.NONE,
					new Insets(5, 5, 5, 5),
					2,2));

		   this.add(new JPanel(),new GridBagConstraints(
				   0,3,1,1,1,1,
				   GridBagConstraints.WEST,
				   GridBagConstraints.BOTH,
				   new Insets(5, 5, 5, 5),
				   2,2));
			
		}
		
		String getTitle() {
			return "Open source file:";
		}
		String getNextButtonText() {
			return "Open >>";
		}
		WizardPanel getNextPanel() {
			return rdfQueryPanel;
		}
		void updateContents() {
		}
		
		boolean executeStep() {
			File file = new File(fileLocationField.getText());
			try {
				rdfModel = RdfQueryUtil.createModel(file);
			} catch (Exception e) {
				ErrorDialog.showError(this, e, "Error parsing file " + file.getAbsolutePath());
				return false;
			}
			ConfigurationManager.storeString(CONFIGURATION_SECTION_NAME, CONFIGURATION_FILE_STRING, file.getAbsolutePath());
			return true;
		}
		
	}

	class RdfQueryPanel extends WizardPanel {

		private JTextArea rdfQueryArea;

		RdfQueryPanel() {
			super();
			
			tupleSet = null;
			
			rdfQueryArea = new JTextArea();
			rdfQueryArea.setBorder(BorderFactory.createLoweredBevelBorder());
			
			/// @todo: temporary set text here so it is faster to test. Remove!
			rdfQueryArea.setText("SELECT ?a, ?b, ?d WHERE (?a, <implements>, ?c),\n (?c, <is-a>, ?b),\n (?a, <extends>, ?d)");

			this.setLayout(new GridBagLayout());
			this.add(new JLabel("RDF Query:"),new GridBagConstraints(
					0,0,1,1,1,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5),
					0,0));

			this.add(rdfQueryArea,new GridBagConstraints(
					0,1,1,1,1,1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets( 5, 15, 5, 5),
					0,0));
		}

		void updateContents() {
		}
        
		boolean executeStep() {
			try {
				String queryString = rdfQueryArea.getText();

				Query query = new Query(queryString) ;
				List resultVars = query.getResultVars();
				tupleSet = new TupleSet(
									(String[]) resultVars.toArray(new String[resultVars.size()]));
				QueryResults results = RdfQueryUtil.executeRDQL(rdfModel, query);
				for ( Iterator iter = results ; iter.hasNext() ; ) {
					ResultBinding resBinding = (ResultBinding)iter.next() ;
					Object[] tuple = new Object[resultVars.size()];
					for (int i = 0; i < resultVars.size(); i++) {
						String  queryVar = (String) resultVars.get(i);
						Object obj = resBinding.get(queryVar);
						tuple[i] = obj;				
					} 
					tupleSet.addTuple(tuple);
				}
				results.close() ;
				return true;
			}
			catch (Exception e) {
				ErrorDialog.showError(this, e, "Query Failed");
				return false;
			}
		}

		String getTitle() {
			return "Enter RDF Query";
		}

		String getNextButtonText() {
			return "Query";
		}

		WizardPanel getNextPanel() {
			return null;
		}
	}

	/**
	 * Construct an instance of this view
	 */
	public RdfQueryDialog (JFrame parent) {
		super(parent, "RDF Query", true);
		initializePanels();

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		
		addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width < MINIMUM_WIDTH) width = MINIMUM_WIDTH;
				if (height < MINIMUM_HEIGHT) height = MINIMUM_HEIGHT;
				setSize(width, height);
			}
			public void componentShown(ComponentEvent e) {
				componentResized(e);
			}
		});
		
		this.stepLabel = new JLabel();
				
		JPanel buttonPane = createButtonPanel(parent);
		
		contentPane.add(stepLabel,new GridBagConstraints(
				0,0,1,1,1,0,
				GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 10, 5, 5),
				2,2));
		contentPane.add(buttonPane, new GridBagConstraints(
				0,2,1,1,1,0,
				GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 5, 5),
				2,2));
				
		setCurrentPanel(this.openFilePanel);
				
		ConfigurationManager.restorePlacement(
			CONFIGURATION_SECTION_NAME,
			this,
			new Rectangle(100, 100, MINIMUM_WIDTH, MINIMUM_WIDTH));
	}

	protected void initializePanels() {
		openFilePanel = new RdfModelCreationPanel();
		rdfQueryPanel = new RdfQueryPanel();
	}

	public JPanel createButtonPanel(JFrame frame) {
		nextButton = new JButton();
		nextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				boolean success = executeCurrentStep();
				if(success) {
					gotoNextStep();
				}
			}
		});
        
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
        
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(nextButton);
		buttonPane.add(cancelButton);
		return buttonPane;
	}
    
	protected boolean executeCurrentStep() {
		try {
			return this.currentStep.executeStep();
		} catch(Exception e) {
			ErrorDialog.showError(this,e,"Step failed");
		}
		return false;
	}

	protected void gotoNextStep() {
		WizardPanel nextPanel = this.currentStep.getNextPanel();
		if(nextPanel == null) {
			hide();
		} else {
			setCurrentPanel(nextPanel);
		}
	}

	protected void setCurrentPanel(WizardPanel panel) {
		Container contentPane = this.getContentPane();
		if(this.currentStep != null) {
			contentPane.remove(this.currentStep);
		}
		this.currentStep = panel;
		this.stepLabel.setText(panel.getTitle());
		this.nextButton.setText(panel.getNextButtonText());
		try {
			panel.updateContents();
		} catch (Exception e) {
			ErrorDialog.showError(this, e, "Internal problem");
		}
		contentPane.add(panel,new GridBagConstraints(
				0,1,1,1,1,1,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5),
				2,2));
		contentPane.invalidate();
		this.repaint();
	}
	
	/**
	 * @todo this method is practically copy and paste from DatabaseConnectionDialog
	 * with small alterations. Work out how to avoid this.
	 */
	private void getFileURL(JTextField urlField, final String[] extensions, final String description, final String openingDirLocation) {
		JFileChooser openDialog;
		if (openingDirLocation != null) {
			openDialog = new JFileChooser(openingDirLocation);
		} else {
			openDialog = new JFileChooser(System.getProperty("user.dir"));
		}
		
		openDialog.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				for (int i = 0; i < extensions.length; i++) {
					String ext = extensions[i];
					if (f.getAbsolutePath().endsWith("." + ext)) {
						return true;
					}
				}
				return false;
			}
			public String getDescription() {
				return description;
			}
		});

		if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File openedFile = openDialog.getSelectedFile();
			String fileURL = openedFile.getAbsolutePath();
			urlField.setText(fileURL);
		}
	}
	
	public void hide() {
		super.hide();
		setCurrentPanel(this.openFilePanel);
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME,	this);
	}

	public TupleSet getTuples() {
		return this.tupleSet;
	}

}
