/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.anameg.jdbchelper;

public class JdbcHelperException extends RuntimeException {
    public JdbcHelperException() {
    }

    public JdbcHelperException(String message) {
        super(message);
    }

    public JdbcHelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcHelperException(Throwable cause) {
        super(cause);
    }
}
