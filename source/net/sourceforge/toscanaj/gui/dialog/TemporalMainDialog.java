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
import javax.swing.JPanel;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;

public class TemporalMainDialog extends JDialog implements EventBrokerListener {
	private static final String TRANSITION_LAYER_NAME = "transitions";
    private ManyValuedContext context;
    private EventBroker eventBroker;
    private JComboBox sequenceChooser;
    private JComboBox timelineChooser;
    private JButton showAllTransitionsButton;
    private JButton exportImagesButton;
    private JButton exportAnimationButton;
    private JButton stepControlsButton;
    private JButton animateControlsButton;
    private DiagramView diagramView;
    
    private static final Color[] COLORS = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.GRAY, Color.MAGENTA,
                                                         Color.ORANGE, Color.PINK, Color.BLACK, Color.YELLOW};
	
    public TemporalMainDialog(Frame frame, DiagramView diagramView, EventBroker eventBroker) {
	  	super(frame, "Temporal Controls", false);
	  	this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	  	
	  	this.diagramView = diagramView;
	  	this.eventBroker = eventBroker;
	  	
        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);
        diagramView.getController().getEventBroker().subscribe(this, DisplayedDiagramChangedEvent.class, DiagramView.class);
	  	
	  	buildGUI();
	  	fillGUI();
    }
    
    private void buildGUI() {
        JLabel sequenceLabel = new JLabel("Sequence Column:");
        sequenceChooser = new JComboBox();

        JLabel timelineLabel = new JLabel("Timeline Column:");
        timelineChooser = new JComboBox();

        showAllTransitionsButton = new JButton("Show All Transitions");
        showAllTransitionsButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		showAllTransitions();
        	}
        });

        exportImagesButton = new JButton("Export Images");
        exportAnimationButton = new JButton("Export Animation");
        stepControlsButton = new JButton("Step Controls");
        animateControlsButton = new JButton("Animate Controls");

        Container contentPane = this.getContentPane();
        GridBagLayout layout = new GridBagLayout();
        contentPane.setLayout(layout);

        contentPane.add(sequenceLabel, new GridBagConstraints(0, 0, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(sequenceChooser, new GridBagConstraints(2, 0, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(timelineLabel, new GridBagConstraints(0, 1, 2, 1, 1, 0,
                                                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(timelineChooser, new GridBagConstraints(2, 1, 2, 1, 1, 0,
                                                        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(new JPanel(), new GridBagConstraints(0, 2, 1, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(showAllTransitionsButton, new GridBagConstraints(1, 2, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(new JPanel(), new GridBagConstraints(3, 2, 1, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(exportImagesButton, new GridBagConstraints(1, 3, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(exportAnimationButton, new GridBagConstraints(1, 4, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(stepControlsButton, new GridBagConstraints(1, 5, 2, 1, 1, 0,
                                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                        new Insets(2,2,2,2), 0, 0));
        contentPane.add(animateControlsButton, new GridBagConstraints(1, 6, 2, 1, 1, 0,
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

        sequenceChooser.setModel(new DefaultComboBoxModel(attributes));
        sequenceChooser.setEnabled(enabled);

        timelineChooser.setModel(new DefaultComboBoxModel(attributes));
        timelineChooser.setEnabled(enabled);

        setButtonStates(!enabled);
    }

    private void setButtonStates(boolean allDisabled) {
        boolean enabled = !allDisabled && (this.diagramView.getDiagram() != null);
        showAllTransitionsButton.setEnabled(enabled);
        exportImagesButton.setEnabled(false);
        exportAnimationButton.setEnabled(false);
        stepControlsButton.setEnabled(false);
        animateControlsButton.setEnabled(false);
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
    }

    private void showAllTransitions() {
    	if(!this.diagramView.hasLayer(TRANSITION_LAYER_NAME)) {
    		this.diagramView.addLayer(TRANSITION_LAYER_NAME);
    	}
        List objectSequences = calculateObjectSequences();
        Hashtable nodeViewMap = createNodeViewMap();
        Iterator seqIt = objectSequences.iterator();
        int colNum = 0;
        while (seqIt.hasNext()) {
            List sequence = (List) seqIt.next();
            Color color = COLORS[colNum];
            showTransitions(sequence, new Color(color.getRed(), color.getGreen(), color.getBlue(), 100), nodeViewMap);
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
    
    private void showTransitions(List sequence, Color color, Hashtable nodeViewMap) {
    	NodeView oldView = null;
    	Iterator objectIt = sequence.iterator();
    	objLoop: while (objectIt.hasNext()) {
            FCAObject object = (FCAObject) objectIt.next();
            Iterator nodeIt = diagramView.getDiagram().getNodes();
            while (nodeIt.hasNext()) {
                DiagramNode node = (DiagramNode) nodeIt.next();
                Iterator objIt = node.getConcept().getObjectContingentIterator();
                while (objIt.hasNext()) {
                    FCAObject contObj = (FCAObject) objIt.next();
                    if(contObj.equals(object)) {
                    	NodeView curView = (NodeView) nodeViewMap.get(node);
                    	if(oldView != null) {
                    		this.diagramView.addCanvasItem(new TransitionArrow(oldView, curView, color), TRANSITION_LAYER_NAME);
                    	}
                    	oldView = curView;
                        continue objLoop;
                    }
                }
            }
        }
    }

    private List calculateObjectSequences() {
        List objectSequences = new ArrayList();
        ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) this.sequenceChooser.getSelectedItem();
        List sequenceValues = new ArrayList();
        
        ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) this.timelineChooser.getSelectedItem();
        List timelineValues = new ArrayList();
        
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
        	    objIt = this.context.getObjects().iterator();
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