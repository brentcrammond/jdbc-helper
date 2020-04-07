/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.h4t.jdbchelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is the default PreparedStatementCreator, which takes an SQL and an array of parameter objects and initialises the PreparedStatement.
 */
public class DefaultPreparedStatementCreator implements PreparedStatementCreator {
    private final String sql;
    private final Object[] pms;

    public DefaultPreparedStatementCreator(String sql, Object... pms) {
        this.sql = sql;
        this.pms = pms;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        int i = 1;
        for (Object obj : pms) {
            ps.setObject(i++, obj);
        }
        return ps;
    }
}
