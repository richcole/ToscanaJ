/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Iterator;

public class ScaleEditingView extends JPanel implements EventBrokerListener, TableColumnPairsSelectionSource {
	private EventBroker eventBroker;
    private ConceptualSchema conceptualSchema;
    private DefaultListModel tableColumnListModel;
    private JSplitPane splitPane;
    private JList tableColumnsListView;
    private ScaleGeneratorPanel scaleGeneratorPanel;

    /**
     * Construct an instance of this view
     */
    public ScaleEditingView(
            ConceptualSchema conceptualSchema,
            EventBroker eventBroker,
            DatabaseConnection databaseConnection) {
        super();
        this.eventBroker = eventBroker;
        this.conceptualSchema = conceptualSchema;
        if(this.conceptualSchema.getDatabaseSchema() == null) {
            this.conceptualSchema.setDatabaseSchema(new DatabaseSchema(eventBroker));
        }

        setName("ScalesEditingView");
        setLayout(new BorderLayout());
        
        LabeledPanel columnListView = new LabeledPanel("Available Columns:", makeTableColumnListView());

        scaleGeneratorPanel = makeScaleGeneratorPane(eventBroker, databaseConnection);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, columnListView, scaleGeneratorPanel);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
    }

    private ScaleGeneratorPanel makeScaleGeneratorPane(
            EventBroker eventBroker,
            DatabaseConnection databaseConnection) {
        return new ScaleGeneratorPanel(
                conceptualSchema,
                this,
                databaseConnection,
                eventBroker
        );
    }

    public TableColumnPair[] getSelectedTableColumnPairs() {
        int[] selections = tableColumnsListView.getSelectedIndices();
        TableColumnPair[] ret = new TableColumnPair[selections.length];
        for (int i = 0; i < selections.length; i++) {
            ret[i] = (TableColumnPair) tableColumnListModel.get(selections[i]);
        }
        return ret;
    }

    private JComponent makeTableColumnListView() {
        tableColumnListModel = new DefaultListModel();
        fillTableColumnsList();
        tableColumnsListView = new JList(tableColumnListModel);
        ListSelectionModel listSelectionModel = tableColumnsListView.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.getValueIsAdjusting()) {
                    return;
                }
                scaleGeneratorPanel.updateGeneratorViews();
            }
        });
        return tableColumnsListView;
    }

    private void fillTableColumnsList() {
        tableColumnListModel.removeAllElements();
        java.util.List tables = conceptualSchema.getDatabaseSchema().getTables();
        for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
            Table table = (Table) tableIterator.next();
            java.util.List columns = table.getColumns();
            for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
                Column column = (Column) columnsIterator.next();
                tableColumnListModel.addElement(new TableColumnPair(table, column));
            }
        }
    }

    public void processEvent(Event event) {
        if (event instanceof NewConceptualSchemaEvent) {
            ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
            conceptualSchema = changeEvent.getConceptualSchema();
            if(this.conceptualSchema.getDatabaseSchema() == null) {
                this.conceptualSchema.setDatabaseSchema(new DatabaseSchema(this.eventBroker));
            }
        }
        fillTableColumnsList();
    }

    public void setHorizontalDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getHorizontalDividerLocation() {
        return splitPane.getDividerLocation();
    }
}
