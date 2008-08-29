/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.swing;

import java.text.NumberFormat;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.text.NumberFormatter;

import org.tockit.datatype.ConversionException;
import org.tockit.datatype.Datatype;
import org.tockit.datatype.xsd.DecimalType;
import org.tockit.datatype.xsd.StringType;
import org.tockit.datatype.xsd.StringValue;

public abstract class DatatypeViewFactory {
    // @todo generalize to cover TreeCellEditor, too
    public static TableCellEditor getValueCellEditor(final Datatype datatype) {
        if (datatype instanceof StringType.EnumerationRestrictedStringType) {
            final StringType.EnumerationRestrictedStringType stringType = (StringType.EnumerationRestrictedStringType) datatype;
            final StringValue[] enumeration = stringType.getEnumeration();
            final StringValue[] arrayIncludingUnset = new StringValue[enumeration.length + 1];
            System.arraycopy(enumeration, 0, arrayIncludingUnset, 1,
                    enumeration.length);
            final JComboBox comp = new JComboBox(arrayIncludingUnset);
            return new DefaultCellEditor(comp);
        }
        if (datatype instanceof StringType) {
            final StringType stringType = (StringType) datatype;
            final JTextField comp = new JTextField();
            return new DefaultCellEditor(comp) {
                @Override
                public Object getCellEditorValue() {
                    if (comp.getText().length() == 0) {
                        return null; // unset relationship
                    }
                    try {
                        return stringType.parse(comp.getText());
                    } catch (final ConversionException e) {
                        // @todo provide error feedback somehow
                        return null;
                    }
                }
            };
        }
        if (datatype instanceof DecimalType) {
            final DecimalType decType = (DecimalType) datatype;
            final NumberFormat format = NumberFormat.getNumberInstance();
            final JFormattedTextField comp = new JFormattedTextField(
                    new NumberFormatter(format));
            return new DefaultCellEditor(comp) {
                @Override
                public Object getCellEditorValue() {
                    if (comp.getText().length() == 0) {
                        return null; // unset relationship
                    }
                    try {
                        return decType.parse(comp.getText());
                    } catch (final ConversionException e) {
                        // @todo provide error feedback somehow
                        return null;
                    }
                }
            };
        }
        throw new RuntimeException("Unknown type");
    }

    public static JPanel getSubtypingPanel(final Datatype datatype) {
        assert datatype != null;
        throw new RuntimeException("Not yet implemented");
    }
}
