package util.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit test case for StringUtilTest
 */

public class StringUtilTest extends TestCase {
    private static final Class THIS = StringUtilTest.class;

    public StringUtilTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testTrim() {
        assertEquals("Should be empty string", "", StringUtil.safeTrim(null));
        assertEquals("Should be empty string", "", StringUtil.safeTrim(""));
        assertEquals("Should be empty string", "", StringUtil.safeTrim("  "));
        assertEquals("Should be equal string", "one", StringUtil.safeTrim("one"));
        assertEquals("Should safeTrim", "one", StringUtil.safeTrim("  one  "));
    }

    /**
     * Insert the method's description here.
     * Creation date: (21.05.01 0:52:12)
     */
    public void testGetExtension() {
        assertEquals("", StringUtil.getExtension(null));
        assertEquals("", StringUtil.getExtension("one"));
        assertEquals("", StringUtil.getExtension("one.gif/two"));
        assertEquals("", StringUtil.getExtension("one."));
        assertEquals("gif", StringUtil.getExtension("one.gif"));
    }

    public void testReplaceStringWithNewString() {
        String test = "abc";
        assertEquals(test, StringUtil.replaceStringWithNewString(test, "d", "ef"));
        test = "abcd";
        assertEquals("abcef", StringUtil.replaceStringWithNewString(test, "d", "ef"));

        test = "abcdede";
        assertEquals("abcgfegfe", StringUtil.replaceStringWithNewString(test, "d", "gf"));
    }


    public void testSplit() {
        String toSplit = "aaaaa";
        List splitted = StringUtil.split(toSplit, ",");
        assertEquals(1, splitted.size());

        toSplit = "aa,";
        splitted = StringUtil.split(toSplit, ",");
        assertEquals(1, splitted.size());
        toSplit = "";
        splitted = StringUtil.split(toSplit, ",");
        assertEquals(0, splitted.size());
        toSplit = "aa,ab";
        splitted = StringUtil.split(toSplit, ",");
        assertEquals(2, splitted.size());
    }


    public void testJoin() {
        ArrayList toJoin = new ArrayList();
        assertEquals("", StringUtil.join(toJoin, " "));

        toJoin.add("ab");
        assertEquals("ab", StringUtil.join(toJoin, " "));
        toJoin.add("ac");
        assertEquals("ab ac", StringUtil.join(toJoin, " "));

    }

}