/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types;

import org.jdom.Element;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleColumn;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

public class NumericalType extends TypeImplementation implements XMLizable{
	private double minValue;
	private double maxValue;
	private int numOfDecimals;
	
	private static final String MIN_ATTRIBUTE_NAME = "min";
	private static final String MAX_ATTRIBUTE_NAME = "max";
	private static final String NUMBER_OF_DECIMALS_ATTRIBUTE_NAME = "numberOfDecimals";
	
    public NumericalType(String name) {
        super(name);
    }

    public void addValueGroup(ScaleColumn column, String id) {
        if (column instanceof NumericalValueGroup) {
            scale.addColumn(column, id);
            return;
        }
        throw new RuntimeException("Wrong value group type");
    }

	public void setMinimumValue(double minValue) {
		this.minValue = minValue;
	}
	
	public void setMaximumValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public void setNumberOfDecimals(int numOfDecimals) {
		this.numOfDecimals = numOfDecimals;
	}
	
	public double getMinimumValue(){
		return minValue;
	}
	
	public double getMaximumValue() {
		return maxValue;
	}
	
	public int getNumOfDecimals(){
		return numOfDecimals;
	}
	
	public boolean isValidValue(AttributeValue valueToTest) {
		if(!(valueToTest instanceof NumericalValue)) {
			return false;
		}
		NumericalValue numVal = (NumericalValue) valueToTest;
		double value = numVal.getValue();
		if(value >= getMinimumValue() && value <= getMaximumValue()){
				return true;
		}
		return false;
	}
	public AttributeValue[] getValueRange() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#toXML()
	 */
	public void fillRangeElement(Element rangeElement) {
		rangeElement.setAttribute(MIN_ATTRIBUTE_NAME, String.valueOf(minValue));
		rangeElement.setAttribute(MAX_ATTRIBUTE_NAME, String.valueOf(maxValue));
		rangeElement.setAttribute(NUMBER_OF_DECIMALS_ATTRIBUTE_NAME, String.valueOf(numOfDecimals));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#readXML(org.jdom.Element)
	 */
	public void readRangeElement(Element rangeElement) throws XMLSyntaxError {
		this.minValue = XMLHelper.getDoubleAttribute(rangeElement, MIN_ATTRIBUTE_NAME);
		this.maxValue = XMLHelper.getDoubleAttribute(rangeElement, MAX_ATTRIBUTE_NAME);
		this.numOfDecimals = XMLHelper.getIntAttribute(rangeElement, NUMBER_OF_DECIMALS_ATTRIBUTE_NAME);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType#toValue(org.jdom.Element)
	 */
	public AttributeValue toValue(Element element) {
		// TODO Auto-generated method stub
		return null;
	}
}
