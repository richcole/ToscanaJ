/*
 * User: sergey
 * Date: Dec 22, 2001
 * Time: 6:10:39 PM
 */
package util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class IntArrayWrapper {
    final int [] array;

    public IntArrayWrapper(int[] array) {
        this.array = array;
    }

    public boolean equals(Object other) {
        if(!(other instanceof IntArrayWrapper)){
            return false;
        }
        if(other == this){
            return true;
        }
        return Arrays.equals(array, ((IntArrayWrapper)other).array);
    }

    public int hashCode() {
        int ret = 0;
        if(array==null){
            return ret;
        }
        for(int i=0; i<array.length; i++){
            ret+= (array[i] << (i % 32));
        }
        return  ret;
    }

    public List toList(){
        ArrayList ret = new ArrayList(array.length);
        for(int i=0; i<array.length; i++){
            ret.add(new Integer(array[i]));
        }
        return ret;
    }

    public String toString() {
        if(array==null){
            return "Null array";
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("int [");
        buffer.append(array.length);
        buffer.append("] { ");
        boolean first = true;
        for(int i=0; i<array.length; i++){
            if(first){
                first = false;
            }else{
                buffer.append(" , ");
            }
            buffer.append(array[i]);
        }
        buffer.append(" } ");
        return buffer.toString();
    }
}
