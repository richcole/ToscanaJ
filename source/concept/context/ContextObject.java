package concept.context;

import util.StringUtil;

import java.beans.PropertyChangeEvent;


public class ContextObject {
    String name;
    ContextObjectListener listener;

    public void setContextObjectListener(ContextObjectListener listener) {
        this.listener = listener;
    }


    boolean obj = false;

    public ContextObject(String name) {
        this(name, false);
    }


    public String getName() {
        return name;
    }

    public boolean isObject() {
        return obj;
    }

    public void setName(String newName) {
        newName = StringUtil.safeTrim(newName);
        if ((this.name == null) ||
                (!this.name.equals(newName))) {
            String oldValue = this.name;
            this.name = newName;
            if (null != listener) {
                listener.nameChanged(new PropertyChangeEvent(this, obj ? "CONTEXT_OBJECT_NAME" : "CONTEXT_ATTRIBUTE_NAME", oldValue, this.name));
            }
        }
    }


    public ContextObject(String name, boolean obj) {
        setName(name);
        this.obj = obj;
    }


    public void makeAttrib() {
        obj = false;
    }


    public void makeObject() {
        obj = true;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ContextObject)) {
            return false;
        }
        ContextObject that = (ContextObject) obj;
        if (!(this.getName().equals(that.getName()))) {
            return false;
        }

        if (this.isObject() != that.isObject()) {
            return false;
        }
        return true;
    }

    public String toString() {
        return (isObject() ? "Object " : "Attribute ") + getName();
    }
}