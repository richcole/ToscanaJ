/*
 * Created by IntelliJ IDEA.
 * User: sergey
 * Date: Jan 7, 2002
 * Time: 10:11:52 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package concept.context;

public class DefaultBinaryRelationProcessor implements BinaryRelationProcessor {
    protected BinaryRelation rel;

    public void setRelation(BinaryRelation relation) {
        this.rel = relation;
    }

    public void tearDown() {
        rel = null;
    }
}
