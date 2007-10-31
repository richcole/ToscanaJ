/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import javax.swing.*;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemMouseEnterEvent;
import org.tockit.canvas.events.CanvasItemMouseExitEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

/**
 * @todo add some feature to lock the concept, and add scrollpane
 */
public class ReadingHelpDialog extends JDialog {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(ReadingHelpDialog.class);
    private JEditorPane textArea;
    private static final int MAX_OBJECTS = 100;        

    public ReadingHelpDialog(Frame frame, EventBroker diagramEventBroker) {
        super(frame, "Info", false);

        this.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
        
        this.textArea = new JEditorPane();
        this.textArea.setContentType("text/html");
        this.textArea.setEditable(false);
        
        diagramEventBroker.subscribe(new EventBrokerListener() {
            public void processEvent(Event e) {
                ReadingHelpDialog.this.textArea.setText(createDescriptiveText((NodeView) e.getSubject()));
            }
        }, CanvasItemMouseEnterEvent.class, NodeView.class);
        diagramEventBroker.subscribe(new EventBrokerListener() {
            public void processEvent(Event e) {
                ReadingHelpDialog.this.textArea.setText("");
            }
        }, CanvasItemMouseExitEvent.class, NodeView.class);
        diagramEventBroker.subscribe(new EventBrokerListener() {
            public void processEvent(Event e) {
                ReadingHelpDialog.this.textArea.setText(createDescriptiveText(((LabelView) e.getSubject()).getNodeView()));
            }
        }, CanvasItemMouseEnterEvent.class, LabelView.class);
        diagramEventBroker.subscribe(new EventBrokerListener() {
            public void processEvent(Event e) {
                ReadingHelpDialog.this.textArea.setText("");
            }
        }, CanvasItemMouseExitEvent.class, LabelView.class);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this.textArea, BorderLayout.CENTER);
        preferences.restoreWindowPlacement(this, new Rectangle(50,50,200,300));
    }
    
    private String createDescriptiveText(NodeView nodeView) {
        Concept concept = nodeView.getDiagramNode().getConcept();
        ConceptInterpreter conceptInterpreter = nodeView.getDiagramView().getConceptInterpreter();
        ConceptInterpretationContext conceptInterpretationContext = 
            nodeView.getDiagramView().getConceptInterpretationContext();
        
        StringBuffer description = new StringBuffer("<html>");
        description.append("<hr><i>Attributes (");
        description.append(concept.getIntentSize());
        description.append("):</i><hr>");
        Iterator it = concept.getIntentIterator();
        while(it.hasNext()) {
            description.append("- ");
            description.append(it.next().toString());
            description.append("<br>");
        }
        boolean originalObjectMode = conceptInterpretationContext.getObjectDisplayMode();
        conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
        int extentSize = conceptInterpreter.getExtentSize(concept, conceptInterpretationContext);
        description.append("<hr><i>Objects (");
        description.append(extentSize);
        description.append("):</i><hr>");
        if(extentSize <= MAX_OBJECTS) {
            it = conceptInterpreter.getObjectSetIterator(concept, conceptInterpretationContext);
            while(it.hasNext()) {
                description.append("- ");
                description.append(it.next().toString());
                if(it.hasNext()) {
                    description.append("<br>");
                }
            }
        } else {
            description.append("<i>not queried (too many)</i>");
        }
        conceptInterpretationContext.setObjectDisplayMode(originalObjectMode);
        description.append("</html>");
        return description.toString();
    }

    public void closeDialog() {
        preferences.storeWindowPlacement(this);
        this.setVisible(false);
    }
}
