package concept.context;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * Insert the type's description here.
 * Creation date: (19.04.01 22:58:26)
 * @author:
 */
public class ContextListenerSupport {
    private transient java.util.ArrayList listeners;
    private final Context cxt;

    public ContextListenerSupport(Context cxt) {
        super();
        this.cxt = cxt;
        clearPostponedStructureChangeFlag();
    }

    public synchronized void addContextListener(ContextListener lst) {
        getListeners().add(lst);
    }

    public synchronized boolean hasListener(ContextListener lst) {
        if (null == listeners) {
            return false;
        }
        return getListeners().contains(lst);
    }


    interface EventBinder {
        void fireEventForCollection(ArrayList events);

    }

    abstract static class ContextListenerEventBinder implements EventBinder {
        public void fireEventForCollection(ArrayList targets) {
            EventObject evt = getEvent();
            for (int i = 0; i < targets.size(); i++) {
                fireEventFor((ContextListener) targets.get(i), evt);
            }
        }

        protected EventObject getEvent() {
            return null;
        }

        protected abstract void fireEventFor(ContextListener listener, EventObject evt);

    }

    protected void eventFireHelper(EventBinder binder) {
        if (null == listeners) {
            return;
        }
        java.util.ArrayList targets;
        synchronized (this) {
            targets = (java.util.ArrayList) listeners.clone();
        }
        binder.fireEventForCollection(targets);
    }

    private boolean structureChangePostponed;

    /**
     * @test_public
     * */
    public boolean hasStructureChangePostponed() {
        return structureChangePostponed;
    }

    public void madePostponedStructureChange() {
        structureChangePostponed = true;
    }


    protected void clearPostponedStructureChangeFlag() {
        structureChangePostponed = false;
    }

    public void realisePostponedStructureChange() {
        if (structureChangePostponed) {
            fireContextStructureChanged();
            util.Assert.isTrue(structureChangePostponed == false);
        }
    }


    public void fireContextStructureChanged() {
        eventFireHelper(new ContextListenerEventBinder() {
            protected void fireEventFor(ContextListener listener, EventObject evt) {
                listener.contextStructureChanged();
            }
        });
        clearPostponedStructureChangeFlag();
    }

    /**
     * Insert the method's description here.
     * Creation date: (19.04.01 23:01:37)
     */
    public void fireRelationChanged() {
        eventFireHelper(new ContextListenerEventBinder() {
            protected void fireEventFor(ContextListener listener, EventObject evt) {
                listener.relationChanged();
            }
        });
    }

    public void fireObjectNameChanged(final PropertyChangeEvent changeEvt) {
        eventFireHelper(new ContextListenerEventBinder() {
            protected java.util.EventObject getEvent() {
                return changeEvt;
            }

            protected void fireEventFor(ContextListener listener, EventObject evt) {
                listener.objectNameChanged((PropertyChangeEvent) evt);
            }
        });
    }

    public void fireAttributeNameChanged(final PropertyChangeEvent changeEvt) {
        eventFireHelper(new ContextListenerEventBinder() {
            protected java.util.EventObject getEvent() {
                return changeEvt;
            }

            protected void fireEventFor(ContextListener listener, EventObject evt) {
                listener.attributeNameChanged((PropertyChangeEvent) evt);
            }
        });
    }

    /**
     * Insert the method's description here.
     * Creation date: (19.04.01 23:02:41)
     * @return java.util.ArrayList
     */
    protected synchronized java.util.ArrayList getListeners() {
        if (null == listeners) {
            listeners = new java.util.ArrayList();
        }
        return listeners;
    }

    /**
     * Insert the method's description here.
     * Creation date: (19.04.01 23:05:21)
     * @param lst concept.context.ContextListener
     */
    public synchronized void removeContextListener(ContextListener lst) {
        if (null == listeners) {
            return;
        }
        listeners.remove(lst);
    }

    static class AttributeEventBinder extends ContextListenerEventBinder {
        protected void fireEventFor(ContextListener listener, EventObject evt) {
            listener.attributeChanged((ContextChangeEvent) evt);
        }
    }

    public void fireAttributeInserted(final int j) {
        eventFireHelper(new AttributeEventBinder() {
            protected EventObject getEvent() {
                return ContextChangeEvent.makeAttributeInsertedEvent(cxt, j);
            }
        });

    }

    public void fireAttributeRemoved(final int index) {
        eventFireHelper(new AttributeEventBinder() {
            protected EventObject getEvent() {
                return ContextChangeEvent.makeAttributeRemovedEvent(cxt, index);
            }
        });
    }
}