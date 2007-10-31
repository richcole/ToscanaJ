/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramReference;
import net.sourceforge.toscanaj.gui.eventhandler.DiagramOrganiserDnDController;
import net.sourceforge.toscanaj.gui.events.DiagramClickedEvent;
import net.sourceforge.toscanaj.model.DiagramCollection;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.events.EventBroker;

import java.awt.*;
import java.awt.event.*;


/**
 * A panel for editing the list of diagrams used.
 *
 * This consists of two list: one which displays the available diagrams in the
 * current conceptual schema ofr selection as further views
 */
public class DiagramOrganiser extends JPanel {
    /**
     * Stores the conceptual schema used for accessing the available diagrams.
     */
    private DiagramCollection schema;

    /**
     * The listview for the available diagrams.
     */
    private JList availableDiagramsListview;

    /**
     * Button to remove a diagram from the history
     */
    private JButton removeButton;

    /**
     * Button to add a Diagram to the history
     */
    private JButton addButton;

    /**
     * The listview for the selected diagrams.
     */
    private DiagramHistoryView selectedDiagramsListview;
    
	public DiagramOrganiser(DiagramCollection conceptualSchema, final EventBroker eventBroker) {
        // store model
        this.schema = conceptualSchema;
		
        // create view components
        this.removeButton = new JButton();
        this.addButton = new JButton();
        JScrollPane availableDiagramsPanel = new JScrollPane();
        JScrollPane selectedDiagramsPanel = new JScrollPane();
        this.availableDiagramsListview = new JList();
        this.availableDiagramsListview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.selectedDiagramsListview = new DiagramHistoryView(DiagramController.getController().getDiagramHistory());
        this.selectedDiagramsListview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
        // label the buttons
        this.addButton.setText("Add Selected");
        this.removeButton.setText("Remove Last");

        // fill the upper listview
        setConceptualSchema(this.schema);

        // create the layout
        GridBagLayout mainLayout = new GridBagLayout();
        setLayout(mainLayout);
        add(availableDiagramsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 50, 800));
        availableDiagramsPanel.getViewport().add(this.availableDiagramsListview, null);

        add(this.addButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

        add(selectedDiagramsPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 50, 800));
        selectedDiagramsPanel.getViewport().add(this.selectedDiagramsListview, null);

        add(this.removeButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        // connect the buttons and lists
        this.addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = DiagramOrganiser.this.availableDiagramsListview.getSelectedIndex();
                if (index >= 0) {
                    DiagramController.getController().addDiagram(DiagramOrganiser.this.schema.getDiagram(index));
                }
            }
        });

        this.removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DiagramController.getController().removeLastDiagram();
            }
        });

        this.availableDiagramsListview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					/// @todo this should be done using an event
					int index = DiagramOrganiser.this.availableDiagramsListview.locationToIndex(e.getPoint());
					Diagram2D diagram = DiagramOrganiser.this.schema.getDiagram(index);
					DiagramController.getController().addDiagram(diagram);
					DiagramOrganiser.this.selectedDiagramsListview.setSelectedIndices(new int[0]);
					e.consume();
				}
			}
		});

        this.selectedDiagramsListview.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = DiagramOrganiser.this.selectedDiagramsListview.getSelectedIndex();
				if (index == -1) {
					return;
				}
				DiagramHistory diagramHistory = DiagramController.getController().getDiagramHistory();
				DiagramReference diagramReference = diagramHistory.getReferenceAt(index); 
				eventBroker.processEvent(new DiagramClickedEvent(diagramReference));            	
				DiagramOrganiser.this.availableDiagramsListview.setSelectedIndices(new int[0]);
			}
		});

        this.availableDiagramsListview.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
				int index = DiagramOrganiser.this.availableDiagramsListview.getSelectedIndex();
                if (index == -1) {
                	DiagramOrganiser.this.addButton.setEnabled(false);
                } else {
					Diagram2D diagram = DiagramOrganiser.this.schema.getDiagram(index);
					eventBroker.processEvent(new DiagramClickedEvent(new DiagramReference(diagram, null)));
					DiagramOrganiser.this.selectedDiagramsListview.setSelectedIndices(new int[0]);
					DiagramOrganiser.this.addButton.setEnabled(true);
                }
            }
        });
        this.addButton.setEnabled(false);

        // The remove button can only be used if a diagram in the history is selected
        DiagramController.getController().getDiagramHistory().addListDataListener(
                new ListDataListener() {
                    public void contentsChanged(ListDataEvent ev) {
                        // nothing to do
                    }

                    public void intervalAdded(ListDataEvent ev) {
                        int size = DiagramController.getController().getDiagramHistory().getSize();
                        DiagramOrganiser.this.removeButton.setEnabled(size != 0);
                    }

                    public void intervalRemoved(ListDataEvent ev) {
                        int size = DiagramController.getController().getDiagramHistory().getSize();
                        DiagramOrganiser.this.removeButton.setEnabled(size != 0);
                    }
                }
        );
        this.removeButton.setEnabled(false);
        
        new DiagramOrganiserDnDController(this.availableDiagramsListview , this.selectedDiagramsListview , this);
    }
    
    /**
     * This changes the conceptual schema used for the list of available diagrams.
     */
    public void setConceptualSchema(DiagramCollection schema) {
        this.schema = schema;
        if (schema == null) {
            this.availableDiagramsListview.removeAll();
            return;
        }
        String[] listEntries = new String[schema.getNumberOfDiagrams()];
        for (int i = 0; i < schema.getNumberOfDiagrams(); i++) {
            listEntries[i] = schema.getDiagram(i).getTitle();
        }
        this.availableDiagramsListview.setListData(listEntries);
    } 
    
    public DiagramCollection getSchema(){
    	return this.schema;
    }
}
