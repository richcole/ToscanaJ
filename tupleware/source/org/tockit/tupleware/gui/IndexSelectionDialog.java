/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.LabeledPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IndexSelectionDialog extends JDialog {
    private JButton okButton;
    private Object[] data;
    private DefaultListModel availableList;
    private DefaultListModel selectedList;

    private JList availableListView;
    private JList selectedListView;
    private JSplitPane mainSplitPane;

    class MoveEntryListSelectionListener implements ListSelectionListener {
        private DefaultListModel fromModel;
        private DefaultListModel toModel;
        
        public MoveEntryListSelectionListener(DefaultListModel fromModel, DefaultListModel toModel) {
            this.fromModel = fromModel;
            this.toModel = toModel;
        }
        
        public void valueChanged(ListSelectionEvent event) {
            if (event.getSource() instanceof JList) {
                JList list = (JList) event.getSource();
                ListSelectionModel selModel = list.getSelectionModel();
                if (selModel.getValueIsAdjusting()) {
                    return;
                }
                if (!selModel.isSelectionEmpty()) {
                    Object data = fromModel.getElementAt(selModel.getLeadSelectionIndex());
                    fromModel.removeElement(data);
                    toModel.addElement(data);
                }
                updateUIStates();
            }
        }
    }
    
    private void updateUIStates() {
    	this.okButton.setEnabled(this.selectedList.size() != 0);
    }
    
    public IndexSelectionDialog(Frame aFrame, String title, Object[] data) {
        super(aFrame, true);
        setTitle(title);
        init(title, data);
        int divPos = ConfigurationManager.fetchInt("IndexSelectionDialog", "verticalDivider", 100);
        mainSplitPane.setDividerLocation(divPos);
        ConfigurationManager.restorePlacement("IndexSelectionDialog", this,
                                              new Rectangle(100, 100, 300, 200));
    }

    public void init(String title, Object[] data) {
        this.data = data;
        this.availableList = new DefaultListModel();
        for (int i = 0; i < data.length; i++) {
            this.availableList.addElement(data[i]);
        }
        this.selectedList = new DefaultListModel();

        this.availableListView = new JList(this.availableList);
        this.selectedListView = new JList(this.selectedList);
        
        this.availableListView.addListSelectionListener(new MoveEntryListSelectionListener(
                                                                this.availableList, this.selectedList));
        this.selectedListView.addListSelectionListener(new MoveEntryListSelectionListener(
                                                                this.selectedList, this.availableList));

        LabeledPanel availableView = new LabeledPanel("Available:", availableListView);
        LabeledPanel selectedView = new LabeledPanel("Selected:", selectedListView);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, availableView, selectedView);
        
        this.okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                hide();
            }
        });
        this.okButton.setEnabled(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel(title), BorderLayout.NORTH);
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }
    
    public void hide() {
        super.hide();
        ConfigurationManager.storeInt("IndexSelectionDialog", "verticalDivider", mainSplitPane.getDividerLocation());
        ConfigurationManager.storePlacement("IndexSelectionDialog", this);
    }
    
    public int[] getSelectedIndices() {
        int[] retVal = new int[this.selectedList.getSize()];
        for(int i= 0; i < this.selectedList.getSize(); i++) {
            Object object = this.selectedList.getElementAt(i);
            for (int j = 0; j < this.data.length; j++) {
                if(object == this.data[j]) {
                    retVal[i] = j;
                }
            }
        }
        return retVal;
    }
}
