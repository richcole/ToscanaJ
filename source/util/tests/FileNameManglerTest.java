package util.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.FileNameMangler;

import java.io.File;

/**
 * JUnit test case for StringUtilTest
 */

public class FileNameManglerTest extends TestCase {
    private static final Class THIS = FileNameManglerTest.class;

    public FileNameManglerTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testGetFileDirectory() {
        String expDir = "c:" + File.separator + "testPath" + File.separator;
        assertEquals(expDir, FileNameMangler.getFileDirectory("c:\\testPath\\file"));
        assertEquals(expDir, FileNameMangler.getFileDirectory("c:\\testPath/file"));
        assertEquals(expDir, FileNameMangler.getFileDirectory("c:\\testPath\\"));
    }

    public void testNormalizeDirName() {
        String expDir = "c:" + File.separator + "testPath" + File.separator;
        assertEquals(expDir, FileNameMangler.normalizeDirectoryName("c:\\testPath"));
        assertEquals(expDir, FileNameMangler.normalizeDirectoryName("c:\\testPath/"));
        assertEquals(expDir, FileNameMangler.normalizeDirectoryName("c:\\testPath\\"));
    }

    public void testGetBaseFileName() {
        assertEquals("a", FileNameMangler.getBaseFileName("c:\\testPath\\a"));
        assertEquals("a", FileNameMangler.getBaseFileName("c:\\testPath\\a.txt"));
        assertEquals("a.txt", FileNameMangler.getBaseFileName("c:\\testPath/a.txt.old"));
        assertEquals("a.txt", FileNameMangler.getBaseFileName("c:\\testPath/a.txt.old"));
        assertEquals("", FileNameMangler.getBaseFileName("c:\\testPath/"));
        assertEquals("a", FileNameMangler.getBaseFileName("c:\\testPath/a."));
        assertEquals("", FileNameMangler.getBaseFileName("c:\\testPath/.a"));
    }
}