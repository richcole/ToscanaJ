/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import javax.swing.*;
import java.awt.*;

public class LabeledPanel extends JPanel {
    public LabeledPanel(String label, Component contentView) {
        this(label, contentView, null);
    }
    
    public LabeledPanel(String label, Component contentView, Component extraComponent) {
        super();
        setLayout(new GridBagLayout());
        add(new JLabel(label),
                new GridBagConstraints(
                        0, 0, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        add(new JScrollPane(contentView),
                new GridBagConstraints(
                        0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(2, 5, 2, 5),
                        5, 5)
        );
        
        if (extraComponent != null) {
            add(extraComponent,
                    new GridBagConstraints(
                            0, 2, 1, 1, 1.0, 0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(2, 5, 2, 5),
                            5, 5)
            );
        }
    }
}
