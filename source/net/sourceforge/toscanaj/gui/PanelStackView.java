/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.model.ViewListModel;
import net.sourceforge.toscanaj.controller.ConfigurationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelStackView extends JPanel {

    private ViewListModel viewListModel;
    private JPanel mainPane;
    private JPanel buttonPane;
    private JSplitPane splitPane;

    public void addView(final String displayName, final JPanel view) {
        mainPane.add(view, displayName);
        JButton button = new JButton(displayName);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) mainPane.getLayout();
                layout.show(mainPane, displayName);
            }
        });
        buttonPane.add(button);
    }

    public PanelStackView(JFrame frame) {
        super(new BorderLayout());

        buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));

        mainPane = new JPanel();
        mainPane.setLayout(new CardLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPane, mainPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }
}
