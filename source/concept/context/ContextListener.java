package concept.context;

import java.beans.PropertyChangeEvent;

/**
 * Insert the type's description here.
 * Creation date: (19.04.01 1:39:08)
 * @author:
 */
public interface ContextListener {
    void contextStructureChanged();

    void relationChanged();

    void objectNameChanged(PropertyChangeEvent evt);

    void attributeNameChanged(PropertyChangeEvent evt);

    void attributeChanged(ContextChangeEvent changeEvent);
}