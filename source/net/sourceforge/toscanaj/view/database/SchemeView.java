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
import net.sourceforge.toscanaj.controller.ConfigurationManager;

import javax.swing.*;
import java.awt.*;

public class SchemeView extends JPanel
{

  JList availableTableListPanel;
  JList selectedTableListPanel;

  public SchemeView(JFrame frame)
  {
      super(new GridLayout(0,1));
      JPanel leftPane = new JPanel(new GridBagLayout());
      JPanel rightPane = new JPanel();

      JScrollPane leftTopPane = new JScrollPane();
      JScrollPane leftBottomPane = new JScrollPane();

      availableTableListPanel = new JList();
      selectedTableListPanel = new JList();

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
  }

}
