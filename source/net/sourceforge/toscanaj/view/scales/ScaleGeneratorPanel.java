/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.util.CollectionFactory;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScaleGeneratorPanel extends JPanel implements EventBrokerListener {
	private EventBroker eventBroker;
    private List scaleGenerators = null;
    ConceptualSchema conceptualSchema;
    TableColumnPairsSelectionSource selectionSource;
    DatabaseConnection databaseConnection;

    /**
     * Construct an instance of this view
     */
    public ScaleGeneratorPanel(
            ConceptualSchema conceptualSchema,
            TableColumnPairsSelectionSource selectionSource,
            DatabaseConnection databaseConnection,
            EventBroker eventBroker) {
        super();
        this.eventBroker = eventBroker;
        this.conceptualSchema = conceptualSchema;
        this.databaseConnection = databaseConnection;
        fillGeneratorButtonsPane(eventBroker);

        this.selectionSource = selectionSource;
        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
    }

    public ConceptualSchema getConceptualSchema() {
        return conceptualSchema;
    }

    private List getScaleGenerators() {
        if (scaleGenerators == null) {
            scaleGenerators = CollectionFactory.createDefaultList();
            fillScalesGenerators();
        }
        return scaleGenerators;
    }

    private void fillScalesGenerators() {
        scaleGenerators.add(new OrdinalScaleGenerator(getParentFrame()));
        scaleGenerators.add(new NominalScaleGenerator(getParentFrame()));
        scaleGenerators.add(new ContextTableScaleGenerator(getParentFrame(), this.eventBroker));
        scaleGenerators.add(new AttributeListScaleGenerator(getParentFrame()));
		scaleGenerators.add(new BiordinalScaleGenerator(getParentFrame()));
    }
	
	private Frame getParentFrame() {
		return JOptionPane.getFrameForComponent(this);
	}

    public void processEvent(Event e) {
        if (e instanceof NewConceptualSchemaEvent) {
            NewConceptualSchemaEvent csEvent = (NewConceptualSchemaEvent) e;
            conceptualSchema = csEvent.getConceptualSchema();
        }
    }

    Map generatorButtonMap = CollectionFactory.createDefaultMap();

    private void fillGeneratorButtonsPane(EventBroker eventBroker) {
        setLayout(new FlowLayout());
        removeAll();
        generatorButtonMap.clear();
        
        final JComponent parent = this;

        Iterator it = getScaleGenerators().iterator();
        while (it.hasNext()) {
            final ScaleGenerator generator = (ScaleGenerator) it.next();
            JButton generatorButton = new JButton(generator.getScaleName());
            generatorButton.setEnabled(false);
            generatorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	try {
	                    Context context =
	                		   generator.generateScale(conceptualSchema, databaseConnection);
	                    Diagram2D returnValue = null;
	                    Lattice lattice = null;
	                    if(context!=null){
							LatticeGenerator lgen = new GantersAlgorithm();
							lattice = lgen.createLattice(context);
							returnValue = NDimLayoutOperations.createDiagram(
										  lattice, context.getName(), 
										  new DefaultDimensionStrategy());
							if (null != returnValue) {
							   Diagram2D diagramWithSameTitle = null;
							   int indexOfExistingDiagram = -1;
							   for(int i = 0; i < conceptualSchema.getNumberOfDiagrams(); i++) {
								   if(conceptualSchema.getDiagram(i).getTitle().equalsIgnoreCase(returnValue.getTitle())) {
									   diagramWithSameTitle = conceptualSchema.getDiagram(i);
									   indexOfExistingDiagram = i; 
								   }
							   }
							   if(diagramWithSameTitle != null) {
									   int rv = showTitleExistsDialog(returnValue);
									   if(rv==JOptionPane.OK_OPTION){
										   replaceTitle(returnValue, diagramWithSameTitle, indexOfExistingDiagram);
									   }else if(rv==JOptionPane.CANCEL_OPTION){
										   renameTitle(returnValue, diagramWithSameTitle);
									   }		
							   }else{
								   conceptualSchema.addDiagram(returnValue);
							   }
						   }
	                    }
            	    } catch (Exception exc) {
            	        ErrorDialog.showError(parent, exc, "Scale generation failed");
            	    }
                }
				private void replaceTitle(Diagram2D returnValue, Diagram2D diagramWithSameTitle, int indexOfExistingDiagram) {
					conceptualSchema.addDiagram(returnValue);
					if(indexOfExistingDiagram!=-1){
						conceptualSchema.exchangeDiagrams((conceptualSchema.getNumberOfDiagrams()-1),indexOfExistingDiagram);
						conceptualSchema.removeDiagram(diagramWithSameTitle);
					}else{
						conceptualSchema.removeDiagram(diagramWithSameTitle);
					}
				}
				private void renameTitle(Diagram2D returnValue, Diagram2D diagramWithSameTitle) {
					String inputValue = "";
					String currentValue = returnValue.getTitle(); 
					do {
						inputValue = (String)JOptionPane.showInputDialog(
								null,
								"Enter title: ",
								"Rename title",
								JOptionPane.PLAIN_MESSAGE,
								null,
								null, 
								currentValue);
								if(inputValue!=null){
									inputValue = inputValue.trim();
									currentValue = inputValue;
								}
					} while (inputValue!=null && (inputValue.equals("") || 
						inputValue.equalsIgnoreCase(diagramWithSameTitle.getTitle().trim())));
						//to set the edited title to the Diagram2D
						SimpleLineDiagram lineDiag = (SimpleLineDiagram) returnValue;
						lineDiag.setTitle(inputValue);
						conceptualSchema.addDiagram(lineDiag);
				}
				
                private int showTitleExistsDialog(Diagram2D returnValue){
                	Object[] options;
                	if(returnValue instanceof SimpleLineDiagram){
						options = new Object[]{ "Replace Old Diagram", "Discard New Diagram", "Rename New Diagram" };
                	    return JOptionPane.showOptionDialog(
                	                    parent,
                	                    "A diagram with the title '"+
                	                    returnValue.getTitle()+
                	                    "' already exists.",
                	                    "Title exists",
                	                    JOptionPane.YES_NO_CANCEL_OPTION,
                	                    JOptionPane.ERROR_MESSAGE,
                	                    null,
                	                    options,
                	                    options[2]);
                	} else {
						options = new Object[]{ "Replace Old Diagram", "Discard New Diagram" };
                	    return JOptionPane.showOptionDialog(
                	                    parent,
                	                    "A diagram with the title '"+
                	                    returnValue.getTitle()+
                	                    "' already exists.",
                	                    "Title exists",
                	                    JOptionPane.YES_NO_OPTION,
                	                    JOptionPane.ERROR_MESSAGE,
                	                    null,
                	                    options,
                	                    null);
                	}
                }
            });
            add(generatorButton);
            setComponentForGenerator(generator, generatorButton);
        }
    }

    private void setComponentForGenerator(ScaleGenerator generator, JComponent generatorButton) {
        generatorButtonMap.put(generator, generatorButton);
    }

    public void updateGeneratorViews() {
        final TableColumnPair[] selectedTableColumnPairs = selectionSource.getSelectedTableColumnPairs();
        Iterator scalesIterator = getScaleGenerators().iterator();
        while (scalesIterator.hasNext()) {
            ScaleGenerator scaleGenerator = (ScaleGenerator) scalesIterator.next();
            JComponent scaleComponent = getComponentForGenerator(scaleGenerator);
            if (null != scaleComponent) {
                boolean enabled = scaleGenerator.canHandleColumns(selectedTableColumnPairs) &&
                					this.conceptualSchema.getDatabaseInfo() != null;
                scaleComponent.setEnabled(enabled);
            }
        }
    }

    private JComponent getComponentForGenerator(ScaleGenerator scaleGenerator) {
        return (JComponent) generatorButtonMap.get(scaleGenerator);
    }
}
