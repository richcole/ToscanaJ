package net.sourceforge.toscanaj.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Query {
    private String name;

    public Query(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}