package net.sourceforge.toscanaj.model.manyvaluedcontext;

public interface WritableManyValuedContext extends ManyValuedContext {
	void setRelationship(FCAObject object, ManyValuedAttribute attribute, AttributeValue value);
}
