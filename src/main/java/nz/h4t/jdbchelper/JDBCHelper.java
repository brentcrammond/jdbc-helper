/*
 * Copyright 2020 Anameg Consulting Ltd
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
