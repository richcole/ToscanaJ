/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.datatype.xsd;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;
import org.tockit.datatype.AbstractDatatype;
import org.tockit.datatype.Value;


public class StringType extends AbstractDatatype {
    private StringType();
    
    public static StringType createUnrestrictedType() {
        return new StringType();
    }
    
    public static StringType createLengthRestrictedType(int minLength, int maxLength) {
        return new LengthRestrictedStringType(minLength, maxLength);
    }

    public static StringType createPatternRestrictedType(String regExp) {
        return new PatternRestrictedStringType(regExp);
    }

    public static StringType createEnumerationRestrictedType(String[] enumeration) {
        return new EnumerationRestrictedStringType(enumeration);
    }

    public static StringType createEnumerationRestrictedType(Collection enumeration) {
        String[] enumerationArray = (String[]) enumeration.toArray(new String[enumeration.size()]);
        return new EnumerationRestrictedStringType(enumerationArray);
    }

    public boolean isValidValue(Value valueToTest) {
        if(valueToTest.getClass() != StringValue.class) {
            return false;
        }
        StringValue stringValue = (StringValue) valueToTest;
        return isValidStringValue(stringValue.getValue());
    }

    protected boolean isValidStringValue(String valueToTest) {
        return true;
    }

    public JComponent getTypeEditingComponent() {
        return null;
    }

    public JComponent getValueEditingComponent() {
        return null;
    }

    public JComponent getTypeDisplayComponent() {
        return null;
    }

    public JComponent getValueDisplayComponent() {
        return null;
    }

    public JComponent getSubsetSelectionComponent() {
        return null;
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

    private static class LengthRestrictedStringType extends StringType {
        private int minLength;
        private int maxLength;
        
        public LengthRestrictedStringType(int minLength, int maxLength) {
            this.minLength = minLength;
            this.maxLength = maxLength;
        }
        
        public JComponent getTypeDisplayComponent() {
            return null;
        }

        public JComponent getTypeEditingComponent() {
            return null;
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

    private static class PatternRestrictedStringType extends StringType {
        private Pattern pattern;
        
        public PatternRestrictedStringType(String regExp) {
            this.pattern = Pattern.compile(regExp);
        }
        
        public JComponent getTypeDisplayComponent() {
            return null;
        }

        public JComponent getTypeEditingComponent() {
            return null;
        }
        
        public boolean isValidStringValue(String valueToTest) {
            Matcher matcher = this.pattern.matcher(valueToTest); 
            return matcher.matches();
        }
    }

    private static class EnumerationRestrictedStringType extends StringType {
        private String[] enumeration;
        
        public EnumerationRestrictedStringType(String[] enumeration) {
            this.enumeration = enumeration;
        }
        
        public JComponent getTypeDisplayComponent() {
            return null;
        }

        public JComponent getTypeEditingComponent() {
            return null;
        }
        
        public boolean isValidStringValue(String valueToTest) {
            for (int i = 0; i < this.enumeration.length; i++) {
                if(this.enumeration[i].equals(valueToTest)) {
                    return true;
                }
            }
            return false;
        }
    }
}
