package util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (26.12.00 1:06:00)
 * @author:
 */
public class StringUtil {
    public static String formatPercents(double val) {
        return java.text.DecimalFormat.getPercentInstance().format(val);
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.05.01 0:51:45)
     * @return java.lang.String
     * @param path java.lang.String
     */
    public static String getExtension(String path) {
        path = safeTrim(path);
        int dotPos = path.lastIndexOf('.');
        if ('/' == File.separatorChar) {
            path = path.replace('\\', File.separatorChar);
        } else {
            path = path.replace('/', File.separatorChar);
        }
        int sepPos = path.lastIndexOf(File.separatorChar);

        if (sepPos >= dotPos) {
            return "";
        }
        return path.substring(dotPos + 1);
    }


    /**
     * Insert the method's description here.
     * Creation date: (04.06.01 13:38:42)
     * @return java.lang.String
     * @param ex java.lang.Exception
     */
    public static String stackTraceToString(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }


    /**
     * Insert the method's description here.
     * Creation date: (10.02.01 0:07:15)
     * @return java.lang.String
     * @param s java.lang.String
     */
    public static String safeTrim(String s) {
        if (null == s) {
            return "";
        }
        return s.trim();
    }

    public static boolean isEmpty(String toCheck) {
        String temp = safeTrim(toCheck);
        if ("".equals(temp)) {
            return true;
        }
        return false;
    }

    public static String replaceStringWithNewString(String toProcess, String toReplace, String replaceWith) {
        int currPos = toProcess.indexOf(toReplace, 0);
        if (currPos == -1) {
            return toProcess;
        }

        StringBuffer ret = new StringBuffer();
        int prevPos = 0;

        do {
            ret.append(toProcess.substring(prevPos, currPos));
            ret.append(replaceWith);

            prevPos = currPos + toReplace.length();
            currPos = toProcess.indexOf(toReplace, prevPos);
        } while (currPos != -1);

        ret.append(toProcess.substring(prevPos));

        return ret.toString();
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.07.01 14:01:46)
     * @return java.lang.String
     */
    public static String extractClassName(String className) {
        int indexOfDot = className.lastIndexOf('.');
        if (indexOfDot > 0) {
            if (indexOfDot < className.length() - 1) {
                return className.substring(indexOfDot + 1);
            }
        }
        return className;
    }

    public static List split(String str, String delim) {
        StringTokenizer tokenizer = new StringTokenizer(str, delim);
        ArrayList result = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            if (!StringUtil.isEmpty(nextToken)) {
                result.add(nextToken);
            }
        }
        return result;
    }

    public static String join(Collection strList, String delim) {
        StringBuffer ret = new StringBuffer();
        join(ret, strList, delim);
        return ret.toString();
    }

    public static void join(StringBuffer buf, Collection strList, String delim) {
        Iterator iter = strList.iterator();
        boolean first = true;
        while (iter.hasNext()) {
            String str = (String) iter.next();
            if (first) {
                first = false;
            } else {
                buf.append(delim);
            }
            buf.append(str);
        }
    }

}