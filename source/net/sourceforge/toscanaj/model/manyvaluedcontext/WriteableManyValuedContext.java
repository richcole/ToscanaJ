package net.sourceforge.toscanaj.model.manyvaluedcontext;

public interface WriteableManyValuedContext extends ManyValuedContext {
	
	void setRelationship(FCAObject object, ManyValuedAttribute attribute, AttributeValue value);
	void updateObject(String string, int index);

}
