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
     * to some controller object or similar
     */
    protected static boolean allHidden = false;

    public static void setAllHidden(boolean allHidden) {
        SqlClauseLabelView.allHidden = allHidden;
    }

    public static boolean allAreHidden() {
        return allHidden;
    }

    public boolean isVisible() {
        return super.isVisible() && !allHidden;
    }

	private List entries;
	
    public static LabelFactory getFactory() {
        return new LabelFactory(){
            public LabelView createLabelView(DiagramView diagramView,NodeView nodeView,LabelInfo label){
                return new SqlClauseLabelView(diagramView, nodeView, label);
            }

			public Class getLabelClass() {
				return SqlClauseLabelView.class;
			}
        };
    }

    protected SqlClauseLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        super(diagramView, nodeView, label);
        this.labelInfo.setTextAlignment(LabelInfo.ALIGNLEFT);
    }

    protected int getPlacement() {
        return LabelView.BELOW;
    }

    public int getNumberOfEntries() {
        return this.entries.size();
    }

    public Iterator getEntryIterator() {
        return this.entries.iterator();
    }

    protected boolean highlightedInIdeal() {
        return true;
    }

    protected boolean highlightedInFilter() {
        return false;
    }

    protected boolean isFaded() {
        return nodeView.getSelectionState() == DiagramView.NOT_SELECTED;
    }
    
    public void updateEntries() {
        this.entries = new ArrayList();
    	Iterator objIt = this.labelInfo.getNode().getConcept().getObjectContingentIterator();
    	while (objIt.hasNext()) {
            String object = objIt.next().toString();
            addObjectEntries(object);
        }
        super.updateEntries();
    }
    
    private void addObjectEntries(String object) {
    	StringTokenizer tokenizer = new StringTokenizer(object, " \t\n\r\f", true);
    	String nextEntry;
    	boolean first = true;
    	while(tokenizer.hasMoreTokens()) {
    		nextEntry = null;
    		String token;
    		do {
	    		token = tokenizer.nextToken().trim();
	    		if (token.equals("")) {
	    			continue;
	    		}
	    		if(nextEntry == null) {
	    			nextEntry = token;
	    		} else {
   		        	nextEntry += " " + token;
	    		}
    		} while (tokenizer.hasMoreTokens() && !token.toLowerCase().equals("and") &&
    					!token.toLowerCase().equals("or"));
    	    if(first) {
    	        first = false;
    	        this.entries.add(nextEntry);
    	    } else {
    	        this.entries.add("   " + nextEntry);
    	    }
    	}
    }
}
