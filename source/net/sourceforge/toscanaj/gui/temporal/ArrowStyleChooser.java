/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package net.sourceforge.toscanaj.gui.temporal;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tockit.swing.dialogs.GenericDialog;

import net.sourceforge.toscanaj.view.temporal.ArrowStyle;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;


public class ArrowStyleChooser extends JComponent {
    private static class StrokeButton extends JButton {
        private BasicStroke stroke;
        
        public StrokeButton(BasicStroke stroke) {
            super();
            this.stroke = stroke;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Line2D line = new Line2D.Double(10,this.getHeight()/2, this.getWidth()-5,this.getHeight()/2);
            g2d.fill(this.stroke.createStrokedShape(line));
        }
        
        public BasicStroke getStroke() {
            return this.stroke;
        }
    }
    
    private static final float[][] DASH_STYLES = new float[][] {
        null,
        new float[] {10},  
        new float[] {10,20},  
        new float[] {20},  
        new float[] {10,30},  
        new float[] {30}  
    };
    
    private ArrowStyle style;

    private JSlider strokeWidthSlider;

    public ArrowStyleChooser(ArrowStyle initialStyle) {
        this.style = new ArrowStyle(initialStyle);
        
        this.setLayout(new BorderLayout());
        this.add(createDisplayPane(), BorderLayout.NORTH);
        this.add(createControlPane(), BorderLayout.CENTER);
    }

    private Component createDisplayPane() {
        JPanel retVal = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldTransform = g2d.getTransform();
                Paint oldPaint = g2d.getPaint();
                Stroke oldStroke = g2d.getStroke();
                        
                Shape arrow = TransitionArrow.getArrowShape(style, this.getWidth() * 0.9);
                g2d.setPaint(style.getColor());
                g2d.translate(this.getWidth() * 0.95, this.getHeight() / 2);
                g2d.fill(arrow);
                g2d.setPaint(Color.BLACK);
                g2d.draw(arrow);
                        
                g2d.setPaint(oldPaint);
                g2d.setStroke(oldStroke);
                g2d.setTransform(oldTransform);
            }
        };
        Dimension size = new Dimension(150,55);
        retVal.setMinimumSize(size);
        retVal.setPreferredSize(size);
        return retVal;
    }

    private Component createControlPane() {
        JTabbedPane retVal = new JTabbedPane();
        retVal.addTab("Color", createColorPane());
        retVal.addTab("Shape", createStrokePane());
        return retVal;
    }
    
    private Component createColorPane() {
        final JColorChooser retVal = new JColorChooser(this.style.getColor());
        retVal.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                style.setColor(retVal.getColor());
                repaint();
            } 
        });
        return retVal;
    }

    private Component createStrokePane() {
        JPanel retVal = new JPanel(new GridBagLayout());

        int width = 4;
        if(this.style.getStroke() instanceof BasicStroke) {
            width = (int) ((BasicStroke) this.style.getStroke()).getLineWidth();
        }
        this.strokeWidthSlider = new JSlider(0,20,width);
        this.strokeWidthSlider.setMajorTickSpacing(5);
        this.strokeWidthSlider.setMinorTickSpacing(1);
        this.strokeWidthSlider.setPaintTicks(true);
        this.strokeWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateStroke();
            }
        });
        
        JPanel dashStylePanel = new JPanel(new FlowLayout());
        int buttonWidth = 120;
        int buttonHeight = 30;
        for (int i = 0; i < DASH_STYLES.length; i++) {
            float[] dashStyle = DASH_STYLES[i];
            final StrokeButton button = new StrokeButton(new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, dashStyle, 0));
            button.setPreferredSize(new Dimension(buttonWidth,buttonHeight));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    style.setStroke(button.getStroke());
                    updateStroke();  
                }
            });
            dashStylePanel.add(button);
        }
        dashStylePanel.setPreferredSize(new Dimension(3 * buttonWidth + 50, ((DASH_STYLES.length + 2) / 3) * buttonHeight + 50));

        final JSlider headWidthSlider = new JSlider(0,50,(int) this.style.getHeadWidth());
        headWidthSlider.setMajorTickSpacing(10);
        headWidthSlider.setMinorTickSpacing(2);
        headWidthSlider.setPaintTicks(true);
        headWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                style.setHeadWidth(headWidthSlider.getValue());
                repaint();
            }
        });
        
        final JSlider headLengthSlider = new JSlider(0,50,(int) this.style.getHeadLength());
        headLengthSlider.setMajorTickSpacing(10);
        headLengthSlider.setMinorTickSpacing(2);
        headLengthSlider.setPaintTicks(true);
        headLengthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                style.setHeadLength(headLengthSlider.getValue());
                repaint();
            }
        });
        
        GridBagConstraints labelconstraints = new GridBagConstraints();
        labelconstraints.gridx = 0;
        labelconstraints.anchor = GridBagConstraints.NORTHWEST;
        
        GridBagConstraints controlconstraints = new GridBagConstraints();
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
        retVal.add(strokeWidthSlider, controlconstraints);
        
        controlconstraints.gridy++;
        controlconstraints.weighty = 1;
        controlconstraints.fill = GridBagConstraints.BOTH;
        retVal.add(new JLabel("Line Style:"), labelconstraints);
        retVal.add(dashStylePanel, controlconstraints);

        return retVal;
    }

    protected void updateStroke() {
        BasicStroke oldStroke = (BasicStroke) this.style.getStroke();
        BasicStroke newStroke = new BasicStroke(this.strokeWidthSlider.getValue(), 
                                                oldStroke.getEndCap(), 
                                                oldStroke.getLineJoin(), 
                                                oldStroke.getMiterLimit(), 
                                                oldStroke.getDashArray(), 
                                                oldStroke.getDashPhase());
        this.style.setStroke(newStroke);
        repaint();
    }

    public static ArrowStyle showDialog(Component parent, String title, ArrowStyle initialStyle) {
        ArrowStyleChooser chooser = new ArrowStyleChooser(initialStyle);
        if(GenericDialog.showDialog(parent, title, chooser)) {
            return chooser.style;
        } else {
            return null;
        }
    }
}
