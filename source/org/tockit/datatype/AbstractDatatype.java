/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import org.jdom.Element;


public abstract class AbstractDatatype implements Datatype {
    private String name;
    
    /**
     * Constructor is to be called from the subtypes.
     */
    protected AbstractDatatype(String name) {
        this.name = name;
    }
    
    protected AbstractDatatype(Element element) {
        this.name = element.getAttributeValue("name");
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean canConvertFrom(Value value) {
        return false;
    }

    public Value convertType(Value value) throws ConversionException {
        throw new ConversionException("Can not convert datatypes");
    }
    
    public boolean canParse(String text) {
        return false;
    }
    
    public Value parse(String text) throws ConversionException {
        throw new ConversionException("This type can not parse strings");
    }
}
