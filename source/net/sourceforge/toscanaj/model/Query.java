package net.sourceforge.toscanaj.model;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private String name;

    static private ArrayList queries = new ArrayList();

    /**
     * Returns an iterator on all available queries.
     */
    static public List getQueries() {
        return queries;
    }

    static public void clearQueries() {
        queries.clear();
    }

    public Query(String name) {
        this.name = name;
        queries.add(this);
    }

    public String getName() {
        return this.name;
    }
}