/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 28, 2002
 * Time: 4:14:58 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.gui.PanelStackView;
import net.sourceforge.toscanaj.model.events.DatabaseSchemaChangedEvent;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.DatabaseSchema;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.util.STD_Iterator;

import javax.swing.*;
import java.awt.*;

public class SchemeView extends JPanel implements BrokerEventListener
{

    DefaultListModel availableTableList;
    DefaultListModel selectedTableList;

    private DatabaseSchema dbScheme;

  public SchemeView(JFrame frame, EventBroker broker)
  {
      super(new GridLayout(0,1));
      JPanel leftPane = new JPanel(new GridBagLayout());
      JPanel rightPane = new JPanel();

      JScrollPane leftTopPane = new JScrollPane();
      JScrollPane leftBottomPane = new JScrollPane();

      this.availableTableList = new DefaultListModel();
      this.selectedTableList = new DefaultListModel();

      JList availableTableListPanel = new JList(this.availableTableList);
      JList selectedTableListPanel = new JList(this.selectedTableList);



      leftTopPane.getViewport().add(availableTableListPanel, null);
      leftBottomPane.getViewport().add(selectedTableListPanel, null);

      leftPane.add(new JLabel("Available Tables"),
          new GridBagConstraints(
                  0, 0, 1, 1, 1.0, 0,
                  GridBagConstraints.CENTER,
                  GridBagConstraints.HORIZONTAL,
                  new Insets(5, 5, 5, 5),
                  5, 5)
        );
      leftPane.add(leftTopPane,
          new GridBagConstraints(
                  0, 1, 1, 1, 1.0, 1.0,
                  GridBagConstraints.CENTER,
                  GridBagConstraints.BOTH,
                  new Insets(5, 5, 5, 5),
                  5, 5)
        );
      leftPane.add(new JLabel("Selected Tables"),
          new GridBagConstraints(
                  0, 2, 1, 1, 1.0, 0,
                  GridBagConstraints.CENTER,
                  GridBagConstraints.HORIZONTAL,
                  new Insets(5, 5, 5, 5),
                  5, 5)
        );
      leftPane.add(leftBottomPane,
          new GridBagConstraints(
                  0, 3, 1, 1, 1.0, 1.0,
                  GridBagConstraints.CENTER,
                  GridBagConstraints.BOTH,
                  new Insets(5, 5, 5, 5),
                  5, 5)
        );

      JSplitPane splitPane;
      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
      splitPane.setOneTouchExpandable(true);
      splitPane.setResizeWeight(0);
      add(splitPane);

      broker.subscribe(this, DatabaseSchemaChangedEvent.class, Object.class);
  }

    class TableInfo {
        Table table;

        public TableInfo(Table table) {
            this.table = table;
        }

        public String toString() {
            return table.getName();
        }
    }


    public void processEvent(Event e) {

        if ( e instanceof DatabaseSchemaChangedEvent ) {
            DatabaseSchemaChangedEvent event = (DatabaseSchemaChangedEvent) e;
            this.dbScheme = event.getDBScheme();

            clear();

            STD_Iterator it = new STD_Iterator(dbScheme.getTables());
            for(it.reset(); !it.atEnd(); it.next()) {
                Table table = (Table)it.val();
                availableTableList.add(0, new TableInfo(table));
            }
        }
    }

    private void clear() {
        this.availableTableList.clear();
        this.availableTableList.clear();
    }
}