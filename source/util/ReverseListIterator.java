/*
 * Created by IntelliJ IDEA.
 * User: Serhiy Yevtushenko
 * Date: May 31, 2002
 * Time: 8:34:28 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReverseListIterator implements Iterator {
    List originalList;
    ListIterator reverseIterator;

    public ReverseListIterator(List originalList) {
        this.originalList = originalList;
        reverseIterator = originalList.listIterator(originalList.size());
    }

    public boolean hasNext() {
        return reverseIterator.hasPrevious();
    }

    public Object next() {
        return reverseIterator.previous();
    }

    public void remove() {
        reverseIterator.remove();
    }
}
