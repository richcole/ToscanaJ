/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;
import org.tockit.datatype.ConversionException;
import org.tockit.datatype.Datatype;
import org.tockit.datatype.DatatypeFactory;
import org.tockit.datatype.Value;


/**
 * @todo XSD allows combining the restrictions, we don't
 */
public class StringType extends AbstractXSDDatatype {
    public static DatatypeFactory.TypeCreator getTypeCreator() {
        return new TypeCreator("string") {
            public Datatype create(Element element) {
                String name = getTypeName(element);
                Element restElem = getRestrictionElement(element);
                Element child = restElem.getChild("enumeration", XSD_NAMESPACE);
                if(child != null) {
                    List enumChildren = restElem.getChildren("enumeration", XSD_NAMESPACE);
                    StringValue[] enumeration = new StringValue[enumChildren.size()];
                    int i = 0;
                    for (Iterator iter = enumChildren.iterator(); iter.hasNext();) {
                        Element enumElem = (Element) iter.next();
                        enumeration[i] = new StringValue(enumElem.getAttributeValue("value"));
                        i++;
                    }
                    return createEnumerationRestrictedType(name, enumeration);
                }
                child = restElem.getChild("pattern", XSD_NAMESPACE);
                if(child != null) {
                    return createPatternRestrictedType(name, child.getAttributeValue("value"));
                }
                child = restElem.getChild("minLength", XSD_NAMESPACE);
                if(child != null) {
                    int minLen = Integer.parseInt(child.getAttributeValue("value"));
                    Element maxLength = restElem.getChild("maxLength", XSD_NAMESPACE);
                    int maxLen = Integer.parseInt(maxLength.getAttributeValue("value"));
                    return createLengthRestrictedType(name, minLen, maxLen);
                }
                return createUnrestrictedType(name);
            }
        };
    }
    
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
        return new StringValue(element.getAttributeValue("value"));
    }

    public void insertValue(Element element, Value value) {
        StringValue sValue = (StringValue) value;
        element.setAttribute("value", sValue.getValue());
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    	// @TODO implement
    }

    protected void addRestrictions(Element restrictionElement) {
        // no restrictions needed
    }
    
    protected String getBaseType() {
        return "string";
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

        protected void addRestrictions(Element restElement) {
            Element minElem = createElement("minLength");
            minElem.setAttribute("minLength", String.valueOf(this.minLength));
            restElement.addContent(minElem);
            Element maxElem = createElement("maxLength");
            maxElem.setAttribute("maxLength", String.valueOf(this.maxLength));
            restElement.addContent(maxElem);
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

        protected void addRestrictions(Element restElement) {
            Element pattElem = createElement("pattern");
            pattElem.setAttribute("value", this.pattern.pattern());
            restElement.addContent(pattElem);
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

        protected void addRestrictions(Element restElement) {
            for (int i = 0; i < this.enumeration.length; i++) {
                StringValue value = this.enumeration[i];
                Element enumElem = createElement("enumeration");
                enumElem.setAttribute("value", value.getValue());
                restElement.addContent(enumElem);
            }
        }
    }
}
