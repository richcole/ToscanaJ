/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.ArrayList;
import java.util.List;

public class View {
    private String name;
    private List criteria = new ArrayList();

    public View(String name) {
        this.name = name;
    }

    public void addCriterion(Criterion criterion) {
        criteria.add(criterion);
    }

    public List getCriteria() {
        return criteria;
    }

    public String getName() {
        return name;
    }
}
