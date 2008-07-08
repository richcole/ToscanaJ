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
            public Datatype create(final Element element) {
                final String name = getTypeName(element);
                final Element restElem = getRestrictionElement(element);
                Element child = restElem.getChild("enumeration", XSD_NAMESPACE);
                if (child != null) {
                    final List enumChildren = restElem.getChildren(
                            "enumeration", XSD_NAMESPACE);
                    final StringValue[] enumeration = new StringValue[enumChildren
                            .size()];
                    int i = 0;
                    for (final Iterator iter = enumChildren.iterator(); iter
                            .hasNext();) {
                        final Element enumElem = (Element) iter.next();
                        enumeration[i] = new StringValue(enumElem
                                .getAttributeValue("value"));
                        i++;
                    }
                    return createEnumerationRestrictedType(name, enumeration);
                }
                child = restElem.getChild("pattern", XSD_NAMESPACE);
                if (child != null) {
                    return createPatternRestrictedType(name, child
                            .getAttributeValue("value"));
                }
                child = restElem.getChild("minLength", XSD_NAMESPACE);
                if (child != null) {
                    final int minLen = Integer.parseInt(child
                            .getAttributeValue("value"));
                    final Element maxLength = restElem.getChild("maxLength",
                            XSD_NAMESPACE);
                    final int maxLen = Integer.parseInt(maxLength
                            .getAttributeValue("value"));
                    return createLengthRestrictedType(name, minLen, maxLen);
                }
                return createUnrestrictedType(name);
            }
        };
    }

    protected StringType(final String name) {
        super(name);
    }

    public static StringType createUnrestrictedType(final String name) {
        return new StringType(name);
    }

    public static StringType createLengthRestrictedType(final String name,
            final int minLength, final int maxLength) {
        return new LengthRestrictedStringType(name, minLength, maxLength);
    }

    public static StringType createPatternRestrictedType(final String name,
            final String regExp) {
        return new PatternRestrictedStringType(name, regExp);
    }

    public static StringType createEnumerationRestrictedType(final String name,
            final StringValue[] enumeration) {
        return new EnumerationRestrictedStringType(name, enumeration);
    }

    public static StringType createEnumerationRestrictedType(final String name,
            final String[] enumeration) {
        final StringValue[] stringValues = new StringValue[enumeration.length];
        for (int i = 0; i < stringValues.length; i++) {
            stringValues[i] = new StringValue(enumeration[i]);
        }
        return new EnumerationRestrictedStringType(name, stringValues);
    }

    public boolean isValidValue(final Value valueToTest) {
        if (!(valueToTest instanceof StringValue)) {
            return false;
        }
        final StringValue stringValue = (StringValue) valueToTest;
        return isValidStringValue(stringValue.getValue());
    }

    protected boolean isValidStringValue(final String valueToTest) {
        return valueToTest != null; // we don't allow nulls since we don't want
                                    // the pain
    }

    @Override
    public boolean canConvertFrom(final Value value) {
        return isValidStringValue(value.getDisplayString());
    }

    @Override
    public Value convertType(final Value value) throws ConversionException {
        return parse(value.getDisplayString());
    }

    @Override
    public boolean canParse(final String text) {
        return isValidStringValue(text);
    }

    @Override
    public Value parse(final String text) throws ConversionException {
        if (isValidStringValue(text)) {
            return new StringValue(text);
        }
        throw new ConversionException("Invalid string value for type");
    }

    public Value toValue(final Element element) {
        return new StringValue(element.getAttributeValue("value"));
    }

    public void insertValue(final Element element, final Value value) {
        final StringValue sValue = (StringValue) value;
        element.setAttribute("value", sValue.getValue());
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        // @TODO implement
    }

    @Override
    protected void addRestrictions(final Element restrictionElement) {
        // no restrictions needed
    }

    @Override
    protected String getBaseType() {
        return "string";
    }

    public static class LengthRestrictedStringType extends StringType {
        private final int minLength;
        private final int maxLength;

        private LengthRestrictedStringType(final String name,
                final int minLength, final int maxLength) {
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

        @Override
        public boolean isValidStringValue(final String valueToTest) {
            if (valueToTest.length() < this.minLength) {
                return false;
            }
            if (valueToTest.length() > this.maxLength) {
                return false;
            }
            return true;
        }

        @Override
        protected void addRestrictions(final Element restElement) {
            final Element minElem = createElement("minLength");
            minElem.setAttribute("minLength", String.valueOf(this.minLength));
            restElement.addContent(minElem);
            final Element maxElem = createElement("maxLength");
            maxElem.setAttribute("maxLength", String.valueOf(this.maxLength));
            restElement.addContent(maxElem);
        }
    }

    public static class PatternRestrictedStringType extends StringType {
        private final Pattern pattern;

        private PatternRestrictedStringType(final String name,
                final String regExp) {
            super(name);
            this.pattern = Pattern.compile(regExp);
        }

        public Pattern getPattern() {
            return pattern;
        }

        @Override
        public boolean isValidStringValue(final String valueToTest) {
            final Matcher matcher = this.pattern.matcher(valueToTest);
            return matcher.matches();
        }

        @Override
        protected void addRestrictions(final Element restElement) {
            final Element pattElem = createElement("pattern");
            pattElem.setAttribute("value", this.pattern.pattern());
            restElement.addContent(pattElem);
        }
    }

    public static class EnumerationRestrictedStringType extends StringType {
        private final StringValue[] enumeration;

        private EnumerationRestrictedStringType(final String name,
                final StringValue[] enumeration) {
            super(name);
            this.enumeration = enumeration;
        }

        @Override
        public boolean isValidStringValue(final String valueToTest) {
            for (final StringValue element : this.enumeration) {
                if (element.getValue().equals(valueToTest)) {
                    return true;
                }
            }
            return false;
        }

        public StringValue[] getEnumeration() {
            return enumeration;
        }

        @Override
        protected void addRestrictions(final Element restElement) {
            for (final StringValue value : this.enumeration) {
                final Element enumElem = createElement("enumeration");
                enumElem.setAttribute("value", value.getValue());
                restElement.addContent(enumElem);
            }
        }
    }
}
