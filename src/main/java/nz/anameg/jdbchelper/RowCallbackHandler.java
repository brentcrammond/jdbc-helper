/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.anameg.jdbchelper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Processes as single row from a ResultSet row.
 */
@FunctionalInterface
public interface RowCallbackHandler {
    void processRow(ResultSet rs) throws SQLException;
}
