/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class DoubleNumberField extends JTextField {

    private Toolkit toolkit;
    private NumberFormat doubleFormatter;

    public DoubleNumberField(double value, int columns) {
        super(columns);
        toolkit = Toolkit.getDefaultToolkit();
        doubleFormatter = NumberFormat.getNumberInstance(Locale.US);
        setValue(value);
    }

    public boolean isValid() {
        try {
            doubleFormatter.parse(getText()).doubleValue();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public double getValue() {
        double retVal = 0;
        try {
            retVal = doubleFormatter.parse(getText()).doubleValue();
        } catch (ParseException e) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            toolkit.beep();
        }
        return retVal;
    }

    public void setValue(double value) {
        setText(doubleFormatter.format(value));
    }
}