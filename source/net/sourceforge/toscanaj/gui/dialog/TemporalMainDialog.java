/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.tockit.canvas.events.CanvasDrawnEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.scales.NumberField;
import net.sourceforge.toscanaj.view.temporal.StateRing;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;

public class TemporalMainDialog extends JDialog implements EventBrokerListener {
	private static final String TRANSITION_LAYER_NAME = "transitions";
    private ManyValuedContext context;
    private EventBroker eventBroker;
    private JComboBox sequenceColumnChooser;
    private JComboBox timelineColumnChooser;
    private JButton addStaticTransitionsButton;
    private JComboBox sequenceToShowChooser;
    private JButton animateTransitionsButton;
    private JButton exportImagesButton;
    private JButton exportAnimationButton;
    private DiagramView diagramView;
    private AnimationTimeController timeController;
    
    private static final Color[] COLORS = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.GRAY, Color.MAGENTA,
                                                         Color.ORANGE, Color.PINK, Color.BLACK, Color.YELLOW};
    private double targetTime;
    private double lastAnimationTime;
    private NumberField speedField;
    private NumberField fadeInField;
    private NumberField holdField;
    private NumberField fadeOutField;
    private ArrayList sequenceValues;
    private ArrayList timelineValues;
	
    public TemporalMainDialog(Frame frame, DiagramView diagramView, EventBroker eventBroker) {
	  	super(frame, "Temporal Controls", false);
	  	this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	  	
	  	this.diagramView = diagramView;
	  	this.eventBroker = eventBroker;
	  	
        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);
        diagramView.getController().getEventBroker().subscribe(this, DisplayedDiagramChangedEvent.class, DiagramView.class);
        diagramView.getController().getEventBroker().subscribe(this, CanvasDrawnEvent.class, Object.class);
	  	
	  	buildGUI();
	  	fillGUI();
    }
    
    private void buildGUI() {
        JLabel sequenceColumnLabel = new JLabel("Sequence Column:");
        sequenceColumnChooser = new JComboBox();
        sequenceColumnChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	calculateValueLists();
                fillSequenceChoosers();
            }
        });

        JLabel timelineLabel = new JLabel("Timeline Column:");
        timelineColumnChooser = new JComboBox();
        timelineColumnChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateValueLists();
                fillSequenceChoosers();
            }
        });

        JLabel sequenceLabel = new JLabel("Sequence:");
        sequenceToShowChooser = new JComboBox();

        addStaticTransitionsButton = new JButton("Show Transitions");
        addStaticTransitionsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addFixedTransitions();
            }
        });

        animateTransitionsButton = new JButton("Animate Transitions");
        animateTransitionsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addAnimatedTransitions();
            }
        });

        exportImagesButton = new JButton("Export Images");
        exportAnimationButton = new JButton("Export Animation");
        
        JLabel speedLabel = new JLabel("Speed (ms/step):");
        speedField = new NumberField(10,NumberField.INTEGER);
        speedField.setText("300");
        JLabel fadeInLabel = new JLabel("Fade-in steps:");
        fadeInField= new NumberField(10,NumberField.FLOAT);
        fadeInField.setText("1");
        JLabel holdLabel = new JLabel("Hold steps:");
        holdField= new NumberField(10,NumberField.FLOAT);
        holdField.setText("1");
        JLabel fadeOutLabel = new JLabel("Fade-out steps:");
        fadeOutField = new NumberField(10,NumberField.FLOAT);
        fadeOutField.setText("5");
        
        Container contentPane = this.getContentPane();
        GridBagLayout layout = new GridBagLayout();
        contentPane.setLayout(layout);

		int row = 0;
        contentPane.add(sequenceColumnLabel, new GridBagConstraints(0, row, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(sequenceColumnChooser, new GridBagConstraints(2, row, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(timelineLabel, new GridBagConstraints(0, row, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(timelineColumnChooser, new GridBagConstraints(2, row, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(sequenceToShowChooser, new GridBagConstraints(1, row, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(addStaticTransitionsButton, new GridBagConstraints(1, row, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(speedLabel, new GridBagConstraints(0, row, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(speedField, new GridBagConstraints(2, row, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(fadeInLabel, new GridBagConstraints(0, row, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(fadeInField, new GridBagConstraints(2, row, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(holdLabel, new GridBagConstraints(0, row, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(holdField, new GridBagConstraints(2, row, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(fadeOutLabel, new GridBagConstraints(0, row, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(fadeOutField, new GridBagConstraints(2, row, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(animateTransitionsButton, new GridBagConstraints(1, row, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(exportAnimationButton, new GridBagConstraints(1, row, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));
        row++;
        contentPane.add(exportImagesButton, new GridBagConstraints(1, row, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));

        this.pack();
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

		calculateValueLists();        
        fillSequenceChoosers();

        setButtonStates(!enabled);
    }

    private void fillSequenceChoosers() {
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
        animateTransitionsButton.setEnabled(enabled);
        exportImagesButton.setEnabled(false);
        exportAnimationButton.setEnabled(false);
    }
    
    public void processEvent(Event e) {
        if(e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
            this.context = csce.getConceptualSchema().getManyValuedContext();
            fillGUI();
            pack();
        }
        if(e instanceof DisplayedDiagramChangedEvent) {
        	setButtonStates(false);
        }
        if(e instanceof CanvasDrawnEvent) {
        	animate();
        }
    }
    
    private void animate() {
    	if(this.timeController == null) {
    		return; // we are not animating
    	} 
    	if(this.lastAnimationTime > this.targetTime) {
    		return; // nothing to animate anymore
    	}
        this.timeController.calculateCurrentTime();
        this.lastAnimationTime = this.timeController.getCurrentTime();
        this.diagramView.repaint(); // paint it again as time has past
    }

    private void addFixedTransitions() {
        int length = this.timelineValues.size();
        AnimationTimeController newTimeController = new AnimationTimeController(length,0,Double.MAX_VALUE,0,1);
        Object selectedSequence = this.sequenceToShowChooser.getSelectedItem();
        if(selectedSequence instanceof AttributeValue) {
        	AttributeValue sequenceValue = (AttributeValue) selectedSequence;
            addTransitions(length, newTimeController, false, sequenceValue);
        } else {
        	addTransitions(length, newTimeController, false, null);
        }
    }

    private void addAnimatedTransitions() {
        int length = this.timelineValues.size();
        double fadeIn = this.fadeInField.getDoubleValue();
        double hold = this.holdField.getDoubleValue();
        double fadeOut = this.fadeOutField.getDoubleValue();
        int speed = this.speedField.getIntegerValue();
        AnimationTimeController newTimeController = new AnimationTimeController(length, fadeIn, hold, fadeOut, speed);
        addTransitions(newTimeController.getAllFadedTime(), newTimeController, true, null);
    }

    private void addTransitions(double newTargetTime, AnimationTimeController newTimeController, 
                                 boolean highlightStates, AttributeValue selectedSequence) {
        if(this.diagramView.hasLayer(TRANSITION_LAYER_NAME)) {
        	this.diagramView.removeLayer(TRANSITION_LAYER_NAME);
        }
        this.diagramView.addLayer(TRANSITION_LAYER_NAME);
        List objectSequences = calculateObjectSequences();
        Hashtable nodeViewMap = createNodeViewMap();
        this.timeController = null;
        Iterator seqIt = objectSequences.iterator();
        Iterator seqValIt = this.sequenceValues.iterator();
        int colNum = 0;
        while (seqIt.hasNext()) {
            List sequence = (List) seqIt.next();
            AttributeValue curSequenceValue = (AttributeValue) seqValIt.next();
            if(this.timeController == null) {
                this.timeController = newTimeController;
            	this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            Color color = COLORS[colNum];
            if(selectedSequence == null || selectedSequence == curSequenceValue) {
            	addTransitions(sequence, new Color(color.getRed(), color.getGreen(), color.getBlue(), 140), nodeViewMap, highlightStates);
            }
            colNum = (colNum + 1) % COLORS.length;
        }
        this.diagramView.repaint();
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
    	NodeView oldView = null;
    	Iterator objectIt = sequence.iterator();
    	int count = 0;
    	objLoop: while (objectIt.hasNext()) {
    		count++;
            FCAObject object = (FCAObject) objectIt.next();
            Iterator nodeIt = diagramView.getDiagram().getNodes();
            while (nodeIt.hasNext()) {
                DiagramNode node = (DiagramNode) nodeIt.next();
                Iterator objIt = node.getConcept().getObjectContingentIterator();
                while (objIt.hasNext()) {
                    FCAObject contObj = (FCAObject) objIt.next();
                    if(contObj.equals(object)) {
                    	NodeView curView = (NodeView) nodeViewMap.get(node);
                    	if(highlightStates) {
	                        this.diagramView.addCanvasItem(new StateRing(curView, color, count, this.timeController),
	                                                       TRANSITION_LAYER_NAME);
                    	}
                    	if(oldView != null && oldView != curView) {
                    	    this.diagramView.addCanvasItem(new TransitionArrow(oldView, curView, color, count - 0.5, this.timeController),
                    	                                   TRANSITION_LAYER_NAME);
                    	}
                    	oldView = curView;
                        continue objLoop;
                    }
                }
            }
        }
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
	        if(!sequenceValues.contains(value)) {
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
	                if(value.isLesserThan(curValue)) {
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
            AttributeValue value = (AttributeValue) seqValIt.next();
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
}