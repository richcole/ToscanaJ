/*
 * Date: Dec 2, 2001
 * Time: 11:55:30
 */
package concept.context.tests;

import concept.context.AttributeInformationSupplier;
import concept.context.ContextObject;

public class MockAttributeInformationSupplier implements AttributeInformationSupplier {
    final int attrNo;

    public MockAttributeInformationSupplier(int attrNo) {
        this.attrNo = attrNo;
    }

    public ContextObject getAttribute(int index) {
        return new ContextObject(Integer.toString(attrNo), false);
    }

    public int getAttributeCount() {
        return attrNo;
    }
}
