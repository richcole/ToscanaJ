/*
 * Created by IntelliJ IDEA.
 * User: hereth
 * Date: 03.07.2002
 * Time: 18:26:27
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
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
