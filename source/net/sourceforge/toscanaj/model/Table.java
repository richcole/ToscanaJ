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

import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.events.TableChangedEvent;

public class Table {

    private String name;

    private Column key;
    private EventBroker broker;
    private List columns;

    public Table(EventBroker broker, String name) {
        this.columns = new ArrayList();
        this.broker = broker;
        this.name = name;
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
