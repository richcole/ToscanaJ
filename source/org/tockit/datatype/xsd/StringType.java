/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;
import org.tockit.datatype.AbstractDatatype;
import org.tockit.datatype.ConversionException;
import org.tockit.datatype.Value;


public class StringType extends AbstractDatatype {
    protected StringType(String name) {
        super(name);
    }

    public static StringType createUnrestrictedType(String name) {
        return new StringType(name);
    }
    
    public static StringType createLengthRestrictedType(String name, int minLength, int maxLength) {
        return new LengthRestrictedStringType(name, minLength, maxLength);
    }

    public static StringType createPatternRestrictedType(String name, String regExp) {
        return new PatternRestrictedStringType(name, regExp);
    }

    public static StringType createEnumerationRestrictedType(String name, StringValue[] enumeration) {
        return new EnumerationRestrictedStringType(name, enumeration);
    }

    public static StringType createEnumerationRestrictedType(String name, String[] enumeration) {
        StringValue[] stringValues = new StringValue[enumeration.length];
        for (int i = 0; i < stringValues.length; i++) {
            stringValues[i] = new StringValue(enumeration[i]);
        }
        return new EnumerationRestrictedStringType(name, stringValues);
    }

    public boolean isValidValue(Value valueToTest) {
        if(!(valueToTest instanceof StringValue)) {
            return false;
        }
        StringValue stringValue = (StringValue) valueToTest;
        return isValidStringValue(stringValue.getValue());
    }

    protected boolean isValidStringValue(String valueToTest) {
        return true;
    }
    
    public boolean canConvertFrom(Value value) {
        return isValidStringValue(value.getDisplayString());
    }
    
    public Value convertType(Value value) throws ConversionException {
        return parse(value.getDisplayString());
    }

    public boolean canParse(String text) {
        return isValidStringValue(text);
    }
    
    public Value parse(String text) throws ConversionException {
        if(isValidStringValue(text)) {
            return new StringValue(text);
        }
        throw new ConversionException("Invalid string value for type");
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

    public static class LengthRestrictedStringType extends StringType {
        private int minLength;
        private int maxLength;
        
        private LengthRestrictedStringType(String name, int minLength, int maxLength) {
            super(name);
            this.minLength = minLength;
            this.maxLength = maxLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }

        public int getMinLength() {
            return minLength;
        }
        
        public boolean isValidStringValue(String valueToTest) {
            if(valueToTest.length() < this.minLength) {
                return false;
            }
            if(valueToTest.length() > this.maxLength) {
                return false;
            }
            return true;
        }
    }

    public static class PatternRestrictedStringType extends StringType {
        private Pattern pattern;
        
        private PatternRestrictedStringType(String name, String regExp) {
            super(name);
            this.pattern = Pattern.compile(regExp);
        }
        
        public Pattern getPattern() {
            return pattern;
        }

        public boolean isValidStringValue(String valueToTest) {
            Matcher matcher = this.pattern.matcher(valueToTest); 
            return matcher.matches();
        }
    }

    public static class EnumerationRestrictedStringType extends StringType {
        private StringValue[] enumeration;
        
        private EnumerationRestrictedStringType(String name, StringValue[] enumeration) {
            super(name);
            this.enumeration = enumeration;
        }
        
        public boolean isValidStringValue(String valueToTest) {
            for (int i = 0; i < this.enumeration.length; i++) {
                if(this.enumeration[i].getValue().equals(valueToTest)) {
                    return true;
                }
            }
            return false;
        }
        
        public StringValue[] getEnumeration() {
            return enumeration;
        }
    }
}
