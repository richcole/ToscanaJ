package org.tockit.tupleware.source.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class StringTokenizer implements Iterator {
    private String string;
    private char separator;
    private char quote;
    private char escape;
    private int nextChar;
        
    public StringTokenizer(String string, char separator, char quote, char escape) {
        this.string = string;
        this.separator = separator;
        this.quote = quote;
        this.escape = escape;
        this.nextChar = 0;
    }

    public void remove() {
        throw new UnsupportedOperationException("This iterator can not remove anthing");
    }

    public boolean hasNext() {
        return this.nextChar < this.string.length();
    }

    public Object next() {
        StringBuffer retVal = new StringBuffer();
        boolean inQuotes = false;
        do {
            char curChar = this.string.charAt(this.nextChar);
            nextChar++;
            if((curChar == this.separator) && !inQuotes) {
                break;
            } else if(curChar == this.quote) {
                inQuotes = !inQuotes;
            } else if(curChar == this.escape) {
                retVal.append(this.string.charAt(this.nextChar));
                nextChar++;
            } else {
                retVal.append(curChar);
            }
        } while (this.nextChar < this.string.length());
        return retVal.toString();
    }
    
    public String nextToken() {
        return (String) next();
    }
    
    public String[] tokenizeAll() {
        List result = new ArrayList();
        while(this.hasNext()) {
            result.add(next());
        }
        return (String[]) result.toArray(new String[] {});
    }
}
