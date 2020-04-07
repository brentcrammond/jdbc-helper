/*
 * License:
 * <p>
 * Copyright 2020 Anameg Consulting Ltd
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
