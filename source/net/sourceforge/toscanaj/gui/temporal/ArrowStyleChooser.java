/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package net.sourceforge.toscanaj.gui.temporal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tockit.swing.dialogs.GenericDialog;

import net.sourceforge.toscanaj.view.temporal.ArrowStyle;
import net.sourceforge.toscanaj.view.temporal.TransitionArrow;


public class ArrowStyleChooser extends JComponent {
    private ArrowStyle style;

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
                        
                Shape arrow = TransitionArrow.getArrowShape(style, this.getWidth() * 0.9);
                g2d.setPaint(style.getColor());
                g2d.translate(this.getWidth() * 0.95, this.getHeight() / 2);
                g2d.fill(arrow);
                        
                g2d.setPaint(oldPaint);
                g2d.setTransform(oldTransform);
            }
        };
        Dimension size = new Dimension(150,30);
        retVal.setMinimumSize(size);
        retVal.setPreferredSize(size);
        return retVal;
    }

    private Component createControlPane() {
        JTabbedPane retVal = new JTabbedPane();
        retVal.addTab("Color", createColorPane());
        retVal.addTab("Stroke", createStrokePane());
        retVal.addTab("Head", createHeadSettingsPane());
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
        JPanel retVal = new JPanel();
        return retVal;
    }

    private Component createHeadSettingsPane() {
        JPanel retVal = new JPanel();
        return retVal;
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
