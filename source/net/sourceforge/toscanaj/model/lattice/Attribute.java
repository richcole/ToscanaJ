package net.sourceforge.toscanaj.model.lattice;

import org.jdom.Element;

public class Attribute {
    private String name;
    private Element description;
    public Attribute(String name, Element description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Element getDescription() {
        return this.description;
    }
    
    public String toString() {
        return this.name;
    }
}
