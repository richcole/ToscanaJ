/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;


public interface Datatype extends XMLizable {
    // core methods
    String getName();
    boolean isValidValue(Value valueToTest);
    
    // type conversion
    boolean canConvertFrom(Value value);
    Value convertType(Value value) throws ConversionException;
    boolean canParse(String text);
    Value parse(String text) throws ConversionException;
    
    // XML marshalling/demarshalling of values
    Value toValue(Element element);
    
    /**
     * Adds the given value into the element.
     * 
     * This can be done either by adding a "value" attribute or by adding content.
     * Both are in the default namespace, but which way is used depends on the actual
     * type implementation.
     * 
     * @throws ClassCastException if the value is not of matching type
     */
    void insertValue(Element element, Value value);
}
