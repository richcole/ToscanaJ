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
    void setName(String name);
    boolean isValidValue(Value valueToTest);
    
    // type conversion
    boolean canConvertFrom(Value value);
    Value convertType(Value value);
    
    // XML marshalling/demarshalling of values
    Value toValue(Element element);
    Element toElement(Value value);
}
