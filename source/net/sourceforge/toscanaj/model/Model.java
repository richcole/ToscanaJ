package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.events.EventBroker;
import org.jdom.Element;
import java.util.Observable;

public abstract class Model extends Observable
{
    public void notifyObservers(boolean isChanged) {
        if ( isChanged ) {
            setChanged();
        }
        else {
            clearChanged();
        }
        notifyObservers();
        clearChanged();
    }

    public void notifyObservers(boolean isChanged, Object object) {
        if ( isChanged ) {
            setChanged();
        }
        else {
            clearChanged();
        }
        notifyObservers(object);
        clearChanged();
    }
};

