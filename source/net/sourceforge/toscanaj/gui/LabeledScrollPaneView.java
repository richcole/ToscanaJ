package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.diagram.NodeMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.IdealMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.FilterMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.SetMovementEventListener;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class LabeledScrollPaneView extends JPanel {
    public LabeledScrollPaneView(String label, Component contentView) {
        this(label, contentView, null);
    }

    public LabeledScrollPaneView(String label, Component contentView, Component extraComponent) {
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
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        if(extraComponent != null) {
            add(extraComponent,
                    new GridBagConstraints(
                            0, 2, 1, 1, 1.0, 0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(5, 5, 5, 5),
                            5, 5)
            );
        }
    }
}
