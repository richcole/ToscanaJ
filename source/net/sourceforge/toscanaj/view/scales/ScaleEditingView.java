package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class ScaleEditingView extends JPanel implements BrokerEventListener {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel tableColumnListModel;
    private DefaultListModel scalesListModel;
    private JSplitPane splitPane;
    private JSplitPane leftPane;
    private JPanel diagramView;

    /**
     * Construct an instance of this view
     */
    public ScaleEditingView(JFrame frame, ConceptualSchema conceptualSchema, EventBroker eventBroker) {
        super();
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("ScalesEditingView");

        diagramView = new JPanel();

        leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, makeTableColumnsView(), makeScalesView());
        leftPane.setOneTouchExpandable(true);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, diagramView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
    }

    static class TableColumnPair {
        Table table;
        Column column;

        public TableColumnPair(Table table, Column column) {
            this.table = table;
            this.column = column;
        }

        public String toString() {
            return table.getName() + ":" + column.getName();
        }
    }

    private JComponent makeTableColumnsView() {
        JComponent tableColumnListView;

        tableColumnListModel = new DefaultListModel();
        fillTableColumnsList();

        final JList listView = new JList(tableColumnListModel);
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listView.locationToIndex(e.getPoint());
                }
            }
        };
        listView.addMouseListener(mouseListener);

        JPanel tableColumnPane = new JPanel();
        tableColumnPane.setLayout(new GridBagLayout());
        tableColumnPane.add(new JLabel("Selected tables"),
                new GridBagConstraints(
                        0, 0, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        tableColumnPane.add(new JScrollPane(listView),
                new GridBagConstraints(
                        0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );

        tableColumnListView = tableColumnPane;
        return tableColumnListView;
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
                for (int i = selections.length - 1; i >= 0;  i--) {
                    int selection = selections[i];
                    conceptualSchema.removeDiagram(selection);
                }
            }
        });

        JPanel scalesPane = new JPanel();
        scalesPane.setLayout(new GridBagLayout());
        scalesPane.add(new JLabel("Scales in schema:"),
                new GridBagConstraints(
                        0, 0, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        scalesPane.add(new JScrollPane(listView),
                new GridBagConstraints(
                        0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        scalesPane.add(removeButton,
                new GridBagConstraints(
                        0, 2, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );

        return scalesPane;
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
        if( event instanceof NewConceptualSchemaEvent ) {
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
