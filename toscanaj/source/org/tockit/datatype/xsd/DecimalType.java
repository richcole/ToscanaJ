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
            public Datatype create(Element element) {
                String name = getTypeName(element);
                Element restElem = getRestrictionElement(element);
                boolean minIncluded = false;
                Double min = null;
                Element child = restElem.getChild("minIncluded", XSD_NAMESPACE);
                if(child != null) {
                    minIncluded = true;
                    min = new Double(child.getAttributeValue("value"));
                }
                child = restElem.getChild("minExcluded", XSD_NAMESPACE);
                if(child != null) {
                    if(min != null) {
                        throw new RuntimeException("Type has minIncluded and minExcluded restrictions");
                    }
                    minIncluded = false;
                    min = new Double(child.getAttributeValue("value"));
                }
                boolean maxIncluded = false;
                Double max = null;
                child = restElem.getChild("maxIncluded", XSD_NAMESPACE);
                if(child != null) {
                    maxIncluded = true;
                    max = new Double(child.getAttributeValue("value"));
                }
                child = restElem.getChild("maxExcluded", XSD_NAMESPACE);
                if(child != null) {
                    if(max != null) {
                        throw new RuntimeException("Type has maxIncluded and maxExcluded restrictions");
                    }
                    maxIncluded = false;
                    max = new Double(child.getAttributeValue("value"));
                }
                Integer numDec = null;
                child = restElem.getChild("fractionDigits", XSD_NAMESPACE);
                if(child != null) {
                    numDec = new Integer(child.getAttributeValue("value"));
                }
                return new DecimalType(name, min, minIncluded, max, maxIncluded, numDec);
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
    
    private DecimalType(String name, Double min, boolean minIncluded,
            Double max, boolean maxIncluded, Integer numDecimals) {
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

    public static DecimalType createDecimalType(String name, double min, double max, int numDecimals) {
        return createDecimalType(name, min, true, max, true, numDecimals);
    }

    public static DecimalType createDecimalType(String name, 
            double min, boolean minIncluded, double max, boolean maxIncluded,
            int numDecimals) {
        return new DecimalType(name, new Double(min), minIncluded, new Double(max), maxIncluded, new Integer(numDecimals));
    }
    
    public static DecimalType createUnrestrictedType(String name) {
        return new DecimalType(name, null, true, null, true, null);
    }

    /**
     * Creates a new decimal type based on the restrictions given.
     * 
     * @param name         The name for the new type, must not be null.
     * @param min          The minimum value, can be null for unrestricted.
     * @param minIncluded  Determines if the minimum value is allowed or excluded.
     * @param max          The maximum value, can be null for unrestricted.
     * @param maxIncluded  Determines if the maximum value is allowed or excluded.
     * @param numDecimals  The number of allowed decimals, must not be negative, can be null if unrestricted.
     * @return             A type specification matching the given constraints.
     */
    public static DecimalType createDecimalType(String name, 
            Double min, boolean minIncluded, Double max, boolean maxIncluded,
            Integer numDecimals) {
        return new DecimalType(name, min, minIncluded, max, maxIncluded, numDecimals);
    }

    public boolean isValidValue(Value valueToTest) {
        if(!(valueToTest instanceof DecimalValue)) {
            return false;
        }
        DecimalValue decValue = (DecimalValue) valueToTest;
        return isValidDoubleValue(decValue.getValue());
    }

    public boolean isValidDoubleValue(double value) {
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
        if(this.numDecimals != null) {
            double shifted = value * Math.pow(10, this.numDecimals.intValue());
            if(shifted != Math.floor(shifted)) {
                return false;
            }
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
            return isValidDoubleValue(val);
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public Value parse(String text) throws ConversionException {
        try {
            double val = Double.parseDouble(text);
            if(!isValidDoubleValue(val)) {
                throw new ConversionException("Value out of range");
            }
            return new DecimalValue(val);
        } catch(NumberFormatException e) {
            throw new ConversionException("Can not parse text", e);
        }
    }

    public Value toValue(Element element) {
        return new DecimalValue(Double.parseDouble(element.getAttributeValue("value")));
    }

    public void insertValue(Element element, Value value) {
        DecimalValue dValue = (DecimalValue) value;
        element.setAttribute("value", dValue.toString());
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    	// @TODO implement
    }

    protected String getBaseType() {
        return "decimal";
    }

    protected void addRestrictions(Element restrictionElement) {
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
            Element fracElement = createElement("fractionDigits");
            fracElement.setAttribute("value", String.valueOf(this.numDecimals));
            restrictionElement.addContent(fracElement);
        }
    }
}
