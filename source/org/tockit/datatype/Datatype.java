/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import javax.swing.JComponent;

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
    
    // GUI methods
    // @todo for now we combine the datatypes with Swing. MVC would be good here,
    // XForms (http://www.w3.org/MarkUp/Forms/) might be another option.
    JComponent getTypeEditingComponent();
    JComponent getValueEditingComponent();
    JComponent getSubsetSelectionComponent();
    // @todo the display components are not yet implemented or used
    JComponent getTypeDisplayComponent();
    JComponent getValueDisplayComponent();

    // XML marshalling/demarshalling of values
    Value toValue(Element element);
    Element toElement(Value value);
}
