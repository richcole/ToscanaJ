package concept.context.tests;

import com.mockobjects.ExpectationCounter;
import concept.context.DefaultContextListener;

/**
 * Insert the type's description here.
 * Creation date: (23.12.00 13:33:41)
 * @author:
 */
public class MockContextListener extends DefaultContextListener {
    protected com.mockobjects.ExpectationCounter counter = new ExpectationCounter("Expected number of calls");

    /**
     * Insert the method's description here.
     * Creation date: (23.12.00 13:38:05)
     */
    public void setExpectedCalls(int cnt) {
        counter.setExpected(cnt);
    }

    /**
     * Insert the method's description here.
     * Creation date: (23.12.00 13:40:06)
     */
    public void verify() {
        counter.verify();
    }
}