/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;

import java.awt.*;

public class ColorWriter {

    private static final String chars="0123456789abcdef";

    private static String toHex(int i){
        int low = i%16;
        int high = (i/16) % 16;
        return chars.charAt(low)+ "" + chars.charAt(high);
    }

    private static int fromHex(String s){
        return chars.indexOf(s.charAt(0))*16+chars.indexOf(s.charAt(1));
    }

    public static Color fromHexString(String s){
        Color color = new Color(fromHex(s.substring(2,4)),
                                fromHex(s.substring(4,6)),
                                fromHex(s.substring(6,8)),
                                fromHex(s.substring(0,2)));
        return color;

    }

    public static String toHexString(Color color){
        return toHex(color.getAlpha()) +
                toHex(color.getRed()) +
                toHex(color.getGreen()) +
                toHex(color.getBlue());
    }
}
