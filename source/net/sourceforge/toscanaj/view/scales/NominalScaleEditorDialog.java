/*
 * Created by IntelliJ IDEA.
 * User: Serhiy Yevtushenko
 * Date: Jun 29, 2002
 * Time: 11:38:32 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.batik.ext.swing.GridBagConstants;

public class NominalScaleEditorDialog extends JDialog {

    private Column column;
    private DatabaseConnection databaseConnection;

    private JList columnValueList;
    private JList attributeList;
    private DefaultListModel columnValues;
    private DefaultListModel attributeValues;

    private JTextField  scaleTitleLabel;

    private JButton doneButton;
    private JButton addButton;

    public NominalScaleEditorDialog(Frame owner, Column column, DatabaseConnection databaseConnection) {
        super(owner);
        this.column = column;
        this.databaseConnection = databaseConnection;

        createControls();
        fillControls();
    }

    private void createControls() {
        setModal(true);
        setTitle("Nominal Scale Generator");
        getContentPane().setLayout(new GridBagLayout());

        // -- title pane ---
        this.scaleTitleLabel = new JTextField();
        JPanel titlePane = new JPanel(new GridLayout(1,0));
        titlePane.add(new Label("Scale Title"));
        titlePane.add(scaleTitleLabel);
        getContentPane().add(
            titlePane,
            new GridBagConstraints(
                1, 1, 1, 1, 1, 0,
                GridBagConstants.CENTER,
                GridBagConstants.HORIZONTAL,
                new Insets(5, 5, 5, 5),
                0, 0
            )
        );

        JPanel tablePane = new JPanel(new GridLayout(1,0));
        tablePane.setBorder(BorderFactory.createEtchedBorder());
        this.columnValues = new DefaultListModel();
        this.columnValueList = new JList(columnValues);
        this.attributeValues = new DefaultListModel();
        this.attributeList = new JList(attributeValues);
        tablePane.add(new LabeledScrollPaneView("Values", this.columnValueList));
        tablePane.add(new LabeledScrollPaneView("Attributes", this.attributeList));
        getContentPane().add(
            tablePane,
            new GridBagConstraints(
                1, 2, 1, 1, 1, 1,
                GridBagConstants.CENTER,
                GridBagConstants.BOTH,
                new Insets(5, 5, 5, 5),
                0, 0
            )
        );

        JPanel buttonPane = new JPanel(new GridLayout(1,0));
        buttonPane.add(new JButton("Add"));
        buttonPane.add(new JButton("Cancel"));
        buttonPane.add(new JButton("Done"));
        getContentPane().add(
            buttonPane,
            new GridBagConstraints(
                1, 3, 1, 1, 1, 0,
                GridBagConstants.CENTER,
                GridBagConstants.HORIZONTAL,
                new Insets(5, 5, 5, 5),
                0, 0
            )
        );
        pack();
    }

    private void fillControls()
    {
        // --- get a list of the values in column
        java.util.List resultSet = null;
        try {
            String query = "SELECT DISTINCT " + column.getName() + " FROM " +
                              column.getTable().getName() + ";";
            System.out.println("Query=" + query);
            resultSet = databaseConnection.queryColumn(query, 1);
        } catch (DatabaseException e) {
        }

        for (Iterator it = resultSet.iterator(); it.hasNext();) {
            this.columnValues.addElement((String) it.next());
        }
    };
}
