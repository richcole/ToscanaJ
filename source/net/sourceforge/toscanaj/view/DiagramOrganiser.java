package net.sourceforge.toscanaj.view;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.DiagramHistory;

import net.sourceforge.toscanaj.view.DiagramHistoryView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;

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
    private ConceptualSchema schema;

    /**
     * Stores the list model for the selected diagrams.
     */
    private DiagramHistory history;

    /**
     * The listview for the available diagrams.
     */
    private JList availableDiagramsListview;

    /**
     * Button to remove a diagram from the history
     */
    JButton removeButton;

    /**
     * Button to add a Diagram to the history
     */
    JButton addButton;

    /**
     * The listview for the selected diagrams.
     */
    private DiagramHistoryView selectedDiagramsListview;

    public DiagramOrganiser(ConceptualSchema conceptualSchema, DiagramHistory diagramHistory) {
        // store model
        this.schema = conceptualSchema;
        this.history = diagramHistory;

        // create view components
        removeButton = new JButton();
        addButton = new JButton();
        JScrollPane availableDiagramsPanel = new JScrollPane();
        JScrollPane selectedDiagramsPanel = new JScrollPane();
        availableDiagramsListview = new JList();
        availableDiagramsListview.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        selectedDiagramsListview = new DiagramHistoryView(history);
        selectedDiagramsListview.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // label the buttons
        addButton.setText("Add");
        removeButton.setText("Remove");

        // fill the upper listview
        setConceptualSchema(schema);

        // create the layout
        GridBagLayout mainLayout = new GridBagLayout();
        setLayout(mainLayout);
        add(availableDiagramsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
                ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 50, 800));
        availableDiagramsPanel.getViewport().add(availableDiagramsListview, null);

        add(addButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

        add(selectedDiagramsPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 50, 800));
        selectedDiagramsPanel.getViewport().add(selectedDiagramsListview, null);

        add(removeButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        // connect the buttons and lists
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = availableDiagramsListview.getSelectedIndex();
                if(index >= 0) {
                    history.addFutureDiagram(schema.getDiagram(index));
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = selectedDiagramsListview.getSelectedIndex();
                history.removeDiagram(index);
            }
        });

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = availableDiagramsListview.locationToIndex(e.getPoint());
                    history.addFutureDiagram(schema.getDiagram(index));
                }
            }
        };
        availableDiagramsListview.addMouseListener(mouseListener);

        // The add button can only be used if an available diagram is selected
        availableDiagramsListview.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                if(availableDiagramsListview.getSelectedValue() == null) {
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }
        });
        addButton.setEnabled(false);

        // The remove button can only be used if a diagram in the history is selected
        selectedDiagramsListview.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                if(selectedDiagramsListview.getSelectedValue() == null) {
                    removeButton.setEnabled(false);
                } else {
                    removeButton.setEnabled(true);
                }
            }
        });
        removeButton.setEnabled(false);
    }

    /**
     * This changes the conceptual schema used for the list of available diagrams.
     */
    public void setConceptualSchema(ConceptualSchema schema) {
        this.schema = schema;
        if(schema == null) {
            this.availableDiagramsListview.removeAll();
            return;
        }
        String[] listEntries = new String[schema.getNumberOfDiagrams()];
        for(int i = 0; i < schema.getNumberOfDiagrams(); i++) {
            listEntries[i] = schema.getDiagram(i).getTitle();
        }
        this.availableDiagramsListview.setListData(listEntries);
    }
}