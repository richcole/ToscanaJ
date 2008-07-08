/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;

/**
 * A LabelView for displaying the SQL clauses.
 */
public class SqlClauseLabelView extends LabelView {
    /**
     * @todo this is a quick hack to get a hide all feature, should be changed
     *       to some controller object or similar
     */
    protected static boolean allHidden = false;

    public static void setAllHidden(final boolean allHidden) {
        SqlClauseLabelView.allHidden = allHidden;
    }

    public static boolean allAreHidden() {
        return allHidden;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && !allHidden;
    }

    // / @todo use String[] instead
    private List<String> entries;

    public static LabelFactory getFactory() {
        return new LabelFactory() {
            public LabelView createLabelView(final DiagramView diagramView,
                    final NodeView nodeView, final LabelInfo label) {
                // enforce left align, independent of align given for the normal
                // label
                final LabelInfo newLabelInfo = new LabelInfo(label);
                newLabelInfo.setTextAlignment(LabelInfo.ALIGNLEFT);
                return new SqlClauseLabelView(diagramView, nodeView,
                        newLabelInfo);
            }

            public Class getLabelClass() {
                return SqlClauseLabelView.class;
            }
        };
    }

    protected SqlClauseLabelView(final DiagramView diagramView,
            final NodeView nodeView, final LabelInfo label) {
        super(diagramView, nodeView, label);
    }

    @Override
    protected int getPlacement() {
        return LabelView.BELOW;
    }

    @Override
    public int getNumberOfEntries() {
        return this.entries.size();
    }

    @Override
    public Object getEntryAt(final int position) {
        return this.entries.get(position);
    }

    @Override
    protected boolean highlightedInIdeal() {
        return true;
    }

    @Override
    protected boolean highlightedInFilter() {
        return false;
    }

    @Override
    protected boolean isFaded() {
        return nodeView.getSelectionState() == DiagramView.NOT_SELECTED;
    }

    @Override
    public void updateEntries() {
        this.entries = new ArrayList<String>();
        final Iterator objIt = this.labelInfo.getNode().getConcept()
                .getObjectContingentIterator();
        while (objIt.hasNext()) {
            final String object = objIt.next().toString();
            addObjectEntries(object);
        }
        super.updateEntries();
    }

    private void addObjectEntries(final String object) {
        final StringTokenizer tokenizer = new StringTokenizer(object,
                " \t\n\r\f", true);
        String nextEntry;
        boolean first = true;
        while (tokenizer.hasMoreTokens()) {
            nextEntry = null;
            String token;
            do {
                token = tokenizer.nextToken().trim();
                if (token.equals("")) {
                    continue;
                }
                if (nextEntry == null) {
                    nextEntry = token;
                } else {
                    nextEntry += " " + token;
                }
            } while (tokenizer.hasMoreTokens()
                    && !token.toLowerCase().equals("and")
                    && !token.toLowerCase().equals("or"));
            if (first) {
                first = false;
                this.entries.add(nextEntry);
            } else {
                this.entries.add("   " + nextEntry);
            }
        }
    }
}
