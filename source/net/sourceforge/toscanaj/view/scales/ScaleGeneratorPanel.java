package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.model.DiagramCollection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import util.CollectionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    Map generatorButtonMap = CollectionFactory.createDefaultMap();

    private void fillGeneratorButtonsPane() {
        setLayout(new FlowLayout());
        removeAll();
        generatorButtonMap.clear();

        Iterator it = getScaleGenerators().iterator();
        while (it.hasNext()) {
            final ScaleGenerator generator = (ScaleGenerator) it.next();
            JButton generatorButton = new JButton(generator.getScaleName());
            generatorButton.setEnabled(false);
            generatorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Diagram2D returnValue =
                            generator.generateScale(selectionSource.getSelectedTableColumnPairs(),
                                                    conceptualSchema);
                    if (null != returnValue) {
                        conceptualSchema.addDiagram(returnValue);
                    }
                }
            });
            add(generatorButton);
            setComponentForGenerator(generator, generatorButton);
        }
    }

    private void setComponentForGenerator(ScaleGenerator generator, JComponent generatorButton) {
        generatorButtonMap.put(generator, generatorButton);
    }

    public void updateGeneratorViews(){
        final TableColumnPair[] selectedTableColumnPairs = selectionSource.getSelectedTableColumnPairs();
        Iterator scalesIterator = getScaleGenerators().iterator();
        while (scalesIterator.hasNext()) {
            ScaleGenerator scaleGenerator = (ScaleGenerator) scalesIterator.next();
            JComponent scaleComponent = getComponentForGenerator(scaleGenerator);
            if(null!=scaleComponent){
                scaleComponent.setEnabled(scaleGenerator.canHandleColumns(selectedTableColumnPairs));
            }
        }
    }

    private JComponent getComponentForGenerator(ScaleGenerator scaleGenerator) {
        return (JComponent)generatorButtonMap.get(scaleGenerator);
    }
}
