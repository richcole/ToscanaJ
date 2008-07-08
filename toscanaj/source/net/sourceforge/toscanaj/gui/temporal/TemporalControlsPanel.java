/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.temporal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.TitledBorder;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.temporal.TransitionArrowManipulator;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.scales.NumberField;
import net.sourceforge.toscanaj.view.temporal.ArrowStyle;
import net.sourceforge.toscanaj.view.temporal.InterSequenceTransitionArrow;
import net.sourceforge.toscanaj.view.temporal.StateRing;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;

import org.tockit.canvas.events.CanvasDrawnEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;
import org.tockit.datatype.Value;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * @todo instead of fiddling around everywhere there should be a proper subclass
 *       of SimpleLineDiagram handling all the temporal stuff. Maybe even a
 *       separate program would be useful. The current hacks like attaching the
 *       extra canvas items to the diagram and saving those (instead of the
 *       information how to create them) is pretty bad.
 */
public class TemporalControlsPanel extends JTabbedPane implements
        EventBrokerListener {
    private static final Insets DEFAULT_SPACER_INSETS = new Insets(0, 0, 10, 0);
    private static final Insets DEFAULT_BUTTON_INSETS = new Insets(2, 16, 2, 16);
    private static final Insets DEFAULT_LABEL_INSETS = new Insets(2, 2, 2, 2);
    private static final Insets DEFAULT_FIELD_INSETS = new Insets(2, 20, 2, 2);

    private ManyValuedContext context;
    private JComboBox sequenceColumnChooser;
    private JComboBox timelineColumnChooser;
    private JButton addStaticTransitionsButton;
    private JComboBox sequenceToShowChooser;
    private JButton animateTransitionsButton;
    private JButton exportImagesButton;
    private final DiagramView diagramView;
    private final AnimationTimeController timeController;

    private double targetTime;
    private double lastAnimationTime;
    private int currentStep;
    private NumberField speedField;
    private NumberField fadeInField;
    private NumberField holdField;
    private NumberField fadeOutField;
    private List<Value> sequenceValues;
    private List<Value> timelineValues;
    private JCheckBox serializeSequencesBox;
    private File lastImageExportFile;
    private final DiagramExportSettings diagramExportSettings;
    private JButton firstStepButton;
    private JButton previousStepButton;
    private JButton nextStepButton;
    private JButton lastStepButton;
    private JLabel stepPositionLabel;
    private JButton startSteppingButton;
    private JButton removeTransitionsButton;
    private boolean animating;

    public TemporalControlsPanel(final DiagramView diagramView,
            final DiagramExportSettings diagramExportSettings,
            final EventBroker eventBroker) {
        super();

        this.diagramView = diagramView;

        // make sure the factories for the extra canvas items are loaded
        TransitionArrow.registerFactory();
        InterSequenceTransitionArrow.registerFactory();
        StateRing.registerFactory();

        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class,
                Object.class);
        diagramView.getController().getEventBroker().subscribe(this,
                DisplayedDiagramChangedEvent.class, DiagramView.class);
        diagramView.getController().getEventBroker().subscribe(this,
                CanvasDrawnEvent.class, Object.class);
        new TransitionArrowManipulator(diagramView, diagramView.getController()
                .getEventBroker());

        this.diagramExportSettings = diagramExportSettings;
        this.timeController = new AnimationTimeController(0, 0, 0, 0, 0);

        buildGUI();
        fillGUI();
    }

    private void buildGUI() {
        this.addTab("Controls", createControlsPanel());
        this.addTab("Data", createBasicSettingsPanel());
        this.addTab("Arrows", createArrowSettingsPanel());
        this.addTab("Options", createAnimationsSettingsPanel());
    }

    private Component createArrowSettingsPanel() {
        final JList listView = new JList(DiagramSchema.getCurrentSchema()
                .getArrowStyles());
        listView.setCellRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(final JList list,
                    final Object value, final int index,
                    final boolean isSelected, final boolean cellHasFocus) {
                final ArrowStyle style = (ArrowStyle) value;
                final JPanel retVal = new JPanel() {
                    @Override
                    protected void paintComponent(final Graphics g) {
                        super.paintComponent(g);
                        final Graphics2D g2d = (Graphics2D) g;
                        final AffineTransform oldTransform = g2d.getTransform();
                        final Paint oldPaint = g2d.getPaint();
                        final Stroke oldStroke = g2d.getStroke();

                        final Shape arrow = TransitionArrow.getArrowShape(
                                style, this.getWidth() * 0.9);
                        g2d.setPaint(style.getColor());
                        g2d.translate(this.getWidth() * 0.95,
                                this.getHeight() / 2);
                        g2d.fill(arrow);
                        if (style.getBorderWidth() != 0) {
                            g2d.setStroke(new BasicStroke(style
                                    .getBorderWidth()));
                            g2d.setPaint(Color.BLACK);
                            g2d.draw(arrow);
                        }

                        g2d.setPaint(oldPaint);
                        g2d.setStroke(oldStroke);
                        g2d.setTransform(oldTransform);
                    }
                };
                final Dimension size = new Dimension(150, 30);
                retVal.setMinimumSize(size);
                retVal.setPreferredSize(size);
                return retVal;
            }
        });
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                final int index = listView.getSelectedIndex();
                final ListModel listModel = listView.getModel();
                final ArrowStyle style = (ArrowStyle) listModel
                        .getElementAt(index);
                final ArrowStyle newStyle = ArrowStyleChooser.showDialog(
                        listView, "Edit arrow style", style);
                if (newStyle != null) {
                    style.copyValues(newStyle);
                    DiagramSchema.getCurrentSchema().store();
                    diagramView.repaint();
                }
            }
        });
        return new JScrollPane(listView);
    }

    private JPanel createControlsPanel() {
        addStaticTransitionsButton = new JButton("Show all transitions");
        addStaticTransitionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addFixedTransitions();
            }
        });

        startSteppingButton = new JButton("Start stepping");
        startSteppingButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                startStepping();
            }
        });

        animateTransitionsButton = new JButton("Animate transitions");
        animateTransitionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addAnimatedTransitions();
            }
        });

        removeTransitionsButton = new JButton("Remove transitions");
        removeTransitionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                removeTransitions();
            }
        });

        exportImagesButton = new JButton("Export all steps as images");
        exportImagesButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                exportImages();
            }
        });

        final GridBagConstraints buttonConstraints = new GridBagConstraints(0,
                GridBagConstraints.RELATIVE, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                DEFAULT_BUTTON_INSETS, 0, 0);

        final JPanel panel = new JPanel(new GridBagLayout());
        panel.add(addStaticTransitionsButton, buttonConstraints);
        panel.add(removeTransitionsButton, buttonConstraints);
        panel.add(exportImagesButton, buttonConstraints);
        panel.add(animateTransitionsButton, buttonConstraints);
        panel.add(startSteppingButton, buttonConstraints);
        panel.add(createStepPanel(), new GridBagConstraints(0,
                GridBagConstraints.RELATIVE, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                DEFAULT_SPACER_INSETS, 0, 0));
        panel.add(new JPanel(), new GridBagConstraints(0,
                GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                DEFAULT_SPACER_INSETS, 0, 0));
        return panel;
    }

    protected void removeTransitions() {
        final SimpleLineDiagram diagram = (SimpleLineDiagram) this.diagramView
                .getDiagram();
        diagram.removeExtraCanvasItems();
        this.diagramView.updateDiagram();
    }

    private JPanel createBasicSettingsPanel() {
        final JLabel sequenceColumnLabel = new JLabel("Sequence Column:");
        sequenceColumnChooser = new JComboBox();
        sequenceColumnChooser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                calculateValueLists();
                fillSequenceChooser();
            }
        });

        final JLabel timelineLabel = new JLabel("Timeline Column:");
        timelineColumnChooser = new JComboBox();
        timelineColumnChooser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                calculateValueLists();
                fillSequenceChooser();
            }
        });

        final JLabel sequenceLabel = new JLabel("Sequence:");
        sequenceToShowChooser = new JComboBox();

        serializeSequencesBox = new JCheckBox(
                "Serialize when stepping/animating");

        final JPanel basicSettingsPanel = new JPanel(new GridBagLayout());
        int r = 0;
        basicSettingsPanel.add(sequenceColumnLabel, new GridBagConstraints(0,
                r, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        basicSettingsPanel.add(sequenceColumnChooser, new GridBagConstraints(0,
                r, 1, 1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        basicSettingsPanel.add(timelineLabel, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        basicSettingsPanel.add(timelineColumnChooser, new GridBagConstraints(0,
                r, 1, 1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        basicSettingsPanel.add(sequenceLabel, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        basicSettingsPanel.add(sequenceToShowChooser, new GridBagConstraints(0,
                r, 1, 1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        basicSettingsPanel.add(serializeSequencesBox, new GridBagConstraints(0,
                r, 1, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        r++;
        basicSettingsPanel.add(new JPanel(), new GridBagConstraints(0, r, 1, 1,
                1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                DEFAULT_SPACER_INSETS, 0, 0));
        return basicSettingsPanel;
    }

    private JPanel createAnimationsSettingsPanel() {
        final JLabel speedLabel = new JLabel("Speed (ms/step):");
        speedField = new NumberField(10, NumberField.INTEGER);
        speedField.setText("200");
        final JLabel fadeInLabel = new JLabel("Fade-in steps:");
        fadeInField = new NumberField(10, NumberField.FLOAT);
        fadeInField.setText("1");
        final JLabel holdLabel = new JLabel("Hold steps:");
        holdField = new NumberField(10, NumberField.FLOAT);
        holdField.setText("1");
        final JLabel fadeOutLabel = new JLabel("Fade-out steps:");
        fadeOutField = new NumberField(10, NumberField.FLOAT);
        fadeOutField.setText("5");

        final JPanel animationSettingsPanel = new JPanel(new GridBagLayout());
        int r = 0;
        animationSettingsPanel.add(speedLabel, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(speedField, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(fadeInLabel, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(fadeInField, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(holdLabel, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(holdField, new GridBagConstraints(0, r, 1,
                1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(fadeOutLabel, new GridBagConstraints(0, r,
                1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, DEFAULT_LABEL_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(fadeOutField, new GridBagConstraints(0, r,
                1, 1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, DEFAULT_FIELD_INSETS, 0, 0));
        r++;
        animationSettingsPanel.add(new JPanel(), new GridBagConstraints(0, r,
                1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                DEFAULT_SPACER_INSETS, 0, 0));
        return animationSettingsPanel;
    }

    private JPanel createStepPanel() {
        firstStepButton = new JButton("<<");
        firstStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                gotoFirstStep();
            }
        });

        previousStepButton = new JButton("<");
        previousStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                gotoPreviousStep();
            }
        });

        nextStepButton = new JButton(">");
        nextStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                gotoNextStep();
            }
        });

        lastStepButton = new JButton(">>");
        lastStepButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                gotoLastStep();
            }
        });

        stepPositionLabel = new JLabel("0/0");

        final JPanel stepPanel = new JPanel(new GridBagLayout());
        stepPanel.add(firstStepButton, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        2, 2, 2, 2), 5, 0));
        stepPanel.add(previousStepButton, new GridBagConstraints(1, 0, 1, 1, 1,
                0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 5, 0));
        stepPanel.add(nextStepButton, new GridBagConstraints(2, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        2, 2, 2, 2), 5, 0));
        stepPanel.add(lastStepButton, new GridBagConstraints(3, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        2, 2, 2, 2), 5, 0));
        stepPanel.add(stepPositionLabel, new GridBagConstraints(4, 0, 1, 1, 1,
                0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 5, 0));
        stepPanel.setBorder(createTitledBorder("Step controls"));
        return stepPanel;
    }

    private TitledBorder createTitledBorder(final String title) {
        return BorderFactory.createTitledBorder(BorderFactory
                .createLineBorder(SystemColor.controlDkShadow), title);
    }

    private void fillGUI() {
        Object[] attributes;
        boolean enabled;
        if (context != null) {
            attributes = this.context.getAttributes().toArray();
            enabled = true;
        } else {
            attributes = new Object[] { "No context available" };
            enabled = false;
        }

        sequenceColumnChooser.setModel(new DefaultComboBoxModel(attributes));
        sequenceColumnChooser.setEnabled(enabled);

        timelineColumnChooser.setModel(new DefaultComboBoxModel(attributes));
        timelineColumnChooser.setEnabled(enabled);
        if (context != null) {
            timelineColumnChooser.setSelectedIndex(Math.min(
                    attributes.length - 1, 1));
        }

        calculateValueLists();
        fillSequenceChooser();

        setButtonStates(!enabled);
    }

    private void fillSequenceChooser() {
        final DefaultComboBoxModel listModel = new DefaultComboBoxModel();
        listModel.addElement("<All Sequences>");
        final Iterator<Value> it = this.sequenceValues.iterator();
        while (it.hasNext()) {
            final Value value = it.next();
            listModel.addElement(value);
        }

        this.sequenceToShowChooser.setModel(listModel);
    }

    private void setButtonStates(final boolean allDisabled) {
        final boolean enabled = !allDisabled
                && (this.diagramView.getDiagram() != null);
        addStaticTransitionsButton.setEnabled(enabled);
        removeTransitionsButton.setEnabled(enabled);
        startSteppingButton.setEnabled(enabled);
        animateTransitionsButton.setEnabled(enabled);
        exportImagesButton.setEnabled(enabled
                && this.diagramExportSettings != null);
        setStepButtonStates(!enabled);
    }

    private void setStepButtonStates(final boolean allDisabled) {
        firstStepButton.setEnabled(!allDisabled && this.currentStep > 1);
        previousStepButton.setEnabled(!allDisabled && this.currentStep > 1);
        nextStepButton.setEnabled(!allDisabled
                && this.currentStep < this.timeController.getEndTime());
        lastStepButton.setEnabled(!allDisabled
                && this.currentStep < this.timeController.getEndTime());
        final int endTime = (int) this.timeController.getEndTime();
        this.stepPositionLabel.setText(this.currentStep + "/" + endTime);
    }

    /**
     * @todo this is reduced code from the image export in the
     *       ToscanaJMainPanel. Extract the full code into a new object, and use
     *       it here.
     */
    protected void exportImages() {
        disableStepControls();

        final JFileChooser saveDialog = new JFileChooser(
                this.lastImageExportFile);
        boolean formatDefined;
        do {
            formatDefined = true;
            final int rv = saveDialog.showSaveDialog(this);
            if (rv == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = saveDialog.getSelectedFile();
                final GraphicFormat format = GraphicFormatRegistry
                        .getTypeByExtension(selectedFile);
                if (format != null) {
                    this.diagramExportSettings.setGraphicFormat(format);
                } else {
                    if (selectedFile.getName().indexOf('.') != -1) {
                        JOptionPane
                                .showMessageDialog(
                                        this,
                                        "Sorry, no type with this extension known.\n"
                                                + "Please use either another extension or try\n"
                                                + "manual settings.",
                                        "Export failed",
                                        JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane
                                .showMessageDialog(
                                        this,
                                        "No extension given.\n"
                                                + "Please give an extension or pick a file type\n"
                                                + "from the options.",
                                        "Export failed",
                                        JOptionPane.ERROR_MESSAGE);
                    }
                    formatDefined = false;
                }

                if (formatDefined) {
                    exportImages(selectedFile);
                }
            }
        } while (formatDefined == false);
    }

    protected void exportImages(final File selectedFile) {
        removeTransitions();

        // export all transitions into main file
        final int length = this.timelineValues.size();
        this.timeController.setEndTime(length);
        this.timeController.setFadeInTime(0);
        this.timeController.setVisibleTime(Double.MAX_VALUE);
        this.timeController.setFadeOutTime(0);
        this.timeController.setMillisecondsPerStep(1);
        this.timeController.reset();
        addTransitions(this.timeController.getEndTime(), false);
        this.timeController.setCurrentTime(this.timeController.getEndTime());
        exportImage(selectedFile);
        this.lastImageExportFile = selectedFile;

        this.timeController.setVisibleTime(1);

        if (this.serializeSequencesBox.isSelected()
                && !(this.sequenceToShowChooser.getSelectedItem() instanceof Value)) {
            final int numSeq = this.sequenceValues.size();
            this.timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(this.timeController.getAllFadedTime(),
                    true);
        } else {
            this.timeController.setEndTime(length);
            addTransitions(this.timeController.getAllFadedTime(), true);
        }

        final double targetStep = this.timeController.getAllFadedTime();

        for (int i = 0; i <= targetStep; i++) {
            this.timeController.setCurrentTime(i);
            exportImage(new File(getNumberedFileName(selectedFile, i,
                    targetStep)));
        }
    }

    private String getNumberedFileName(final File selectedFile, final int step,
            final double targetStep) {
        final String origName = selectedFile.getAbsolutePath();
        final int dotPos = origName.lastIndexOf('.');
        double countdown = targetStep;
        String fillingZeroes = "";
        while (countdown >= 1) {
            fillingZeroes += "0";
            countdown /= 10;
        }
        final String currentPos = String.valueOf(step);
        return origName.substring(0, dotPos) + "-"
                + fillingZeroes.substring(currentPos.length()) + currentPos
                + origName.substring(dotPos);
    }

    protected void exportImage(final File file) {
        try {
            this.diagramExportSettings.getGraphicFormat().getWriter()
                    .exportGraphic(this.diagramView,
                            this.diagramExportSettings, file, new Properties());
        } catch (final ImageGenerationException e) {
            ErrorDialog.showError(this, e, "Exporting image error");
        } catch (final OutOfMemoryError e) {
            ErrorDialog.showError(this, e, "Out of memory",
                    "Not enough memory available to export\n"
                            + "the diagram in this size");
        }
    }

    public void processEvent(final Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            final ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
            this.context = csce.getConceptualSchema().getManyValuedContext();
            fillGUI();
        }
        if (e instanceof DisplayedDiagramChangedEvent) {
            disableStepControls();
            setButtonStates(false);
        }
        if ((e instanceof CanvasDrawnEvent) && this.animating) {
            animate();
        }
    }

    public void disableStepControls() {
        this.currentStep = 0;
        this.timeController.setEndTime(0);
        setStepButtonStates(true);
    }

    private void animate() {
        if (this.lastAnimationTime > this.targetTime) {
            removeTransitions();
            this.animating = false;
            return;
        }
        this.timeController.calculateCurrentTime();
        this.lastAnimationTime = this.timeController.getCurrentTime();
        this.diagramView.repaint(); // paint it again as time has past
    }

    private void addFixedTransitions() {
        this.animating = false;
        removeTransitions();

        disableStepControls();

        final int length = this.timelineValues.size();
        this.timeController.setEndTime(length);
        this.timeController.setFadeInTime(0);
        this.timeController.setVisibleTime(Double.MAX_VALUE);
        this.timeController.setFadeOutTime(0);
        this.timeController.setMillisecondsPerStep(1);
        this.timeController.reset();
        this.timeController.setCurrentTime(length + 1);
        addTransitions(length, false);

        this.diagramView.updateDiagram();
    }

    private void startStepping() {
        this.animating = false;
        removeTransitions();

        final int length = this.timelineValues.size();
        this.timeController.setFadeInTime(0);
        this.timeController.setVisibleTime(1);
        this.timeController.setFadeOutTime(0);
        this.timeController.setMillisecondsPerStep(1);
        if (this.serializeSequencesBox.isSelected()
                && !(this.sequenceToShowChooser.getSelectedItem() instanceof Value)) {
            final int numSeq = this.sequenceValues.size();
            this.timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(this.timeController.getAllFadedTime(),
                    true);
        } else {
            this.timeController.setEndTime(length);
            addTransitions(this.timeController.getAllFadedTime(), true);
        }
        this.diagramView.updateDiagram();
        gotoStep(1);
    }

    private void addAnimatedTransitions() {
        this.animating = false;

        removeTransitions();

        disableStepControls();

        final int length = this.timelineValues.size();
        this.timeController.setFadeInTime(this.fadeInField.getDoubleValue());
        this.timeController.setVisibleTime(this.holdField.getDoubleValue());
        this.timeController.setFadeOutTime(this.fadeOutField.getDoubleValue());
        this.timeController.setMillisecondsPerStep(this.speedField
                .getIntegerValue());
        this.timeController.reset();
        if (this.serializeSequencesBox.isSelected()
                && !(this.sequenceToShowChooser.getSelectedItem() instanceof Value)) {
            final int numSeq = this.sequenceValues.size();
            this.timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(this.timeController.getAllFadedTime(),
                    true);
        } else {
            this.timeController.setEndTime(length);
            addTransitions(this.timeController.getAllFadedTime(), true);
        }
        this.animating = true;
        this.diagramView.updateDiagram();
    }

    private void addTransitions(final double newTargetTime,
            final boolean highlightStates) {
        Value selectedSequence = null; // no specific sequence selected
        final Object selectedSequenceItem = this.sequenceToShowChooser
                .getSelectedItem();
        if (selectedSequenceItem instanceof Value) {
            selectedSequence = (Value) selectedSequenceItem;
        }

        final List<ArrayList<FCAElement>> objectSequences = calculateObjectSequences();
        final Iterator<ArrayList<FCAElement>> seqIt = objectSequences
                .iterator();
        final Iterator<Value> seqValIt = this.sequenceValues.iterator();
        int styleNum = 0;
        boolean start = true;
        while (seqIt.hasNext()) {
            final List<FCAElement> sequence = seqIt.next();
            final Value curSequenceValue = seqValIt.next();
            if (start) {
                start = false;
                this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            final ArrowStyle[] styles = DiagramSchema.getCurrentSchema()
                    .getArrowStyles();
            if (selectedSequence == null
                    || selectedSequence == curSequenceValue) {
                addTransitions(sequence, styles[styleNum], highlightStates, 0);
            }
            styleNum = (styleNum + 1) % styles.length;
        }
    }

    /**
     * @todo Copy and paste code, refactor. Maybe it is best to add the
     *       transitions once they are known and not every time a button is
     *       pressed. The buttons would then change only the time controller and
     *       not affect the canvas items directly.
     */
    private void addTransitionsSerialized(final double newTargetTime,
            final boolean highlightStates) {
        Value selectedSequence = null; // no specific sequence selected
        final Object selectedSequenceItem = this.sequenceToShowChooser
                .getSelectedItem();
        if (selectedSequenceItem instanceof Value) {
            selectedSequence = (Value) selectedSequenceItem;
        }

        final List<ArrayList<FCAElement>> objectSequences = calculateObjectSequences();
        final Iterator<ArrayList<FCAElement>> seqIt = objectSequences
                .iterator();
        final Iterator<Value> seqValIt = this.sequenceValues.iterator();
        int seqNum = 0;
        final int seqLength = this.timelineValues.size();
        List<FCAElement> lastSequence = null;
        final ArrowStyle[] styles = DiagramSchema.getCurrentSchema()
                .getArrowStyles();
        ArrowStyle style = null;
        while (seqIt.hasNext()) {
            final List<FCAElement> sequence = seqIt.next();

            if (lastSequence != null) {
                final Color nextColor = styles[seqNum % styles.length]
                        .getColor();
                final DiagramNode endLast = findObjectConceptNode(lastSequence
                        .get(lastSequence.size() - 1));
                final DiagramNode startNew = findObjectConceptNode(sequence
                        .get(0));
                if (endLast == null) {
                    continue;
                }
                if (endLast != startNew) {
                    final SimpleLineDiagram diagram = (SimpleLineDiagram) this.diagramView
                            .getDiagram();
                    diagram
                            .addExtraCanvasItem(new InterSequenceTransitionArrow(
                                    endLast, startNew, style, nextColor, seqNum
                                            * seqLength + 0.5,
                                    this.timeController));
                }
            }

            style = styles[seqNum % styles.length];
            final Value curSequenceValue = seqValIt.next();
            if (lastSequence == null) {
                this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            if (selectedSequence == null
                    || selectedSequence == curSequenceValue) {
                addTransitions(sequence, style, highlightStates, seqNum
                        * seqLength);
            }
            seqNum++;
            lastSequence = sequence;
        }
    }

    private void addTransitions(final List<FCAElement> sequence,
            final ArrowStyle style, final boolean highlightStates,
            final int countStart) {
        final SimpleLineDiagram diagram = (SimpleLineDiagram) this.diagramView
                .getDiagram();
        DiagramNode oldNode = null;
        final Iterator<FCAElement> objectIt = sequence.iterator();
        int count = countStart;
        while (objectIt.hasNext()) {
            count++;
            final FCAElement object = objectIt.next();
            final DiagramNode curNode = findObjectConceptNode(object);
            if (curNode == null) {
                continue;
            }
            if (highlightStates) {
                diagram.addExtraCanvasItem(new StateRing(curNode, style
                        .getColor(), count, this.timeController));
            }
            if (oldNode != null && oldNode != curNode) {
                diagram.addExtraCanvasItem(new TransitionArrow(oldNode,
                        curNode, style, count - 0.5, this.timeController));
            }
            oldNode = curNode;
        }
    }

    private DiagramNode findObjectConceptNode(final FCAElement object) {
        final Iterator<DiagramNode> nodeIt = this.diagramView.getDiagram()
                .getNodes();
        final ConceptInterpreter conceptInterpreter = this.diagramView
                .getConceptInterpreter();
        final ConceptInterpretationContext interpretationContext = this.diagramView
                .getConceptInterpretationContext();
        while (nodeIt.hasNext()) {
            final DiagramNode node = nodeIt.next();
            // resolve nesting
            ConceptInterpretationContext curContext = interpretationContext;
            DiagramNode curNode = node.getOuterNode();
            while (curNode != null) {
                curContext = curContext.createNestedContext(curNode
                        .getConcept());
                curNode = curNode.getOuterNode();
            }
            // try finding the object in nested context
            final Iterator objIt = conceptInterpreter.getObjectSetIterator(node
                    .getConcept(), curContext);
            while (objIt.hasNext()) {
                final FCAElement contObj = (FCAElement) objIt.next();
                if (contObj.equals(object)) {
                    return node;
                }
            }
        }
        return null;
    }

    private void calculateValueLists() {
        sequenceValues = new ArrayList<Value>();
        timelineValues = new ArrayList<Value>();

        final Object selectedSequenceColumn = this.sequenceColumnChooser
                .getSelectedItem();
        if (!(selectedSequenceColumn instanceof ManyValuedAttribute)) {
            return;
        }
        final ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) selectedSequenceColumn;
        final ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) this.timelineColumnChooser
                .getSelectedItem();

        final Iterator<FCAElement> objIt = this.context.getObjects().iterator();
        while (objIt.hasNext()) {
            final FCAElement object = objIt.next();
            Value value = this.context.getRelationship(object,
                    sequenceAttribute);
            if (!sequenceValues.contains(value) && value != null) {
                boolean inserted = false;
                final ListIterator<Value> seqIt = sequenceValues.listIterator();
                while (seqIt.hasNext()) {
                    final Value curValue = seqIt.next();
                    if (value.isLesserThan(curValue)) {
                        if (seqIt.hasPrevious()) {
                            seqIt.previous();
                        }
                        seqIt.add(value);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    seqIt.add(value);
                }
            }
            value = this.context.getRelationship(object, timelineAttribute);
            if (!timelineValues.contains(value)) {
                boolean inserted = false;
                final ListIterator<Value> tlIt = timelineValues.listIterator();
                while (tlIt.hasNext()) {
                    final Value curValue = tlIt.next();
                    if (curValue != null && value.isLesserThan(curValue)) {
                        if (tlIt.hasPrevious()) {
                            tlIt.previous();
                        }
                        tlIt.add(value);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    tlIt.add(value);
                }
            }
        }
    }

    private List<ArrayList<FCAElement>> calculateObjectSequences() {
        final ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) this.sequenceColumnChooser
                .getSelectedItem();
        final ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) this.timelineColumnChooser
                .getSelectedItem();

        final List<ArrayList<FCAElement>> objectSequences = new ArrayList<ArrayList<FCAElement>>();

        // initialise sequences with empty lists
        Iterator<Value> seqValIt = sequenceValues.iterator();
        while (seqValIt.hasNext()) {
            seqValIt.next();
            objectSequences.add(new ArrayList<FCAElement>());
        }

        // go over time
        final Iterator<Value> timeIt = timelineValues.iterator();
        while (timeIt.hasNext()) {
            final Value timelineValue = timeIt.next();

            // try to find matching object for each sequence
            seqValIt = sequenceValues.iterator();
            final Iterator<ArrayList<FCAElement>> seqIt = objectSequences
                    .iterator();
            while (seqValIt.hasNext()) {
                final Value sequenceValue = seqValIt.next();
                final List<FCAElement> sequence = seqIt.next();
                boolean objectFound = false;
                final Iterator objIt = this.context.getObjects().iterator();
                while (objIt.hasNext()) {
                    final FCAElement object = (FCAElement) objIt.next();
                    if (this.context.getRelationship(object, sequenceAttribute) != null
                            && this.context.getRelationship(object,
                                    sequenceAttribute).equals(sequenceValue)
                            && this.context.getRelationship(object,
                                    timelineAttribute) != null
                            && this.context.getRelationship(object,
                                    timelineAttribute).equals(timelineValue)) {
                        sequence.add(object);
                        objectFound = true;
                        break;
                    }
                }
                if (!objectFound) {
                    sequence.add(null);
                }
            }
        }

        return objectSequences;
    }

    private void gotoFirstStep() {
        gotoStep(1);
    }

    private void gotoPreviousStep() {
        gotoStep(this.currentStep - 1);
    }

    private void gotoNextStep() {
        gotoStep(this.currentStep + 1);
    }

    private void gotoLastStep() {
        gotoStep((int) this.timeController.getEndTime());
    }

    private void gotoStep(final int i) {
        this.currentStep = i;
        this.timeController.setCurrentTime(i);
        this.diagramView.repaint();
        setStepButtonStates(false);
    }
}