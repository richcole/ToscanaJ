/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 27, 2002
 * Time: 5:55:54 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import concept.context.Context;
import org.jdom.Element;

public class DefaultScale implements Scale {
    private Context context = new Context(0,0);

    public Element toXML() {
        return new Element("Scale");
    }

    public void readXML(Element elem) throws XML_SyntaxError {
    }
}
