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
    public static final String getTypeDescription(int sqlType) {
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

    public static final String getSQLName(int sqlType) {
        switch (sqlType) {
            case Types.ARRAY:
                return "array";
            case Types.BIGINT:
                return "bigint";
            case Types.BINARY:
                return "binary";
            case Types.BIT:
                return "bit";
            case Types.BLOB:
                return "blob";
            case Types.BOOLEAN:
                return "boolean";
            case Types.CHAR:
                return "char";
            case Types.CLOB:
                return "clob";
            case Types.DATALINK:
                return "datalink";
            case Types.DATE:
                return "date";
            case Types.DECIMAL:
                return "decimal";
            case Types.DISTINCT:
                return "distinct";
            case Types.DOUBLE:
                return "double";
            case Types.FLOAT:
                return "float";
            case Types.INTEGER:
                return "integer";
            case Types.JAVA_OBJECT:
                return "java_object";
            case Types.LONGVARBINARY:
                return "longvarbinary";
            case Types.LONGVARCHAR:
                return "longvarchar";
            case Types.NULL:
                return "null";
            case Types.NUMERIC:
                return "numeric";
            case Types.OTHER:
                return "other";
            case Types.REAL:
                return "real";
            case Types.REF:
                return "ref";
            case Types.SMALLINT:
                return "smallint";
            case Types.STRUCT:
                return "struct";
            case Types.TIME:
                return "time";
            case Types.TIMESTAMP:
                return "timestamp";
            case Types.TINYINT:
                return "tinyint";
            case Types.VARBINARY:
                return "varbinary";
            case Types.VARCHAR:
                return "varchar";
            default:
                throw new RuntimeException("Unknown SQL type: " + sqlType);
        }
    }
}
