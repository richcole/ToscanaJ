/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.gui.temporal;

import net.sourceforge.toscanaj.view.temporal.ArrowStyle;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;
import org.tockit.swing.dialogs.GenericDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class ArrowStyleChooser extends JComponent {
    private static class StrokeButton extends JButton {
        private final BasicStroke stroke;

        public StrokeButton(final BasicStroke stroke) {
            super();
            this.stroke = stroke;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g;
            final Line2D line = new Line2D.Double(10, this.getHeight() / 2,
                    this.getWidth() - 5, this.getHeight() / 2);
            g2d.fill(this.stroke.createStrokedShape(line));
        }

        public BasicStroke getStroke() {
            return this.stroke;
        }
    }

    private static final float[][] DASH_STYLES = new float[][] { null,
            new float[] { 10 }, new float[] { 10, 20 }, new float[] { 20 },
            new float[] { 10, 30 }, new float[] { 30 } };

    private final ArrowStyle style;

    private JSlider strokeWidthSlider;

    private static final int BORDER_WIDTH_SCALE = 5;

    public ArrowStyleChooser(final ArrowStyle initialStyle) {
        this.style = new ArrowStyle(initialStyle);

        this.setLayout(new BorderLayout());
        this.add(createDisplayPane(), BorderLayout.NORTH);
        this.add(createControlPane(), BorderLayout.CENTER);
    }

    private Component createDisplayPane() {
        final JPanel retVal = new JPanel() {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);
                final Graphics2D g2d = (Graphics2D) g;
                final AffineTransform oldTransform = g2d.getTransform();
                final Paint oldPaint = g2d.getPaint();
                final Stroke oldStroke = g2d.getStroke();

                final Shape arrow = TransitionArrow.getArrowShape(
                        ArrowStyleChooser.this.style, this.getWidth() * 0.9);
                g2d.setPaint(ArrowStyleChooser.this.style.getColor());
                g2d.translate(this.getWidth() * 0.95, this.getHeight() / 2);
                g2d.fill(arrow);
                if (ArrowStyleChooser.this.style.getBorderWidth() != 0) {
                    g2d.setStroke(new BasicStroke(ArrowStyleChooser.this.style
                            .getBorderWidth()));
                    g2d.setPaint(Color.BLACK);
                    g2d.draw(arrow);
                }

                g2d.setPaint(oldPaint);
                g2d.setStroke(oldStroke);
                g2d.setTransform(oldTransform);
            }
        };
        final Dimension size = new Dimension(150, 55);
        retVal.setMinimumSize(size);
        retVal.setPreferredSize(size);
        return retVal;
    }

    private Component createControlPane() {
        final JTabbedPane retVal = new JTabbedPane();
        retVal.addTab("Color", createColorPane());
        retVal.addTab("Shape", createStrokePane());
        retVal.addTab("Label", createLabelPane());
        return retVal;
    }

    private Component createColorPane() {
        final JColorChooser retVal = new JColorChooser(this.style.getColor());
        retVal.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                ArrowStyleChooser.this.style.setColor(retVal.getColor());
                repaint();
            }
        });
        return retVal;
    }

    private Component createStrokePane() {
        final JPanel retVal = new JPanel(new GridBagLayout());

        final int width = (int) this.style.getStroke().getLineWidth();
        this.strokeWidthSlider = new JSlider(0, 20, width);
        this.strokeWidthSlider.setMajorTickSpacing(5);
        this.strokeWidthSlider.setMinorTickSpacing(1);
        this.strokeWidthSlider.setPaintTicks(true);
        this.strokeWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                updateStroke();
            }
        });

        final JPanel dashStylePanel = new JPanel(new FlowLayout());
        final int buttonWidth = 120;
        final int buttonHeight = 30;
        for (final float[] dashStyle : DASH_STYLES) {
            final StrokeButton button = new StrokeButton(new BasicStroke(5,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
                    dashStyle, 0));
            button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    ArrowStyleChooser.this.style.setStroke(button.getStroke());
                    updateStroke();
                }
            });
            dashStylePanel.add(button);
        }
        dashStylePanel.setPreferredSize(new Dimension(3 * buttonWidth + 50,
                ((DASH_STYLES.length + 2) / 3) * buttonHeight + 50));

        final JSlider headWidthSlider = new JSlider(0, 50, (int) this.style
                .getHeadWidth());
        headWidthSlider.setMajorTickSpacing(10);
        headWidthSlider.setMinorTickSpacing(2);
        headWidthSlider.setPaintTicks(true);
        headWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                ArrowStyleChooser.this.style.setHeadWidth(headWidthSlider
                        .getValue());
                repaint();
            }
        });

        final JSlider headLengthSlider = new JSlider(0, 50, (int) this.style
                .getHeadLength());
        headLengthSlider.setMajorTickSpacing(10);
        headLengthSlider.setMinorTickSpacing(2);
        headLengthSlider.setPaintTicks(true);
        headLengthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                ArrowStyleChooser.this.style.setHeadLength(headLengthSlider
                        .getValue());
                repaint();
            }
        });

        // the next line has a little extra bit to fix rounding problems (cast
        // to int rounds down)
        final JSlider borderWidthSlider = new JSlider(0, 50, (int) (this.style
                .getBorderWidth()
                * BORDER_WIDTH_SCALE + 0.01f));
        borderWidthSlider.setMajorTickSpacing(5);
        borderWidthSlider.setMinorTickSpacing(1);
        borderWidthSlider.setPaintTicks(true);
        borderWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                ArrowStyleChooser.this.style
                        .setBorderWidth(((float) borderWidthSlider.getValue())
                                / BORDER_WIDTH_SCALE);
                repaint();
            }
        });

        final GridBagConstraints labelconstraints = new GridBagConstraints();
        labelconstraints.gridx = 0;
        labelconstraints.anchor = GridBagConstraints.NORTHWEST;

        final GridBagConstraints controlconstraints = new GridBagConstraints();
        controlconstraints.gridx = 1;
        controlconstraints.gridy = 0;
        controlconstraints.weightx = 1;
        controlconstraints.fill = GridBagConstraints.HORIZONTAL;
        controlconstraints.anchor = GridBagConstraints.NORTH;

        retVal.add(new JLabel("Head Width:"), labelconstraints);
        retVal.add(headWidthSlider, controlconstraints);

        controlconstraints.gridy++;
        retVal.add(new JLabel("Head Length:"), labelconstraints);
        retVal.add(headLengthSlider, controlconstraints);

        controlconstraints.gridy++;
        retVal.add(new JLabel("Line Width:"), labelconstraints);
        retVal.add(this.strokeWidthSlider, controlconstraints);

        controlconstraints.gridy++;
        retVal.add(new JLabel("Border Width:"), labelconstraints);
        retVal.add(borderWidthSlider, controlconstraints);

        controlconstraints.gridy++;
        controlconstraints.weighty = 1;
        controlconstraints.fill = GridBagConstraints.BOTH;
        retVal.add(new JLabel("Line Style:"), labelconstraints);
        retVal.add(dashStylePanel, controlconstraints);

        return retVal;
    }

    private Component createLabelPane() {
        final JPanel retVal = new JPanel(new GridBagLayout());

        final GridBagConstraints labelconstraints = new GridBagConstraints();
        labelconstraints.gridx = 0;
        labelconstraints.anchor = GridBagConstraints.WEST;

        final GridBagConstraints controlconstraints = new GridBagConstraints();
        controlconstraints.gridx = 1;
        controlconstraints.gridy = 0;
        controlconstraints.weightx = 1;
        controlconstraints.weighty = 0;
        controlconstraints.fill = GridBagConstraints.HORIZONTAL;
        controlconstraints.anchor = GridBagConstraints.CENTER;

        final JComboBox<ArrowStyle.LabelUse> labelUseChooser = new JComboBox<>(ArrowStyle.LabelUse.values());
        labelUseChooser.setSelectedItem(style.getLabelUse());
        labelUseChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                style.setLabelUse((ArrowStyle.LabelUse) labelUseChooser.getSelectedItem());
            }
        });

        retVal.add(new JLabel("Show Labels:"), labelconstraints);
        retVal.add(labelUseChooser, controlconstraints);

        controlconstraints.gridy++;
        controlconstraints.weighty = 1;
        controlconstraints.fill = GridBagConstraints.BOTH;
        controlconstraints.gridwidth = 2;
        retVal.add(new JPanel(), controlconstraints);

        return retVal;
    }

    protected void updateStroke() {
        BasicStroke oldStroke = this.style.getStroke();
        BasicStroke newStroke = new BasicStroke(strokeWidthSlider.getValue(),
                oldStroke.getEndCap(), oldStroke.getLineJoin(), oldStroke.getMiterLimit(), oldStroke.getDashArray(),
                oldStroke.getDashPhase());
        style.setStroke(newStroke);
        repaint();
    }

    public static ArrowStyle showDialog(Component parent, String title, ArrowStyle initialStyle) {
        ArrowStyleChooser chooser = new ArrowStyleChooser(initialStyle);
        if (GenericDialog.showDialog(parent, title, chooser)) {
            return chooser.style;
        } else {
            return null;
        }
    }
}
