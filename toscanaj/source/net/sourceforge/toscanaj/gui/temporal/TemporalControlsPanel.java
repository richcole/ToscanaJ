/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.temporal;

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
import net.sourceforge.toscanaj.view.temporal.*;
import org.tockit.canvas.events.CanvasDrawnEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;
import org.tockit.datatype.Value;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.*;
import java.util.List;

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
    private JList<Value> sequenceToShowChooser;
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
    private JCheckBox repeatAnimationCheckbox;
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

        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);
        diagramView.getController().getEventBroker().subscribe(this, DisplayedDiagramChangedEvent.class, DiagramView.class);
        diagramView.getController().getEventBroker().subscribe(this, CanvasDrawnEvent.class, Object.class);
        new TransitionArrowManipulator(diagramView, diagramView.getController().getEventBroker());

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
        final ArrowStyle[] styles = DiagramSchema.getCurrentSchema().getArrowStyles();
        final JList<ArrowStyle> listView = new JList<>(styles);
        listView.setCellRenderer(new ListCellRenderer<ArrowStyle>() {
            public Component getListCellRendererComponent(final JList list,
                                                          final ArrowStyle style, final int index,
                                                          final boolean isSelected, final boolean cellHasFocus) {
                JPanel retVal = new JPanel() {
                    @Override
                    protected void paintComponent(final Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        AffineTransform oldTransform = g2d.getTransform();
                        Paint oldPaint = g2d.getPaint();
                        Stroke oldStroke = g2d.getStroke();

                        Shape arrow = TransitionArrow.getArrowShape(style, getWidth() * 0.9);
                        g2d.setPaint(style.getColor());
                        g2d.translate(getWidth() * 0.95, getHeight() / 2);
                        g2d.fill(arrow);
                        if (style.getBorderWidth() != 0) {
                            g2d.setStroke(new BasicStroke(style.getBorderWidth()));
                            g2d.setPaint(Color.BLACK);
                            g2d.draw(arrow);
                        }

                        g2d.setPaint(oldPaint);
                        g2d.setStroke(oldStroke);
                        g2d.setTransform(oldTransform);
                    }
                };
                Dimension size = new Dimension(150, 30);
                retVal.setMinimumSize(size);
                retVal.setPreferredSize(size);

                if(sequenceValues.size() > index) {
                    retVal.setToolTipText(sequenceValues.get(index).getDisplayString());
                } else {
                    retVal.setToolTipText("n/a");
                }
                return retVal;
            }
        });
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int index = listView.getSelectedIndex();
                ListModel listModel = listView.getModel();
                ArrowStyle style = (ArrowStyle) listModel.getElementAt(index);
                ArrowStyle newStyle = ArrowStyleChooser.showDialog(listView, "Edit arrow style", style);
                if (newStyle != null) {
                    style.copyValues(newStyle);
                    DiagramSchema.getCurrentSchema().store();
                    diagramView.repaint();
                }
            }
        });
        MouseInputAdapter dragHandler = new MouseInputAdapter() {
            private boolean isDragging = false;
            private int dragStartIndex;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStartIndex = listView.getSelectedIndex();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int currentIndex = listView.locationToIndex(e.getPoint());
                    if (currentIndex != dragStartIndex) {
                        int dragTargetIndex = listView.getSelectedIndex();
                        ArrowStyle sourceStyle = styles[dragStartIndex];
                        styles[dragStartIndex] = styles[dragTargetIndex];
                        styles[dragTargetIndex] = sourceStyle;
                        dragStartIndex = currentIndex;
                    }
                }
                isDragging = true;
            }
        };
        listView.addMouseListener(dragHandler);
        listView.addMouseMotionListener(dragHandler);
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

        repeatAnimationCheckbox = new JCheckBox("Repeat Animations");
        repeatAnimationCheckbox.setSelected(false);

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
        panel.add(repeatAnimationCheckbox, buttonConstraints);
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
        sequenceToShowChooser = new JList<>();

        serializeSequencesBox = new JCheckBox("Serialize when stepping/animating");

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
        sequenceToShowChooser.setListData(sequenceValues.toArray(new Value[sequenceValues.size()]));
        sequenceToShowChooser.setSelectionInterval(0, sequenceValues.size() - 1);
    }

    private void setButtonStates(final boolean allDisabled) {
        final boolean enabled = !allDisabled && (this.diagramView.getDiagram() != null);
        addStaticTransitionsButton.setEnabled(enabled);
        removeTransitionsButton.setEnabled(enabled);
        startSteppingButton.setEnabled(enabled);
        animateTransitionsButton.setEnabled(enabled);
        exportImagesButton.setEnabled(enabled && this.diagramExportSettings != null);
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
        } while (!formatDefined);
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

        setUpTransitions(length);

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
        if (lastAnimationTime > targetTime) {
            removeTransitions();
            animating = false;
            if(repeatAnimationCheckbox.isSelected()) {
                addAnimatedTransitions();
            }
        } else {
            timeController.calculateCurrentTime();
            lastAnimationTime = timeController.getCurrentTime();
            diagramView.repaint(); // paint it again as time has past
        }
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
        setUpTransitions(length);
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
        setUpTransitions(length);
        this.animating = true;
        this.diagramView.updateDiagram();
    }

    private void setUpTransitions(int length) {
        List<Value> sequences = sequenceToShowChooser.getSelectedValuesList();
        if (this.serializeSequencesBox.isSelected() && sequences.size() > 1) {
            int numSeq = sequences.size();
            timeController.setEndTime(length * numSeq);
            addTransitionsSerialized(timeController.getAllFadedTime(), true);
        } else {
            timeController.setEndTime(length);
            addTransitions(timeController.getAllFadedTime(), true);
        }
    }

    private void addTransitions(double newTargetTime, boolean highlightStates) {
        List<Value> selected = sequenceToShowChooser.getSelectedValuesList();
        List<ArrayList<FCAElement>> objectSequences = calculateObjectSequences();
        Iterator<Value> seqValIt = sequenceValues.iterator();
        int styleNum = 0;
        boolean start = true;
        for (List<FCAElement> sequence : objectSequences) {
            Value curSequenceValue = seqValIt.next();
            if (start) {
                start = false;
                this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            ArrowStyle[] styles = DiagramSchema.getCurrentSchema().getArrowStyles();
            if (selected.contains(curSequenceValue)) {
                addTransitions(curSequenceValue, sequence, styles[styleNum], highlightStates, 0);
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
    private void addTransitionsSerialized(final double newTargetTime, final boolean highlightStates) {
        List<Value> selected = sequenceToShowChooser.getSelectedValuesList();
        List<ArrayList<FCAElement>> objectSequences = calculateObjectSequences();
        Iterator<Value> seqValIt = sequenceValues.iterator();
        int seqNum = 0;
        int seqLength = timelineValues.size();
        List<FCAElement> lastSequence = null;
        ArrowStyle[] styles = DiagramSchema.getCurrentSchema().getArrowStyles();
        ArrowStyle style = null;
        int trailCount = 0;
        for (ArrayList<FCAElement> sequence : objectSequences) {
            Value curSequenceValue = seqValIt.next();
            if(!selected.contains(curSequenceValue)) {
                seqNum++;
                continue;
            }
            if (lastSequence != null) {
                Color nextColor = styles[seqNum % styles.length].getColor();
                DiagramNode endLast = findObjectConceptNode(lastSequence.get(lastSequence.size() - 1));
                DiagramNode startNew = findObjectConceptNode(sequence.get(0));
                if (endLast == null) {
                    continue;
                }
                if (endLast != startNew) {
                    SimpleLineDiagram diagram = (SimpleLineDiagram) diagramView.getDiagram();
                    diagram.addExtraCanvasItem(
                            new InterSequenceTransitionArrow(
                                    endLast, startNew, style, nextColor, trailCount * seqLength + 0.5, timeController));
                }
            }

            style = styles[seqNum % styles.length];
            if (lastSequence == null) {
                this.targetTime = newTargetTime;
                this.lastAnimationTime = 0;
            }
            addTransitions(curSequenceValue, sequence, style, highlightStates, trailCount * seqLength);
            seqNum++;
            trailCount++;
            lastSequence = sequence;
        }
    }

    private void addTransitions(Value curSequenceValue, List<FCAElement> sequence, ArrowStyle style,
                                boolean highlightStates, int countStart) {
        SimpleLineDiagram diagram = (SimpleLineDiagram) diagramView.getDiagram();
        DiagramNode oldNode = null;
        int count = countStart;
        boolean first = true;
        for (Iterator<FCAElement> iterator = sequence.iterator(); iterator.hasNext(); ) {
            FCAElement object = iterator.next();
            count++;
            final DiagramNode curNode = findObjectConceptNode(object);
            if (curNode == null) {
                continue;
            }
            if (highlightStates) {
                diagram.addExtraCanvasItem(new StateRing(curNode, style.getColor(), count, timeController));
            }
            if (oldNode != null && oldNode != curNode) {
                TransitionArrow arrow = new TransitionArrow(oldNode, curNode, style, count - 0.5, timeController);
                diagram.addExtraCanvasItem(arrow);
                if(style.getLabelUse().shouldShow(first, !iterator.hasNext())) {
                    ArrowLabelView label =
                            new ArrowLabelView(diagramView, arrow, style, curSequenceValue.getDisplayString(),
                                    count - 0.5, timeController);
                    diagram.addExtraCanvasItem(label);
                }
                first = false;
            }
            oldNode = curNode;
        }
    }

    private DiagramNode findObjectConceptNode(final FCAElement object) {
        final Iterator<DiagramNode> nodeIt = this.diagramView.getDiagram().getNodes();
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
                curContext = curContext.createNestedContext(curNode.getConcept());
                curNode = curNode.getOuterNode();
            }
            // try finding the object in nested context
            final Iterator objIt = conceptInterpreter.getObjectSetIterator(node.getConcept(), curContext);
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
        sequenceValues = new ArrayList<>();
        timelineValues = new ArrayList<>();

        final Object selectedSequenceColumn = this.sequenceColumnChooser.getSelectedItem();
        if (!(selectedSequenceColumn instanceof ManyValuedAttribute)) {
            return;
        }
        final ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) selectedSequenceColumn;
        final ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) timelineColumnChooser.getSelectedItem();

        for (final FCAElement object : this.context.getObjects()) {
            Value value = this.context.getRelationship(object, sequenceAttribute);
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
            if (!timelineValues.contains(value) && value != null) {
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
        final ManyValuedAttribute sequenceAttribute = (ManyValuedAttribute) sequenceColumnChooser.getSelectedItem();
        final ManyValuedAttribute timelineAttribute = (ManyValuedAttribute) timelineColumnChooser.getSelectedItem();

        final List<ArrayList<FCAElement>> objectSequences = new ArrayList<>();

        // initialise sequences with empty lists
        for (Value ignored : sequenceValues) {
            objectSequences.add(new ArrayList<FCAElement>());
        }

        // go over time
        for (Value timelineValue : timelineValues) {
            // try to find matching object for each sequence
            Iterator<Value> seqValIt = sequenceValues.iterator();
            final Iterator<ArrayList<FCAElement>> seqIt = objectSequences
                    .iterator();
            while (seqValIt.hasNext()) {
                final Value sequenceValue = seqValIt.next();
                final List<FCAElement> sequence = seqIt.next();
                boolean objectFound = false;
                for (FCAElement object : this.context.getObjects()) {
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