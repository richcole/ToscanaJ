package util;

import java.text.MessageFormat;


public class DataFormatException extends Exception {
    public DataFormatException() {
        super();
    }


    public DataFormatException(String s) {
        super(s);
    }

    public DataFormatException(String sFormat, Object[] oParams) {
        this(MessageFormat.format(sFormat, oParams));
    }

    public DataFormatException(String sFormat, Object oParam1) {
        this(sFormat, new Object[]{oParam1});
    }

    public DataFormatException(String sFormat, Object oParam1, Object oParam2) {
        this(sFormat, new Object[]{oParam1, oParam2});
    }

    public DataFormatException(String sFormat, int nParam1) {
        this(sFormat, new Object[]{new Integer(nParam1)});
    }

    public DataFormatException(String sFormat, int nParam1, int nParam2) {
        this(sFormat, new Object[]{new Integer(nParam1), new Integer(nParam2)});
    }
}