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
import org.tockit.datatype.Value;
import org.tockit.datatype.xsd.DecimalType;
import org.tockit.datatype.xsd.StringType;


public abstract class DatatypeViewFactory {
    // @todo generalize to cover TreeCellEditor, too
	public static TableCellEditor getValueCellEditor(Datatype datatype) {
        if(datatype instanceof StringType.EnumerationRestrictedStringType) {
            StringType.EnumerationRestrictedStringType stringType = 
                    (StringType.EnumerationRestrictedStringType) datatype;
            JComboBox comp = new JComboBox(stringType.getEnumeration());
            return new DefaultCellEditor(comp);
        }
        if(datatype instanceof StringType) {
            final StringType stringType = (StringType) datatype;
            final JTextField comp = new JTextField();
            return new DefaultCellEditor(comp) {
                public Object getCellEditorValue() {
                    try {
                        return stringType.parse(comp.getText());
                    } catch (ConversionException e) {
                        // @todo provide error feedback somehow
                        return Value.NULL;
                    }
                }
            };
        }
        if(datatype instanceof DecimalType) {
            final DecimalType decType = (DecimalType) datatype;
            NumberFormat format = NumberFormat.getNumberInstance();
            final JFormattedTextField comp = new JFormattedTextField(new NumberFormatter(format));
            return new DefaultCellEditor(comp) {
                public Object getCellEditorValue() {
                    try {
                        return decType.parse(comp.getText());
                    } catch (ConversionException e) {
                        // @todo provide error feedback somehow
                        return Value.NULL;
                    }
                }
            };
        }
        throw new RuntimeException("Unknown type");
	}
	
	public static JPanel getSubtypingPanel(Datatype datatype) {
		return null;
	}
}
