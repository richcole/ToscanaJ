/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.database;

import java.sql.Types;

/// @todo this is just a quick hack to get rid of the numbers. We should probably put something like
///     this somewhere earlier where we still have access to the whole information, types like varchar
///     should give extra information like their size

public class SQLTypeMapper {
    public static final String getTypeName(int sqlType) {
        switch (sqlType) {
            case Types.ARRAY:
                return "Array";
            case Types.BIGINT:
                return "Big Integer";
            case Types.BINARY:
                return "Binary";
            case Types.BIT:
                return "Bit";
            case Types.BLOB:
                return "Binary Large Object";
            case Types.BOOLEAN:
                return "Boolean";
            case Types.CHAR:
                return "Character";
            case Types.CLOB:
                return "Character Large Object";
            case Types.DATALINK:
                return "Datalink";
            case Types.DATE:
                return "Date";
            case Types.DECIMAL:
                return "Decimal";
            case Types.DISTINCT:
                return "Distinct";
            case Types.DOUBLE:
                return "Double";
            case Types.FLOAT:
                return "Float";
            case Types.INTEGER:
                return "Integer";
            case Types.JAVA_OBJECT:
                return "Java Object";
            case Types.LONGVARBINARY:
                return "Long Variable Sized Binary Array";
            case Types.LONGVARCHAR:
                return "Long Variable Sized Character Array";
            case Types.NULL:
                return "Null";
            case Types.NUMERIC:
                return "Numeric";
            case Types.OTHER:
                return "Other";
            case Types.REAL:
                return "Real";
            case Types.REF:
                return "Reference";
            case Types.SMALLINT:
                return "Small Integer";
            case Types.STRUCT:
                return "Struct";
            case Types.TIME:
                return "Time";
            case Types.TIMESTAMP:
                return "Timestamp";
            case Types.TINYINT:
                return "Tiny Integer";
            case Types.VARBINARY:
                return "Variable Sized Binary Array";
            case Types.VARCHAR:
                return "Variable Sized Character Array";
            default:
                return "Unknown Type (" + sqlType + ")";
        }
    }
}
