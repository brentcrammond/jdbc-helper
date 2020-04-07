/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.anameg.jdbchelper;

public class JDBCHelperException extends RuntimeException {
    public JDBCHelperException() {
    }

    public JDBCHelperException(String message) {
        super(message);
    }

    public JDBCHelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public JDBCHelperException(Throwable cause) {
        super(cause);
    }
}
