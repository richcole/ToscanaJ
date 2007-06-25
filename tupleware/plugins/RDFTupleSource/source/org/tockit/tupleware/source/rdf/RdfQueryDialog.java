/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id:RdfQueryDialog.java 1929 2007-06-24 04:50:48Z peterbecker $
 */
package org.tockit.tupleware.source.rdf;

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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.swing.preferences.ExtendedPreferences;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class RdfQueryDialog extends JDialog {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(DiagramExportSettings.class);

	private static final String CONFIGURATION_STRING_LAST_FILE = "lastFile";
	private static final String CONFIGURATION_STRING_LAST_QUERY = "lastQuery";
	
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 350;
	
	private WizardPanel rdfQueryPanel;
	private WizardPanel openFilePanel;
	private JLabel stepLabel;
	private WizardPanel currentStep;
	private JButton nextButton;
	
	private Model rdfModel;
	private Relation tupleSet;

	private ModelSourcePanel modelSourcePanel;

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
    
	class ModelSourcePanel extends WizardPanel {
		private JRadioButton loadModelChoice;
		
		public ModelSourcePanel() {
			super();
			ButtonGroup choices = new ButtonGroup();
			JRadioButton keepModelChoice = new JRadioButton("keep current RDF model");
			loadModelChoice = new JRadioButton("load new RDF model from file");
			
			choices.add(keepModelChoice);
			choices.add(loadModelChoice);
			keepModelChoice.setSelected(true);
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = GridBagConstraints.RELATIVE;
			constraints.weightx = 1;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			
			this.add(keepModelChoice,constraints);
			this.add(loadModelChoice,constraints);
			
			constraints.weighty = 1;
			this.add(new JPanel(),constraints);
		}
		
		boolean executeStep() {
			// nothing to do
			return true;
		}

		String getNextButtonText() {
			return "Select >>";
		}

		WizardPanel getNextPanel() {
			if(loadModelChoice.isSelected()) {
				return openFilePanel;
			} else {
				return rdfQueryPanel;
			}
		}

		String getTitle() {
			return "Select model source";
		}

		void updateContents() {
			// nothing to do
		}
		
	}
	
	class RdfModelCreationPanel extends WizardPanel {
		private JTextField fileLocationField = new JTextField();
		
		public RdfModelCreationPanel() {
			super();

			JLabel fileLabel = new JLabel("RDF or N3 File Location:");
			final String lastOpenedFileName = preferences.get(CONFIGURATION_STRING_LAST_FILE, null);
			fileLocationField.setText(lastOpenedFileName);
			JButton fileButton = new JButton("Browse...");
			final String[] fileExtensions = {"rdf","n3"};
			fileButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
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
			// nothing to do
		}
		
		boolean executeStep() {
			File file = new File(fileLocationField.getText());
			try {
				rdfModel = RdfQueryUtil.createModel(file);
			} catch (Exception e) {
				ErrorDialog.showError(this, e, "Error parsing file " + file.getAbsolutePath());
				return false;
			}
			preferences.put(CONFIGURATION_STRING_LAST_FILE, file.getAbsolutePath());
			return true;
		}
		
	}

	class RdfQueryPanel extends WizardPanel {

		private JTextArea rdfQueryArea;

		RdfQueryPanel() {
			super();
			
			tupleSet = null;
			
			rdfQueryArea = new JTextArea(preferences.get(CONFIGURATION_STRING_LAST_QUERY, ""));
			rdfQueryArea.setBorder(BorderFactory.createLoweredBevelBorder());
			
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
			// nothing to do
		}
        
		boolean executeStep() {
			preferences.put(CONFIGURATION_STRING_LAST_QUERY, rdfQueryArea.getText());
			Query query = QueryFactory.create(rdfQueryArea.getText());
			List resultVars = query.getResultVars();
			tupleSet = new RelationImplementation((String[]) resultVars
					.toArray(new String[resultVars.size()]));
			QueryExecution queryEx = QueryExecutionFactory.create(query,
					rdfModel);
			try {
				ResultSet results = queryEx.execSelect();
				for (; results.hasNext();) {
					QuerySolution solution = (QuerySolution) results.next();
					Object[] tuple = new Object[resultVars.size()];
					for (int i = 0; i < resultVars.size(); i++) {
						String queryVar = (String) resultVars.get(i);
						Object obj = solution.get(queryVar);
						tuple[i] = obj;
					}
					tupleSet.addTuple(tuple);
				}
				return true;
			}
			catch (Exception e) {
				ErrorDialog.showError(this, e, "Query Failed");
				return false;
			}
			finally {
				queryEx.close();
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
	public RdfQueryDialog (JFrame parent, Model rdfModel) {
		super(parent, "RDF Query", true);
		this.rdfModel = rdfModel;
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
				
		JPanel buttonPane = createButtonPanel();
		
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
				
		setCurrentPanel(this.rdfModel==null?this.openFilePanel:this.modelSourcePanel);
				
		preferences.restoreWindowPlacement(
			this,
			new Rectangle(100, 100, MINIMUM_WIDTH, MINIMUM_WIDTH));
	}

	protected void initializePanels() {
		modelSourcePanel = new ModelSourcePanel();
		openFilePanel = new RdfModelCreationPanel();
		rdfQueryPanel = new RdfQueryPanel();
	}

	public JPanel createButtonPanel() {
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
				setVisible(false);
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
			setVisible(false);
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
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(!visible) {
			setCurrentPanel(this.rdfModel==null?this.openFilePanel:this.modelSourcePanel);
			preferences.storeWindowPlacement(this);
		}
	}

	public Relation getTuples() {
		return this.tupleSet;
	}

	public Model getRdfModel() {
		return this.rdfModel;
	}
}
