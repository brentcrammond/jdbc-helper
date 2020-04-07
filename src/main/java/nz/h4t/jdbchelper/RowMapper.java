/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.h4t.jdbchelper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface defines how to convert ResultSet rows to an Object.
 */
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs, int rowIdx) throws SQLException;
}
