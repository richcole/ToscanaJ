package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import util.CollectionFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

public class ScaleEditingView extends JPanel implements BrokerEventListener {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel tableColumnListModel;
    private DefaultListModel scalesListModel;
    private JSplitPane splitPane;
    private JSplitPane leftPane;
    private JPanel generatorButtonsPane;
    private List scaleGenerators = null;
    private JList tableColumnsListView;

    private JFrame parentFrame;

    public JFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * Construct an instance of this view
     */
    public ScaleEditingView(JFrame frame, ConceptualSchema conceptualSchema, EventBroker eventBroker) {
        super();
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("ScalesEditingView");

        JComponent scaleGeneratorView = makeScaleGeneratorPane();

        leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, makeTableColumnsView(), makeScalesView());
        leftPane.setOneTouchExpandable(true);


        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, scaleGeneratorView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
    }

    private JComponent makeScaleGeneratorPane() {
        JComponent scaleGeneratorView = null;
        generatorButtonsPane = new JPanel();
        fillGeneratorButtonsPane();
        scaleGeneratorView = generatorButtonsPane;
        return scaleGeneratorView;
    }

    private List getScaleGenerators() {
        if (scaleGenerators == null) {
            scaleGenerators = CollectionFactory.createDefaultList();
            fillScales();
        }
        return scaleGenerators;
    }

    private void fillScales() {
        scaleGenerators.add(new OrdinalScaleGenerator(getParentFrame()));
    }

    private void fillGeneratorButtonsPane() {
        generatorButtonsPane.setLayout(new FlowLayout());
        generatorButtonsPane.removeAll();
        Iterator it = getScaleGenerators().iterator();
        while (it.hasNext()) {
            final ScaleGenerator generator = (ScaleGenerator) it.next();
            JButton generatorButton = new JButton(generator.getScaleName());
            generatorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generator.generateScale(getSelectedColumns());
                }
            });
            generatorButtonsPane.add(generatorButton);
        }
    }

    private TableColumnPair[] getSelectedColumns(){
        int[] selections = tableColumnsListView.getSelectedIndices();
        TableColumnPair[] ret = new TableColumnPair[selections.length];
        for(int i=0;i<selections.length; i++){
            ret[i] = (TableColumnPair)tableColumnListModel.get(selections[i]);
        }
        return ret;
    }

    private JComponent makeTableColumnsView() {
        JComponent tableColumnListView;

        tableColumnListModel = new DefaultListModel();
        fillTableColumnsList();

        tableColumnsListView = new JList(tableColumnListModel);
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = tableColumnsListView.locationToIndex(e.getPoint());
                }
            }
        };
        tableColumnsListView.addMouseListener(mouseListener);

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
        tableColumnPane.add(new JScrollPane(tableColumnsListView),
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
                for (int i = selections.length - 1; i >= 0; i--) {
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
