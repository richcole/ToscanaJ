/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.events.*;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class ScaleEditingView extends JPanel implements BrokerEventListener, TableColumnPairsSelectionSource {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel tableColumnListModel;
    private DefaultListModel scalesListModel;
    private JSplitPane splitPane;
    private JSplitPane leftPane;
    private JList tableColumnsListView;

    private JFrame parentFrame;
    private ScaleGeneratorPanel scaleGeneratorPanel;

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public DefaultListModel getScalesListModel() {
        return scalesListModel;
    }

    /**
     * Construct an instance of this view
     */
    public ScaleEditingView(
            JFrame frame,
            ConceptualSchema conceptualSchema,
            EventBroker eventBroker,
            DatabaseConnection databaseConnection) {
        super();
        this.parentFrame = frame;
        this.conceptualSchema = conceptualSchema;

        setName("ScalesEditingView");
        setLayout(new BorderLayout());

        LabeledScrollPaneView columnListView = new LabeledScrollPaneView("Available Columns:", makeTableColumnListView());
        leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, columnListView, makeScalesView());
        leftPane.setOneTouchExpandable(true);

        scaleGeneratorPanel = makeScaleGeneratorPane(eventBroker, databaseConnection);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, scaleGeneratorPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
    }

    private ScaleGeneratorPanel makeScaleGeneratorPane(
            EventBroker eventBroker,
            DatabaseConnection databaseConnection) {
        return new ScaleGeneratorPanel(
                getParentFrame(),
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
        java.util.List tables = conceptualSchema.getDbScheme().getTables();
        for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
            Table table = (Table) tableIterator.next();
            java.util.List columns = table.getColumns();
            for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
                Column column = (Column) columnsIterator.next();
                tableColumnListModel.addElement(new TableColumnPair(table, column));
            }
        }
    }

    private JComponent makeScalesView() {
        scalesListModel = new DefaultListModel();

        fillScalesList();

        final JList listView = new JList(scalesListModel);
        final JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(false);
        listView.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                removeButton.setEnabled(listView.getSelectedIndex() != -1);
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selections = listView.getSelectedIndices();
                for (int i = selections.length - 1; i >= 0; i--) {
                    int selection = selections[i];
                    conceptualSchema.removeDiagram(selection);
                }
            }
        });

        return new LabeledScrollPaneView("Scales for Selection:", listView, removeButton);
    }

    private void fillScalesList() {
        scalesListModel.removeAllElements();
        for (int i = 0; i < conceptualSchema.getNumberOfDiagrams(); i++) {
            SimpleLineDiagram diagram = (SimpleLineDiagram) conceptualSchema.getDiagram(i);
            scalesListModel.addElement(diagram.getTitle());
        }
    }

    public void processEvent(Event event) {
        ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        if (event instanceof NewConceptualSchemaEvent) {
            conceptualSchema = changeEvent.getConceptualSchema();
            fillTableColumnsList();
        }
        fillScalesList();
    }

    public void setHorizontalDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getHorizontalDividerLocation() {
        return splitPane.getDividerLocation();
    }

    public void setVerticalDividerLocation(int location) {
        leftPane.setDividerLocation(location);
    }

    public int getVerticalDividerLocation() {
        return leftPane.getDividerLocation();
    }
}
