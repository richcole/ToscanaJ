/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.datatype;


public abstract class AbstractDatatype implements Datatype {
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean canConvertFrom(Value value) {
        return false;
    }

    public Value convertType(Value value) {
        throw new IllegalArgumentException("Can not convert datatypes");
    }
}
