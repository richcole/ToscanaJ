/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class ContextTableScaleEditorDialog extends JDialog {
    private boolean result;

    private DatabaseConnection databaseConnection;

    private JTextField scaleTitleField;

    public ContextTableScaleEditorDialog(Frame owner, DatabaseConnection databaseConnection) {
        super(owner);
        this.databaseConnection = databaseConnection;
        createView();
    }

    private void createView() {
        setModal(true);
        setTitle("Context Table Scale Generator");
        getContentPane().setLayout(new GridBagLayout());

        // -- title pane ---
        JPanel titlePane = new JPanel(new GridBagLayout());
        JLabel titleLabel = new JLabel("Title:");
        this.scaleTitleField = new JTextField();
        titlePane.add(
                titleLabel,
                new GridBagConstraints(
                        0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        titlePane.add(
                scaleTitleField,
                new GridBagConstraints(
                        1, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() {
                return 10;
            }
            public int getRowCount() {
                return 10;
            }
            public Object getValueAt(int row, int col) {
                return new Integer(row * col);
            }
        };
        JTable table = new JTable(dataModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollpane = new JScrollPane(table);

        getContentPane().add(
                titlePane,
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        getContentPane().add(
                scrollpane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

        pack();
    }

    public boolean execute() {
        result = false;
        show();
        return result;
    }

    public String getDiagramTitle() {
        return this.scaleTitleField.getText();
    }
}
