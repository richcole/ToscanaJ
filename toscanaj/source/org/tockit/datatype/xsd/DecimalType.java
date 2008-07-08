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
import org.tockit.datatype.ConversionException;
import org.tockit.datatype.Datatype;
import org.tockit.datatype.DatatypeFactory;
import org.tockit.datatype.Value;

/**
 * @todo we lack support for the general decimal type without restriction
 */
public class DecimalType extends AbstractXSDDatatype {
    public static DatatypeFactory.TypeCreator getTypeCreator() {
        return new TypeCreator("decimal") {
            public Datatype create(final Element element) {
                final String name = getTypeName(element);
                final Element restElem = getRestrictionElement(element);
                boolean minIncluded = false;
                Double min = null;
                Element child = restElem.getChild("minIncluded", XSD_NAMESPACE);
                if (child != null) {
                    minIncluded = true;
                    min = new Double(child.getAttributeValue("value"));
                }
                child = restElem.getChild("minExcluded", XSD_NAMESPACE);
                if (child != null) {
                    if (min != null) {
                        throw new RuntimeException(
                                "Type has minIncluded and minExcluded restrictions");
                    }
                    minIncluded = false;
                    min = new Double(child.getAttributeValue("value"));
                }
                boolean maxIncluded = false;
                Double max = null;
                child = restElem.getChild("maxIncluded", XSD_NAMESPACE);
                if (child != null) {
                    maxIncluded = true;
                    max = new Double(child.getAttributeValue("value"));
                }
                child = restElem.getChild("maxExcluded", XSD_NAMESPACE);
                if (child != null) {
                    if (max != null) {
                        throw new RuntimeException(
                                "Type has maxIncluded and maxExcluded restrictions");
                    }
                    maxIncluded = false;
                    max = new Double(child.getAttributeValue("value"));
                }
                Integer numDec = null;
                child = restElem.getChild("fractionDigits", XSD_NAMESPACE);
                if (child != null) {
                    numDec = new Integer(child.getAttributeValue("value"));
                }
                return new DecimalType(name, min, minIncluded, max,
                        maxIncluded, numDec);
            }
        };
    }

    /**
     * Null means open.
     */
    private final Double min;
    /**
     * Null means open.
     */
    private final Double max;
    private final boolean minIncluded;
    private final boolean maxIncluded;
    /**
     * Null means open.
     */
    private final Integer numDecimals;

    private DecimalType(final String name, final Double min,
            final boolean minIncluded, final Double max,
            final boolean maxIncluded, final Integer numDecimals) {
        super(name);
        if (min != null && max != null) {
            if (min.doubleValue() > max.doubleValue()) {
                throw new IllegalArgumentException(
                        "Minimum greater than maximum value for type");
            }
            if (min.doubleValue() == max.doubleValue()
                    && !(minIncluded && maxIncluded)) {
                throw new IllegalArgumentException(
                        "Both minimum and maximum have to be included if equal.");
            }
        }
        this.min = min;
        this.minIncluded = minIncluded;
        this.max = max;
        this.maxIncluded = maxIncluded;
        this.numDecimals = numDecimals;
    }

    public static DecimalType createDecimalType(final String name,
            final double min, final double max, final int numDecimals) {
        return createDecimalType(name, min, true, max, true, numDecimals);
    }

    public static DecimalType createDecimalType(final String name,
            final double min, final boolean minIncluded, final double max,
            final boolean maxIncluded, final int numDecimals) {
        return new DecimalType(name, new Double(min), minIncluded, new Double(
                max), maxIncluded, new Integer(numDecimals));
    }

    public static DecimalType createUnrestrictedType(final String name) {
        return new DecimalType(name, null, true, null, true, null);
    }

    /**
     * Creates a new decimal type based on the restrictions given.
     * 
     * @param name
     *            The name for the new type, must not be null.
     * @param min
     *            The minimum value, can be null for unrestricted.
     * @param minIncluded
     *            Determines if the minimum value is allowed or excluded.
     * @param max
     *            The maximum value, can be null for unrestricted.
     * @param maxIncluded
     *            Determines if the maximum value is allowed or excluded.
     * @param numDecimals
     *            The number of allowed decimals, must not be negative, can be
     *            null if unrestricted.
     * @return A type specification matching the given constraints.
     */
    public static DecimalType createDecimalType(final String name,
            final Double min, final boolean minIncluded, final Double max,
            final boolean maxIncluded, final Integer numDecimals) {
        return new DecimalType(name, min, minIncluded, max, maxIncluded,
                numDecimals);
    }

    public boolean isValidValue(final Value valueToTest) {
        if (!(valueToTest instanceof DecimalValue)) {
            return false;
        }
        final DecimalValue decValue = (DecimalValue) valueToTest;
        return isValidDoubleValue(decValue.getValue());
    }

    public boolean isValidDoubleValue(final double value) {
        if (this.min != null) {
            if (value < this.min.doubleValue()) {
                return false;
            }
            if (value == this.min.doubleValue() && !this.minIncluded) {
                return false;
            }
        }
        if (this.max != null) {
            if (value > this.max.doubleValue()) {
                return false;
            }
            if (value == this.max.doubleValue() && !this.maxIncluded) {
                return false;
            }
        }
        if (this.numDecimals != null) {
            final double shifted = value
                    * Math.pow(10, this.numDecimals.intValue());
            if (shifted != Math.floor(shifted)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canConvertFrom(final Value value) {
        if (value instanceof StringValue) {
            return canParse(((StringValue) value).getValue());
        } else {
            return false;
        }
    }

    @Override
    public Value convertType(final Value value) throws ConversionException {
        if (value instanceof StringValue) {
            return parse(((StringValue) value).getValue());
        } else {
            return super.convertType(value);
        }
    }

    @Override
    public boolean canParse(final String text) {
        try {
            final double val = Double.parseDouble(text);
            return isValidDoubleValue(val);
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Value parse(final String text) throws ConversionException {
        try {
            final double val = Double.parseDouble(text);
            if (!isValidDoubleValue(val)) {
                throw new ConversionException("Value out of range");
            }
            return new DecimalValue(val);
        } catch (final NumberFormatException e) {
            throw new ConversionException("Can not parse text", e);
        }
    }

    public Value toValue(final Element element) {
        return new DecimalValue(Double.parseDouble(element
                .getAttributeValue("value")));
    }

    public void insertValue(final Element element, final Value value) {
        final DecimalValue dValue = (DecimalValue) value;
        element.setAttribute("value", dValue.toString());
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        // @TODO implement
    }

    @Override
    protected String getBaseType() {
        return "decimal";
    }

    @Override
    protected void addRestrictions(final Element restrictionElement) {
        if (this.min != null) {
            Element minElement;
            if (this.minIncluded) {
                minElement = createElement("minIncluded");
            } else {
                minElement = createElement("minExcluded");
            }
            minElement.setAttribute("value", String.valueOf(this.min));
            restrictionElement.addContent(minElement);
        }

        if (this.max != null) {
            Element maxElement;
            if (this.maxIncluded) {
                maxElement = createElement("maxIncluded");
            } else {
                maxElement = createElement("maxExcluded");
            }
            maxElement.setAttribute("value", String.valueOf(this.max));
            restrictionElement.addContent(maxElement);
        }

        if (this.numDecimals != null) {
            final Element fracElement = createElement("fractionDigits");
            fracElement.setAttribute("value", String.valueOf(this.numDecimals));
            restrictionElement.addContent(fracElement);
        }
    }
}
