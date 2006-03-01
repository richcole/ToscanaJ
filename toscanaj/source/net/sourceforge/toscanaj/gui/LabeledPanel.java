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
        this(label, contentView, new Component[0], true);
    }

    public LabeledPanel(String label, Component contentView, boolean scrollable) {
        this(label, contentView, new Component[0], scrollable);
    }

    public LabeledPanel(String label, Component contentView, Component extraComponent) {
    	this(label,contentView,extraComponent,true);
    }

    public LabeledPanel(String label, Component contentView, Component extraComponent, boolean scrollable) {
        this(label, contentView, new Component[] {extraComponent}, scrollable);
    }
    
    public LabeledPanel(String label, Component contentView, Component[] extraComponents, boolean scrollable) {
        super();
        setLayout(new GridBagLayout());

        add(new JLabel(label),
                new GridBagConstraints(
                        0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 5, 5, 5),
                        5, 5)
        );

		if(scrollable) {
		    add(new JScrollPane(contentView),
		            new GridBagConstraints(
		                    0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0,
		                    GridBagConstraints.CENTER,
		                    GridBagConstraints.BOTH,
		                    new Insets(2, 5, 2, 5),
		                    5, 5)
		    );
		} else {
		    add(contentView,
		            new GridBagConstraints(
		                    0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0,
		                    GridBagConstraints.CENTER,
		                    GridBagConstraints.BOTH,
		                    new Insets(2, 5, 2, 5),
		                    5, 5)
		    );
		}

        for (int i = 0; i < extraComponents.length; i++) {
            addExtraComponent(extraComponents[i]);
        }
    }

    public void addExtraComponent(Component extraComponent) {
        add(extraComponent,
                new GridBagConstraints(
                        0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(2, 5, 2, 0),
                        5, 5)
        );
    }
}
