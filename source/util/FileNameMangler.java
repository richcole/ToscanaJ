package util;

import java.io.File;

public class FileNameMangler {

    String currFileName;

    /**
     *  @return file name without extension
     */
    public String getBasicName() {
        if (currFileName == null) {
            return "Unnamed";
        }
        return getBaseFileName(currFileName);
    }

    public static String getBaseFileName(String currFileName) {
        if (currFileName == null) {
            return null;
        }
        currFileName = getFileName(currFileName);
        int dotPos = currFileName.lastIndexOf('.');
        if (dotPos >= 0) {
            return currFileName.substring(0, dotPos);
        }
        return currFileName;
    }

    /**
     * Get the value of currFileName.
     * @return Value of currFileName.
     */
    public String getCurrFileName() {
        return currFileName;
    }

    /**
     *  @return file name with extension
     */

    public String getFileName() {
        if (null == currFileName) {
            return "Unknown";
        }
        return getFileName(currFileName);
    }

    /**
     *  @return file dir
     */
    public String getOutPath() {
        return getFileDirectory(this.currFileName);
    }

    public static String getFileDirectory(String currFileName) {
        currFileName = normalizeFileName(currFileName);
        if (currFileName != null) {
            int pos = currFileName.lastIndexOf(File.separator);
            if (pos >= 0) {
                return currFileName.substring(0, pos + 1);
            } else {
                return null;
            }
        }
        return null;
    }

    public static String getFileExtension(String strPath) {
        int nCommaLastIndex = strPath.lastIndexOf(".");
        if ((-1 == nCommaLastIndex) || (strPath.length() - 1 == nCommaLastIndex)) {
            return "";
        }
        return strPath.substring(nCommaLastIndex + 1, strPath.length());
    }

    /**
     * Set the value of currFileName.
     * @param v  Value to assign to currFileName.
     */
    public void setCurrFileName(String v) {
        this.currFileName = v;
    }

    public static String normalizeDirectoryName(String dir) {
        dir = normalizeFileName(dir);
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        return dir;
    }

    public static String getFileName(String strFullPath) {
        if (strFullPath != null) {
            strFullPath = normalizeFileName(strFullPath);

            int pos = strFullPath.lastIndexOf(File.separatorChar);
            if (pos >= 0) {
                return strFullPath.substring(pos + 1);
            } else {
                return new String(strFullPath);
            }
        }
        return null;
    }

    public static String normalizeFileName(String strFullPath) {
        strFullPath = strFullPath.replace('\\', File.separatorChar);
        strFullPath = strFullPath.replace('/', File.separatorChar);
        return strFullPath;
    }
}