/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;
import org.tockit.datatype.AbstractDatatype;
import org.tockit.datatype.ConversionException;
import org.tockit.datatype.Value;


public class DecimalType extends AbstractDatatype {
    private final double min;
    private final double max;
    private final boolean minIncluded;
    private final boolean maxIncluded;
    private final int numDecimals;
    
    private DecimalType(String name, double min, boolean minIncluded,
            double max, boolean maxIncluded, int numDecimals) {
        super(name);
        if(min > max) {
            throw new IllegalArgumentException("Minimum greater than maximum value for type");
        }
        if(min == max && !(minIncluded && maxIncluded)) {
            throw new IllegalArgumentException("Both minimum and maximum have to be included if equal.");
        }
        this.min = min;
        this.minIncluded = minIncluded;
        this.max = max;
        this.maxIncluded = maxIncluded;
        this.numDecimals = numDecimals;
    }

    public static DecimalType createDecimalType(String name, double min, double max, int numDecimals) {
        return new DecimalType(name, min, true, max, true, numDecimals);
    }

    public static DecimalType createDecimalType(String name, 
            double min, boolean minIncluded, double max, boolean maxIncluded,
            int numDecimals) {
        return new DecimalType(name, min, minIncluded, max, maxIncluded, numDecimals);
    }

    public boolean isValidValue(Value valueToTest) {
        if(!(valueToTest instanceof DecimalValue)) {
            return false;
        }
        DecimalValue decValue = (DecimalValue) valueToTest;
        return isValidValue(decValue.getValue());
    }

    public boolean isValidValue(double value) {
        if(value < this.min) {
            return false;
        }
        if(value == this.min && !this.minIncluded) {
            return false;
        }
        if(value > this.max) {
            return false;
        }
        if(value == this.max && !this.maxIncluded) {
            return false;
        }
        return true;
    }
    
    public boolean canConvertFrom(Value value) {
        if(value instanceof StringValue) {
            return canParse(((StringValue) value).getValue());
        } else {
            return false;
        }
    }
    
    public Value convertType(Value value) throws ConversionException {
        if(value instanceof StringValue) {
            return parse(((StringValue) value).getValue());
        } else {
            return super.convertType(value);
        }
    }
    
    public boolean canParse(String text) {
        try {
            double val = Double.parseDouble(text);
            return isValidValue(val);
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public Value parse(String text) throws ConversionException {
        try {
            double val = Double.parseDouble(text);
            if(!isValidValue(val)) {
                throw new ConversionException("Value out of range");
            }
            return new DecimalValue(val);
        } catch(NumberFormatException e) {
            throw new ConversionException("Can not parse text", e);
        }
    }

    public Value toValue(Element element) {
        return null;
    }

    public Element toElement(Value value) {
        return null;
    }

    public Element toXML() {
        return null;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    }
}
