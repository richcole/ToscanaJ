package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    public ScaleEditingView(JFrame frame, ConceptualSchema conceptualSchema) {
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

    private JComponent makeScalesView() {
        scalesListModel = new DefaultListModel();

        scalesListModel.removeAllElements();
        java.util.List tables = conceptualSchema.getDbScheme().getTables();
        for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
            Table table = (Table) tableIterator.next();
            java.util.List columns = table.getColumns();
            for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
                Column column = (Column) columnsIterator.next();
                scalesListModel.addElement(new TableColumnPair(table, column));
            }
        }

        final JList listView = new JList(scalesListModel);

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

        return scalesPane;
    }

    public void processEvent(Event e) {

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
