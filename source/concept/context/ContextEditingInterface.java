/*
 * Created by IntelliJ IDEA.
 * User: sergey
 * Date: Dec 13, 2001
 * Time: 7:22:12 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package concept.context;

public interface ContextEditingInterface {
    /**
     *  Gets the Attrib attribute of the Context object
     *
     *@param  index  Description of Parameter
     *@return        The Attrib value
     */
    ContextObject getAttribute(int index);

    public void removeAttribute(int index);

    /**
     *  Gets the AttributeCount attribute of the Context object
     *
     *@return    The AttributeCount value
     */
    int getAttributeCount();

    /**
     *  Gets the Object attribute of the Context object
     *
     *@param  index  Description of Parameter
     *@return        The Object value
     */
    ContextObject getObject(int index);

    /**
     *  Gets the ObjectsCount attribute of the Context object
     *
     *@return    The ObjectsCount value
     */
    int getObjectCount();


    public void removeObject(int index);


    /**
     *  Gets the RelationAt attribute of the Context object
     *
     *@param  x  Description of Parameter
     *@param  y  Description of Parameter
     *@return    The RelationAt value
     */
    boolean getRelationAt(int x, int y);

    /**
     *  Sets the RelationAt attribute of the Context object
     *
     *@param  x      The new RelationAt value
     *@param  y      The new RelationAt value
     *@param  value  The new RelationAt value
     */
    void setRelationAt(int x, int y, boolean value);

    /**
     *  Sets the Dimension attribute of the Context object
     *
     *@param  numObj   The new Dimension value
     *@param  numAttr  The new Dimension value
     */
    public void setDimension(int numObj, int numAttr);



    /**
     * Insert the method's description here.
     * Creation date: (19.04.01 2:19:10)
     */
    void addContextListener(ContextListener lst);

    /**
     * Insert the method's description here.
     * Creation date: (19.04.01 2:20:00)
     * @param lst concept.context.ContextListener
     */
    void removeContextListener(ContextListener lst);
}
