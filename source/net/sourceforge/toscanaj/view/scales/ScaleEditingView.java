package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;

public class ScaleEditingView extends JPanel implements BrokerEventListener {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel columnListModel;
    private JSplitPane splitPane;
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

        JList scalesList = new JList();
        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, makeTableColumnsView(), scalesList);
        leftPane.setOneTouchExpandable(true);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, diagramView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);
    }

    static class TableColumnPair{
        Table table;
        Column column;

        public TableColumnPair(Table table, Column column) {
            this.table = table;
            this.column = column;
        }

        public String toString() {
            return table.getName()+":"+column.getName();
        }
    }

    private JComponent makeTableColumnsView() {
        JComponent tableColumnListView;

        columnListModel = new DefaultListModel();

        columnListModel.removeAllElements();
        java.util.List tables = conceptualSchema.getDbScheme().getTables();
        for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
            Table table = (Table) tableIterator.next();
            java.util.List columns = table.getColumns();
            for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
                Column column = (Column) columnsIterator.next();
                columnListModel.addElement(new TableColumnPair(table,  column));
            }
        }

        final JList listView = new JList(columnListModel);


        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listView.locationToIndex(e.getPoint());
                }
            }
        };



        listView.addMouseListener(mouseListener);

        JPanel tableColumnPane= new JPanel();
        tableColumnPane.setLayout(new BorderLayout());
        tableColumnPane.add(new JLabel("Selected tables"), BorderLayout.NORTH);
        tableColumnPane.add(listView, BorderLayout.CENTER);

        tableColumnListView = tableColumnPane;
        return tableColumnListView;
    }

    public void processEvent(Event e) {

    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }
}
