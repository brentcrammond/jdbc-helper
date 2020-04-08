/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.anameg.jdbchelper.support;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * This class wraps a Single Connection in a DataSource.  It will suppress the closing of the connections,
 * so the user will need to close the connection manually.
 */
public class SingleConnectionDataSource implements DataSource {

    private final Connection delegate;

    public SingleConnectionDataSource(Connection delegate) {
        this.delegate = delegate;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // Cannot use Logger.getGlobal() in JDK 6 yet
        return Logger.getAnonymousLogger().getParent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        } else {
            throw new SQLException("DataSource does not implement " + iface);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new DelegateConnection(delegate) {
            @Override
            public void close() throws SQLException {
                // Don't close the connection, this will be left to the User, so multiple statements can be processed.
            }
        };
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException("SingleConnectionDataSource cannot create new connections");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException("getLogWriter");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLFeatureNotSupportedException("setLogWriter");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("setLoginTimeout");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getLoginTimeout");
    }
}
