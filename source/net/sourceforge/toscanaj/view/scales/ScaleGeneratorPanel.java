package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import util.CollectionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

public class ScaleGeneratorPanel extends JPanel{
    private List scaleGenerators = null;
    private JFrame parentFrame;
    ConceptualSchema conceptualSchema;
    TableColumnPairsSelectionSource selectionSource;

    public JFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * Construct an instance of this view
     */
    public ScaleGeneratorPanel(JFrame frame, ConceptualSchema conceptualSchema, TableColumnPairsSelectionSource selectionSource) {
        super();
        this.parentFrame = frame;
        this.conceptualSchema = conceptualSchema;
        this.selectionSource = selectionSource;
        fillGeneratorButtonsPane();
    }

    private List getScaleGenerators() {
        if (scaleGenerators == null) {
            scaleGenerators = CollectionFactory.createDefaultList();
            fillScalesGenerators();
        }
        return scaleGenerators;
    }

    private void fillScalesGenerators() {
        scaleGenerators.add(new OrdinalScaleGenerator(getParentFrame()));
    }

    private void fillGeneratorButtonsPane() {
        setLayout(new FlowLayout());
        removeAll();
        Iterator it = getScaleGenerators().iterator();
        while (it.hasNext()) {
            final ScaleGenerator generator = (ScaleGenerator) it.next();
            JButton generatorButton = new JButton(generator.getScaleName());
            generatorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Diagram2D returnValue = generator.generateScale(selectionSource.getSelectedTableColumnPairs());
                    if (null != returnValue) {
                        conceptualSchema.addDiagram(returnValue);
                    }
                }
            });
            add(generatorButton);
        }
    }

}
