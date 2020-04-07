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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple replacement for JDBCTemplate without the reliance on Spring Framework.
 */
public class JDBCHelper {
    private final DataSource ds;

    public JDBCHelper(DataSource ds) {
        this.ds = ds;
    }

    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> mapper) {
        List<T> lst = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = psc.createPreparedStatement(conn)) {
                try (ResultSet rs = ps.executeQuery()) {
                    int rowIdx = 0;
                    while (rs.next()) {
                        T val = mapper.mapRow(rs, rowIdx++);
                        if (val != null) {
                            lst.add(val);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new JDBCHelperException(e);
        }
        return lst;
    }

    public <T> Optional<T> queryObject(PreparedStatementCreator psc, RowMapper<T> mapper) {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = psc.createPreparedStatement(conn)) {
                ps.setMaxRows(1);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        T val = mapper.mapRow(rs, 0);
                        if (val != null) {
                            return Optional.of(val);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new JDBCHelperException(e);
        }
        return Optional.empty();
    }

    public void execute(PreparedStatementCreator psc, RowCallbackHandler mapper) {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = psc.createPreparedStatement(conn)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        mapper.processRow(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new JDBCHelperException(e);
        }
    }

    public int update(PreparedStatementCreator psc) {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = psc.createPreparedStatement(conn)) {
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new JDBCHelperException(e);
        }
    }

    public <T> List<T> query(String query, RowMapper<T> mapper, Object... pms) {
        return query(new DefaultPreparedStatementCreator(query, pms), mapper);
    }

    public <T> Optional<T> queryObject(String query, RowMapper<T> mapper, Object... pms) {
        return queryObject(new DefaultPreparedStatementCreator(query, pms), mapper);
    }

    public void execute(String query, RowCallbackHandler mapper, Object... pms) {
        execute(new DefaultPreparedStatementCreator(query, pms), mapper);
    }

    public int update(String query, Object... pms) {
        return update(new DefaultPreparedStatementCreator(query, pms));
    }
}
