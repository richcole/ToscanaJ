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
import org.tockit.datatype.AbstractDatatype;
import org.tockit.datatype.Value;


public class DecimalType extends AbstractDatatype {
    public boolean isValidValue(Value valueToTest) {
        return false;
    }

    public Value toValue(Element element) {
        return null;
    }

    public Element toElement(Value value) {
        return null;
    }

    public Element toXML() {
        return null;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    }
}
