/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 25, 2002
 * Time: 12:04:54 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import java.util.HashMap;

public class ViewListModel extends Model {
    HashMap parentMap = new HashMap();

    class ModelViewInfo {
        public ModelViewInfo(String description, String parentName) {
            this.description = description;
            this.parentName = parentName;
        }

        public String description;
        public String parentName;
    };

    public void register(String childDescr,String childName, String parentName) {
        parentMap.put(
            childName,
            new ModelViewInfo(childDescr, parentName)
        );
        notifyObservers(true, childName);
        System.out.println("Observers Notified");
    }

    public HashMap getParentMap() {
        return parentMap;
    };

    public String description(String childName) {
        return ((ModelViewInfo)parentMap.get(childName)).description;
    }

    public String parentName(String childName) {
        return ((ModelViewInfo)parentMap.get(childName)).parentName;
    }
}
