package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.XML_Serializable;
import org.jdom.Element;
import java.util.Observable;

public class Model extends Observable
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
};

