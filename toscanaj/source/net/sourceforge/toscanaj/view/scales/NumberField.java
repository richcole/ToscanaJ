/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JTextField;

/**
 * @todo restrict data entered to be integer instead of turning it into integer
 *       at the end
 */
public class NumberField extends JTextField {
    public static final int INTEGER = 0;
    public static final int FLOAT = 1;

    private NumberFormat formatter;

    public NumberField(final int columns, final int formatType) {
        super(columns);
        setNumberType(formatType);
    }

    public void setNumberType(final int formatType) {
        if (formatType == FLOAT) {
            formatter = NumberFormat.getNumberInstance(Locale.US);
        } else {
            formatter = NumberFormat.getIntegerInstance(Locale.US);
        }
    }

    @Override
    public boolean isValid() {
        try {
            formatter.parse(getText());
            return true;
        } catch (final ParseException e) {
            return false;
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public double getDoubleValue() {
        double retVal = 0;
        try {
            retVal = formatter.parse(getText()).doubleValue();
        } catch (final ParseException e) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            throw new RuntimeException("Could not parse value in NumberField",
                    e);
        }
        return retVal;
    }

    public int getIntegerValue() {
        int retVal = 0;
        try {
            retVal = formatter.parse(getText()).intValue();
        } catch (final ParseException e) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            throw new RuntimeException("Could not parse value in NumberField",
                    e);
        }
        return retVal;
    }

    public void setIntegerValue(final int value) {
        setText(formatter.format(value));
    }

    public void setDoubleValue(final double value) {
        setText(formatter.format(value));
    }
}
