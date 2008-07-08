/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class LabeledPanel extends JPanel {
    public LabeledPanel(final String label, final Component contentView) {
        this(label, contentView, new Component[0], true);
    }

    public LabeledPanel(final String label, final Component contentView,
            final boolean scrollable) {
        this(label, contentView, new Component[0], scrollable);
    }

    public LabeledPanel(final String label, final Component contentView,
            final Component extraComponent) {
        this(label, contentView, extraComponent, true);
    }

    public LabeledPanel(final String label, final Component contentView,
            final Component extraComponent, final boolean scrollable) {
        this(label, contentView, new Component[] { extraComponent }, scrollable);
    }

    public LabeledPanel(final String label, final Component contentView,
            final Component[] extraComponents, final boolean scrollable) {
        super();
        setLayout(new GridBagLayout());

        add(new JLabel(label), new GridBagConstraints(0,
                GridBagConstraints.RELATIVE, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 5, 5));

        if (scrollable) {
            add(new JScrollPane(contentView), new GridBagConstraints(0,
                    GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 5, 2, 5), 5, 5));
        } else {
            add(contentView, new GridBagConstraints(0,
                    GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 5, 2, 5), 5, 5));
        }

        for (final Component extraComponent : extraComponents) {
            addExtraComponent(extraComponent);
        }
    }

    public void addExtraComponent(final Component extraComponent) {
        add(extraComponent, new GridBagConstraints(0,
                GridBagConstraints.RELATIVE, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(2, 5, 2, 0), 5, 5));
    }
}
