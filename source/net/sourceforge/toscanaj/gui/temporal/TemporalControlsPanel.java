/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.temporal;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.tockit.canvas.events.CanvasDrawnEvent;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;
import org.tockit.canvas.manipulators.ItemMovementManipulator;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.DiagramExportSettings;
import net.sourceforge.toscanaj.model.context.FCAObject;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.scales.NumberField;
import net.sourceforge.toscanaj.view.temporal.InterSequenceTransitionArrow;
import net.sourceforge.toscanaj.view.temporal.StateRing;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;

public class TemporalControlsPanel extends JTabbedPane implements EventBrokerListener {
	private static final Insets DEFAULT_SPACER_INSETS = new Insets(0,0,10,0);
	private static final Insets DEFAULT_BUTTON_INSETS = new Insets(2,16,2,16);
	private static final Insets DEFAULT_LABEL_INSETS = new Insets(2,2,2,2);
	private static final Insets DEFAULT_FIELD_INSETS = new Insets(2,20,2,2);
	private static final String TRANSITION_LAYER_NAME = "transitions";

	private static final Color[] COLORS = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.GRAY, Color.MAGENTA,
														 Color.ORANGE, Color.PINK, Color.BLACK, Color.YELLOW};

    private ManyValuedContext context;
    private JComboBox sequenceColumnChooser;
    private JComboBox timelineColumnChooser;
    private JButton addStaticTransitionsButton;
    private JComboBox sequenceToShowChooser;
    private JButton animateTransitionsButton;
    private JButton exportImagesButton;
    private DiagramView diagramView;
    private AnimationTimeController timeController;
    
    private double targetTime;
    private double lastAnimationTime;
    private int currentStep;
    private NumberField speedField;
    private NumberField fadeInField;
    private NumberField holdField;
    private NumberField fadeOutField;
    private List sequenceValues;
    private List timelineValues;
    private JCheckBox serializeSequencesBox;
    private File lastImageExportFile;
    private DiagramExportSettings diagramExportSettings;
    private JButton firstStepButton;
    private JButton previousStepButton;
    private JButton nextStepButton;
    private JButton lastStepButton;
    private JLabel stepPositionLabel;
    private JButton startSteppingButton;
	
	public TemporalControlsPanel(DiagramView diagramView, 
    						   DiagramExportSettings diagramExportSettings, EventBroker eventBroker) {
	  	super();
	  	
	  	this.diagramView = diagramView;
	  	
        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);
        diagramView.getController().getEventBroker().subscribe(this, DisplayedDiagramChangedEvent.class, DiagramView.class);
		diagramView.getController().getEventBroker().subscribe(this, CanvasDrawnEvent.class, Object.class);
		new ItemMovementManipulator(diagramView, TransitionArrow.class, diagramView.getController().getEventBroker());

        this.diagramExportSettings = diagramExportSettings;
        this.timeController = new AnimationTimeController(0,0,0,0,0);
	  	
	  	buildGUI();
	  	fillGUI();
    }
    
    private void buildGUI() {
        this.addTab("Controls", createControlsPanel());
        this.addTab("Data", createBasicSettingsPanel());
        this.addTab("Animation Settings", createAnimationsSettingsPanel());
    }

	private JPanel createControlsPanel() {
        addStaticTransitionsButton = new JButton("Show all transitions");
        addStaticTransitionsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addFixedTransitions();
            }
        });

        startSteppingButton = new JButton("Start stepping");
        startSteppingButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                startStepping();
            }
        });

        animateTransitionsButton = new JButton("Animate transitions");
        animateTransitionsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addAnimatedTransitions();
            }
        });

        exportImagesButton = new JButton("Export all steps as images");
        exportImagesButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                exportImages();
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        int row = 0;
        panel.add(addStaticTransitionsButton, new GridBagConstraints(0, row, 1, 1, 1, 0,
														GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
														DEFAULT_BUTTON_INSETS, 0, 0));
		row++;
        panel.add(exportImagesButton, new GridBagConstraints(0, row, 1, 1, 1, 0,
														GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
														DEFAULT_BUTTON_INSETS, 0, 0));
		row++;
        panel.add(animateTransitionsButton, new GridBagConstraints(0, row, 1, 1, 1, 0,
														GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
														DEFAULT_BUTTON_INSETS, 0, 0));
		row++;
        panel.add(startSteppingButton, new GridBagConstraints(0, row, 1, 1, 1, 0,
														GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
														DEFAULT_BUTTON_INSETS, 0, 0));
        row++;
        panel.add(createStepPanel(), new GridBagConstraints(0, row, 1, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        DEFAULT_SPACER_INSETS, 0, 0));
        row++;
        panel.add(new JPanel(), new GridBagConstraints(0, row, 1, 1, 1, 1,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        DEFAULT_SPACER_INSETS, 0, 0));
        panel.setBorder(createTitledBorder("Temporal Controls"));                                                
        return panel;
    }

    private JPanel createBasicSettingsPanel() {
		JLabel sequenceColumnLabel = new JLabel("Sequence Column:");
		sequenceColumnChooser = new JComboBox();
		sequenceColumnChooser.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	calculateValueLists();
		        fillSequenceChooser();
		    }
		});
		
		JLabel timelineLabel = new JLabel("Timeline Column:");
		timelineColumnChooser = new JComboBox();
		timelineColumnChooser.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        calculateValueLists();
		        fillSequenceChooser();
		    }
		});
		
		JLabel sequenceLabel = new JLabel("Sequence:");
		sequenceToShowChooser = new JComboBox();
		
		serializeSequencesBox = new JCheckBox("Serialize when stepping/animating"); 
		
		JPanel basicSettingsPanel = new JPanel(new GridBagLayout());
		int r = 0;
		basicSettingsPanel.add(sequenceColumnLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		basicSettingsPanel.add(sequenceColumnChooser, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
		r++;
		basicSettingsPanel.add(timelineLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		basicSettingsPanel.add(timelineColumnChooser, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
		r++;
		basicSettingsPanel.add(sequenceLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		basicSettingsPanel.add(sequenceToShowChooser, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
		r++;
		basicSettingsPanel.add(serializeSequencesBox, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.CENTER, GridBagConstraints.NONE,
														new Insets(2,2,2,2), 0, 0));
        r++;
        basicSettingsPanel.add(new JPanel(), new GridBagConstraints(0, r, 1, 1, 1, 1,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        DEFAULT_SPACER_INSETS, 0, 0));
		basicSettingsPanel.setBorder(createTitledBorder("Data settings"));
		return basicSettingsPanel;
	}

	private JPanel createAnimationsSettingsPanel() {
		JLabel speedLabel = new JLabel("Speed (ms/step):");
		speedField = new NumberField(10,NumberField.INTEGER);
		speedField.setText("200");
		JLabel fadeInLabel = new JLabel("Fade-in steps:");
		fadeInField= new NumberField(10,NumberField.FLOAT);
		fadeInField.setText("1");
		JLabel holdLabel = new JLabel("Hold steps:");
		holdField= new NumberField(10,NumberField.FLOAT);
		holdField.setText("1");
		JLabel fadeOutLabel = new JLabel("Fade-out steps:");
		fadeOutField = new NumberField(10,NumberField.FLOAT);
		fadeOutField.setText("5");
		
		JPanel animationSettingsPanel = new JPanel(new GridBagLayout());
		int r = 0;
		animationSettingsPanel.add(speedLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(speedField, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(fadeInLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(fadeInField, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(holdLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(holdField, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(fadeOutLabel, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
														DEFAULT_LABEL_INSETS, 0, 0));
		r++;
		animationSettingsPanel.add(fadeOutField, new GridBagConstraints(0, r, 1, 1, 1, 0,
														GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
														DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(new JPanel(), new GridBagConstraints(0, r, 1, 1, 1, 1,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        DEFAULT_SPACER_INSETS, 0, 0));
		animationSettingsPanel.setBorder(createTitledBorder("Animation controls"));
		return animationSettingsPanel;
	}

	private JPanel createStepPanel() {
		firstStepButton = new JButton("<<");
		firstStepButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		        gotoFirstStep();
		    }
		});
		
		previousStepButton = new JButton("<");
		previousStepButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		        gotoPreviousStep();
		    }
		});
		
		nextStepButton = new JButton(">");
		nextStepButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		        gotoNextStep();
		    }
		});
		
		lastStepButton = new JButton(">>");
		lastStepButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		        gotoLastStep();
		    }
		});
		
		stepPositionLabel = new JLabel("0/0");
		
		JPanel stepPanel = new JPanel(new GridBagLayout());
		stepPanel.add(firstStepButton, new GridBagConstraints(0, 0, 1, 1, 1, 0,
		                                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
		                                                new Insets(2,2,2,2), 5, 0));
		stepPanel.add(previousStepButton, new GridBagConstraints(1, 0, 1, 1, 1, 0,
		                                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
		                                                new Insets(2,2,2,2), 5, 0));
		stepPanel.add(nextStepButton, new GridBagConstraints(2, 0, 1, 1, 1, 0,
		                                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
		                                                new Insets(2,2,2,2), 5, 0));
		stepPanel.add(lastStepButton, new GridBagConstraints(3, 0, 1, 1, 1, 0,
		                                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
		                                                new Insets(2,2,2,2), 5, 0));
		stepPanel.add(stepPositionLabel, new GridBagConstraints(4, 0, 1, 1, 1, 0,
		                                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
		                                                new Insets(2,2,2,2), 5, 0));
		stepPanel.setBorder(createTitledBorder("Step controls"));
		return stepPanel;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(
												BorderFactory.createLineBorder(SystemColor.controlDkShadow),
												title);
	}
    
    private void fillGUI() {
        Object[] attributes;
        boolean enabled;
        if(context != null) {
            attributes = this.context.getAttributes().toArray();
            enabled = true;
        } else {
            attributes = new Object[]{"No context available"};
            enabled = false;
        }

        sequenceColumnChooser.setModel(new DefaultComboBoxModel(attributes));
        sequenceColumnChooser.setEnabled(enabled);

        timelineColumnChooser.setModel(new DefaultComboBoxModel(attributes));
        timelineColumnChooser.setEnabled(enabled);
        if(context != null) {
			timelineColumnChooser.setSelectedIndex(Math.min(attributes.length - 1, 1));
        }

		calculateValueLists();        
        fillSequenceChooser();

        setButtonStates(!enabled);
    }

    private void fillSequenceChooser() {
    	DefaultComboBoxModel model = new DefaultComboBoxModel();
    	model.addElement("<All Sequences>");
    	Iterator it = this.sequenceValues.iterator();
    	while (it.hasNext()) {
            AttributeValue value = (AttributeValue) it.next();
            model.addElement(value);
        }
        
    	this.sequenceToShowChooser.setModel(model);
    }

    private void setButtonStates(boolean allDisabled) {
        boolean enabled = !allDisabled && (this.diagramView.getDiagram() != null);
        addStaticTransitionsButton.setEnabled(enabled);
        startSteppingButton.setEnabled(enabled);
        animateTransitionsButton.setEnabled(enabled);
        exportImagesButton.setEnabled(enabled && this.diagramExportSettings != null);
        setStepButtonStates(!enabled);
    }
    
    private void setStepButtonStates(boolean allDisabled) {
        firstStepButton.setEnabled(!allDisabled && this.currentStep > 1);
        previousStepButton.setEnabled(!allDisabled && this.currentStep > 1);
        nextStepButton.setEnabled(!allDisabled && this.currentStep < this.timeController.getEndTime());
        lastStepButton.setEnabled(!allDisabled && this.currentStep < this.timeController.getEndTime());
        int endTime = (int) this.timeController.getEndTime();
        this.stepPositionLabel.setText(this.currentStep + "/" + endTime);
    }

	/**
	 * @todo this is reduced code from the image export in the
	 * ToscanaJMainPanel. Extract the full code into a new object, and use it
	 * here.
	 */    
    protected void exportImages() {
    	disableStepControls();
    	
        final JFileChooser saveDialog = new JFileChooser(this.lastImageExportFile);
        boolean formatDefined;
        do {
            formatDefined = true;
            int rv = saveDialog.showSaveDialog(this);
            if (rv == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                GraphicFormat format =
                    GraphicFormatRegistry.getTypeByExtension(
                        selectedFile);
                if (format != null) {
                    this.diagramExportSettings.setGraphicFormat(format);
                } else {
                    if(selectedFile.getName().indexOf('.') != -1) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Sorry, no type with this extension known.\n"
                                + "Please use either another extension or try\n"
                                + "manual settings.",
                            "Export failed",
                            JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "No extension given.\n" +
                            "Please give an extension or pick a file type\n" +
                            "from the options.",
                            "Export failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    formatDefined = false;
                }

                if (formatDefined) {
                    exportImages(selectedFile);
                }
            }
        } while (formatDefined == false);
    }

    protected void exportImages(File selectedFile){
        clearTransitionLayer();

    	// export all transitions into main file
        int length = this.timelineValues.size();
        this.timeController.setEndTime(length);
        this.timeController.setFadeInTime(0);
        this.timeController.setVisibleTime(Double.MAX_VALUE);
        this.timeController.setFadeOutTime(0);
        this.timeController.setMillisecondsPerStep(1);
        this.timeController.reset();
        addTransitions(this.timeController.getEndTime(), false);
        this.timeController.setCurrentTime(this.timeController.getEndTime());        
        exportImage(selectedFile);
        this.lastImageExportFile = selectedFile;

        this.timeController.setVisibleTime(1);
		
        if(this.serializeSequencesBox.isSelected() &&
                    !(this.sequenceToShowChooser.getSelectedItem() instanceof AttributeValue)) {
            int numSeq = this.sequenceValues.size();
            this.timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(this.timeController.getAllFadedTime(), true);
        } else {
            this.timeController.setEndTime(length);
            addTransitions(this.timeController.getAllFadedTime(), true);
        }
        
        double targetStep = this.timeController.getAllFadedTime();
        
        for(int i = 0; i <= targetStep; i++) {
            this.timeController.setCurrentTime(i);
        	exportImage(new File(getNumberedFileName(selectedFile,i,targetStep)));
        }
        
        this.diagramView.removeLayer(TRANSITION_LAYER_NAME);
    }
    
    private String getNumberedFileName(File selectedFile, int currentStep, double targetStep) {
    	String origName = selectedFile.getAbsolutePath();
    	int dotPos = origName.lastIndexOf('.');
    	double countdown = targetStep;
    	String fillingZeroes = "";
    	while(countdown >= 1) {
    		fillingZeroes += "0";
    		countdown /= 10;
    	} 
    	String currentPos = String.valueOf(currentStep);
        return origName.substring(0,dotPos) + "-" + 
                fillingZeroes.substring(currentPos.length()) + currentPos + 
                origName.substring(dotPos);
    }

    protected void exportImage(File file) {
        try {
            this
                .diagramExportSettings
                .getGraphicFormat()
                .getWriter()
                .exportGraphic(
                this.diagramView,
                this.diagramExportSettings,
                file, 
                new Properties());
        } catch (ImageGenerationException e) {
            ErrorDialog.showError(this, e, "Exporting image error");
        } catch (OutOfMemoryError e) {
				ErrorDialog.showError(
				this,
				e,
				"Out of memory",
				"Not enough memory available to export\n"
					+ "the diagram in this size");
        }
    }

    public void processEvent(Event e) {
        if(e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
            this.context = csce.getConceptualSchema().getManyValuedContext();
            fillGUI();
        }
        if(e instanceof DisplayedDiagramChangedEvent) {
            disableStepControls();
        	setButtonStates(false);
        }
        if(e instanceof CanvasDrawnEvent) {
        	animate();
        }
    }

    public void disableStepControls() {
        this.currentStep = 0;
        this.timeController.setEndTime(0);
        setStepButtonStates(true);
    }
    
    private void animate() {
    	if(this.lastAnimationTime > this.targetTime) {
    		return; // nothing to animate anymore
    	}
        this.timeController.calculateCurrentTime();
        this.lastAnimationTime = this.timeController.getCurrentTime();
        this.diagramView.repaint(); // paint it again as time has past
    }

    private void addFixedTransitions() {
        clearTransitionLayer();

        disableStepControls();

        int length = this.timelineValues.size();
        this.timeController.setEndTime(length);
        this.timeController.setFadeInTime(0);
        this.timeController.setVisibleTime(Double.MAX_VALUE);
        this.timeController.setFadeOutTime(0);
        this.timeController.setMillisecondsPerStep(1);
        this.timeController.reset();
        addTransitions(length, false);
        
        this.diagramView.repaint();
    }

    private void startStepping() {
        clearTransitionLayer();

        int length = this.timelineValues.size();
        this.timeController.setFadeInTime(0);
        this.timeController.setVisibleTime(1);
        this.timeController.setFadeOutTime(0);
        this.timeController.setMillisecondsPerStep(1);
        if(this.serializeSequencesBox.isSelected() &&
                    !(this.sequenceToShowChooser.getSelectedItem() instanceof AttributeValue)) {
            int numSeq = this.sequenceValues.size();
            this.timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(this.timeController.getAllFadedTime(), true);
        } else {
            this.timeController.setEndTime(length);
            addTransitions(this.timeController.getAllFadedTime(), true);
        }
        this.lastAnimationTime = this.timeController.getEndTime() + 23; // don't animate
        gotoStep(1);
    }

    private void addAnimatedTransitions() {
        clearTransitionLayer();
        
        disableStepControls();

        int length = this.timelineValues.size();
        this.timeController.setFadeInTime(this.fadeInField.getDoubleValue());
        this.timeController.setVisibleTime(this.holdField.getDoubleValue());
        this.timeController.setFadeOutTime(this.fadeOutField.getDoubleValue());
        this.timeController.setMillisecondsPerStep(this.speedField.getIntegerValue());
        this.timeController.reset();
        if(this.serializeSequencesBox.isSelected() && 
                    !(this.sequenceToShowChooser.getSelectedItem() instanceof AttributeValue)) {
        	int numSeq = this.sequenceValues.size();
            this.timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(this.timeController.getAllFadedTime(), true);
        } else {
            this.timeController.setEndTime(length);
        	addTransitions(this.timeController.getAllFadedTime(), true);
        }
        this.diagramView.repaint();
    }

    private void addTransitions(double newTargetTime, boolean highlightStates) {
        AttributeValue selectedSequence = null; // no specific sequence selected
        Object selectedSequenceItem = this.sequenceToShowChooser.getSelectedItem();
        if(selectedSequenceItem instanceof AttributeValue) {
            selectedSequence = (AttributeValue) selectedSequenceItem;
        }

        List objectSequences = calculateObjectSequences();
        Hashtable nodeViewMap = createNodeViewMap();
        Iterator seqIt = objectSequences.iterator();
        Iterator seqValIt = this.sequenceValues.iterator();
        int colNum = 0;
        boolean start = true;
        while (seqIt.hasNext()) {
            List sequence = (List) seqIt.next();
            AttributeValue curSequenceValue = (AttributeValue) seqValIt.next();
            if(start) {
                start = false;
                this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            if(selectedSequence == null || selectedSequence == curSequenceValue) {
                addTransitions(sequence, COLORS[colNum], nodeViewMap, highlightStates);
            }
            colNum = (colNum + 1) % COLORS.length;
        }
    }

    private void clearTransitionLayer() {
        if(this.diagramView.hasLayer(TRANSITION_LAYER_NAME)) {
            this.diagramView.removeLayer(TRANSITION_LAYER_NAME);
        }
        this.diagramView.addLayer(TRANSITION_LAYER_NAME);
    }

	/**
	 * @todo Copy and paste code, refactor. Maybe it is best to add the
	 * transitions once they are known and not every time a button is pressed.
	 * The buttons would then change only the time controller and not affect the
	 * canvas items directly.
	 */
    private void addTransitionsSerialized(double newTargetTime, boolean highlightStates) {
        AttributeValue selectedSequence = null; // no specific sequence selected
        Object selectedSequenceItem = this.sequenceToShowChooser.getSelectedItem();
        if(selectedSequenceItem instanceof AttributeValue) {
            selectedSequence = (AttributeValue) selectedSequenceItem;
        }

        List objectSequences = calculateObjectSequences();
        Hashtable nodeViewMap = createNodeViewMap();
        Iterator seqIt = objectSequences.iterator();
        Iterator seqValIt = this.sequenceValues.iterator();
        int seqNum = 0;
        int seqLength = this.timelineValues.size();
        List lastSequence = null;
        Color color = null;
        while (seqIt.hasNext()) {
            List sequence = (List) seqIt.next();

            if(lastSequence != null) {
                Color nextColor = COLORS[seqNum % COLORS.length];
                NodeView endViewLast = findObjectConceptView((FCAObject) lastSequence.get(lastSequence.size()-1), nodeViewMap);
                NodeView startViewNew = findObjectConceptView((FCAObject) sequence.get(0), nodeViewMap);
                if(endViewLast == null) {
                	continue;
                }
                if(endViewLast != startViewNew) {
	                this.diagramView.addCanvasItem(
	                            new InterSequenceTransitionArrow(endViewLast, startViewNew,
	                                                             color, nextColor, seqNum * seqLength + 0.5,
	                                                             this.timeController),
	                            TRANSITION_LAYER_NAME);
                }
            }

            color = COLORS[seqNum % COLORS.length];
            AttributeValue curSequenceValue = (AttributeValue) seqValIt.next();
            if(lastSequence == null) {
                this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            if(selectedSequence == null || selectedSequence == curSequenceValue) {
                addTransitions(sequence, color, 
                               nodeViewMap, highlightStates, seqNum * seqLength);
            }
            seqNum++;
            lastSequence = sequence;
        }
    }

    private Hashtable createNodeViewMap() {
    	Hashtable retVal = new Hashtable();
    	Iterator it = this.diagramView.getCanvasItemsByType(NodeView.class).iterator();
    	while (it.hasNext()) {
            NodeView view = (NodeView) it.next();
            retVal.put(view.getDiagramNode(), view);
        }
        return retVal;
    }
    
    private void addTransitions(List sequence, Color color, Hashtable nodeViewMap, boolean highlightStates) {
    	addTransitions(sequence, color, nodeViewMap, highlightStates, 0);
    }

    private void addTransitions(List sequence, Color color, Hashtable nodeViewMap, boolean highlightStates, int countStart) {
    	NodeView oldView = null;
    	Iterator objectIt = sequence.iterator();
    	int count = countStart;
    	objLoop: while (objectIt.hasNext()) {
    		count++;
            FCAObject object = (FCAObject) objectIt.next();
    	    NodeView curView = findObjectConceptView(object, nodeViewMap);
    	    if(curView == null) {
    	    	continue;
    	    }
    	    if(highlightStates) {
    	        this.diagramView.addCanvasItem(new StateRing(curView, color, count, this.timeController),
    	                                       TRANSITION_LAYER_NAME);
    	    }
    	    if(oldView != null && oldView != curView) {
    	        this.diagramView.addCanvasItem(new TransitionArrow(oldView, curView, color, count - 0.5, this.timeController),
    	                                       TRANSITION_LAYER_NAME);
    	    }
    	    oldView = curView;
        }
    }

	private NodeView findObjectConceptView(FCAObject object, Hashtable nodeViewMap) {
	    Iterator nodeIt = this.diagramView.getDiagram().getNodes();
	    while (nodeIt.hasNext()) {
	        DiagramNode node = (DiagramNode) nodeIt.next();
	        Iterator objIt = node.getConcept().getObjectContingentIterator();
	        while (objIt.hasNext()) {
	            FCAObject contObj = (FCAObject) objIt.next();
	            if(contObj.equals(object)) {
	            	return (NodeView) nodeViewMap.get(node);
	            }
	        }
	    }
	    return null;
	}
	
	private void calculateValueLists() {
	    sequenceValues = new ArrayList();
	    timelineValues = new ArrayList();

	    Object selectedSequenceColumn = this.sequenceColumnChooser.getSelectedItem();
	    if(!(selectedSequenceColumn instanceof ManyValuedAttribute) ) {
	    	return;
	    }
        ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) selectedSequenceColumn;
	    ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) this.timelineColumnChooser.getSelectedItem();

	    Iterator objIt = this.context.getObjects().iterator();
	    while(objIt.hasNext()) {
	        FCAObject object = (FCAObject) objIt.next();
	        AttributeValue value = this.context.getRelationship(object, sequenceAttribute);
	        if(!sequenceValues.contains(value) && value != null) {
	            boolean inserted = false;;
	            ListIterator seqIt = sequenceValues.listIterator();
	            while(seqIt.hasNext()) {
	                AttributeValue curValue = (AttributeValue) seqIt.next();
	                if(value.isLesserThan(curValue)) {
	                    if(seqIt.hasPrevious()) {
	                        seqIt.previous();
	                    }
	                    seqIt.add(value);
	                    inserted = true;
	                    break;
	                }
	            }
	            if(!inserted) {
	                seqIt.add(value);
	            }
	        }
	        value = this.context.getRelationship(object, timelineAttribute);
	        if(!timelineValues.contains(value)) {
	            boolean inserted = false;;
	            ListIterator tlIt = timelineValues.listIterator();
	            while(tlIt.hasNext()) {
	                AttributeValue curValue = (AttributeValue) tlIt.next();
	                if(value != null && value.isLesserThan(curValue)) {
	                    if(tlIt.hasPrevious()) {
	                        tlIt.previous();
	                    }
	                    tlIt.add(value);
	                    inserted = true;
	                    break;
	                }
	            }
	            if(!inserted) {
	                tlIt.add(value);
	            }
	        }
	    }
	}
	
    private List calculateObjectSequences() {
        ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) this.sequenceColumnChooser.getSelectedItem();
        ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) this.timelineColumnChooser.getSelectedItem();

        List objectSequences = new ArrayList();
        
        // initialise sequences with empty lists
        Iterator seqValIt = sequenceValues.iterator();
        while (seqValIt.hasNext()) {
            seqValIt.next();
            objectSequences.add(new ArrayList());
        }
        
        // go over time
        Iterator timeIt = timelineValues.iterator();
        while (timeIt.hasNext()) {
            AttributeValue timelineValue = (AttributeValue) timeIt.next();
            
            // try to find matching object for each sequence
            seqValIt = sequenceValues.iterator();
            Iterator seqIt = objectSequences.iterator();
        	while (seqValIt.hasNext()) {
                AttributeValue sequenceValue = (AttributeValue) seqValIt.next();
                List sequence = (List) seqIt.next();
        	    boolean objectFound = false;
        	    Iterator objIt = this.context.getObjects().iterator();
        	    while (objIt.hasNext()) {
        	        FCAObject object = (FCAObject) objIt.next();
        			if( this.context.getRelationship(object, sequenceAttribute).equals(sequenceValue) && 
        	        	this.context.getRelationship(object, timelineAttribute).equals(timelineValue) ) {
        	        		sequence.add(object);
        	        		objectFound = true;
        	        		break;
        	        }
        	    }
        	    if(!objectFound) {
        	    	sequence.add(null);
        	    }
            }            
        }
        
        return objectSequences;
    }

    private void gotoFirstStep() {
    	gotoStep(1);
    }

    private void gotoPreviousStep() {
    	gotoStep(this.currentStep - 1);
    }

    private void gotoNextStep() {
        gotoStep(this.currentStep + 1);
    }

    private void gotoLastStep() {
        gotoStep((int) this.timeController.getEndTime());
    }

    private void gotoStep(int i) {
    	this.currentStep = i;
    	this.timeController.setCurrentTime(i);
    	this.diagramView.repaint();
    	setStepButtonStates(false);
    }
}