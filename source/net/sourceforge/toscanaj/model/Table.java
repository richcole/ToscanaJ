/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 5:52:40 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import util.CollectionFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.events.TableChangedEvent;
import org.jdom.Element;

public class Table implements XML_Serializable {

    private String name;

    private Column key;
    private EventBroker broker;
    private List columns;
    private static final String TABLE_ELEMENT_NAME = "table";
    private static final String TABLE_NAME_ATTRIBUTE_NAME = "name";

    public Table(EventBroker broker, String name) {
        this.columns = new ArrayList();
        this.broker = broker;
        this.name = name;
    }

    public Element toXML() {
        Element retVal = new Element(TABLE_ELEMENT_NAME);
        retVal.setAttribute(TABLE_NAME_ATTRIBUTE_NAME, name);
        if( key != null ) {
            retVal.addContent(key.toXML());
        }
        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
            Column column = (Column) iterator.next();
            retVal.addContent(column.toXML());
        }
        return retVal;
    }

    public void readXML(Element elem) throws XML_SyntaxError {
        throw new XML_SyntaxError("Not yet implemented");
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String getName() {
        return name;
    }

    public Column getKey() {
        return key;
    }

    public String getKeyName() {
        return key.getName();
    }

    public List getColumns() {
        return columns;
    }

    public void setKey(Column key) {
        this.key = key;
        broker.processEvent(new TableChangedEvent(this, this));
    }

    public void setName(String name) {
        this.name = name;
    }

}
