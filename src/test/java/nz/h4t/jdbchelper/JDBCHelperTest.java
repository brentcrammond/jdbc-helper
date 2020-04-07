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

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the JDBCHelper class, uses a temp H2 database.
 */
class JDBCHelperTest {
    JdbcDataSource ds;

    @BeforeEach
    void setUp() throws SQLException {
        ds = new JdbcDataSource();
        ds.setUrl("jdbc:h2:./test_db");
        new JDBCHelper(ds).update("CREATE TABLE IF NOT EXISTS names (id BIGINT NOT NULL PRIMARY KEY, name VARCHAR(20), seq INT)");
        new JDBCHelper(ds).update("DELETE FROM names");
        new JDBCHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (1, 'Name 1', 100)");
        new JDBCHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (2, 'Name 2', 200)");
        new JDBCHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (3, 'Name 3', 300)");
        new JDBCHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (4, 'Name 4', 400)");
        new JDBCHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (5, 'Name 5', 500)");
    }

    @Test
    void query() throws SQLException {
        var lst = new JDBCHelper(ds).query("SELECT * FROM names ORDER by id",
                (rs, idx) -> Name.builder().id(rs.getLong("id")).name(rs.getString("name")).seq(rs.getInt("seq")).build());
        for (int i = 0; i < 5; i++) {
            var j = i + 1;
            assertEquals(j, lst.get(i).getId());
            assertEquals("Name " + j, lst.get(i).getName());
            assertEquals(j * 100, lst.get(i).getSeq());
        }
    }

    @Test
    void queryObject() throws SQLException {
        var opt = new JDBCHelper(ds).queryObject("SELECT * FROM names WHERE id = 3",
                (rs, idx) -> Name.builder().id(rs.getLong("id")).name(rs.getString("name")).seq(rs.getInt("seq")).build());
        assertTrue(opt.isPresent());
        assertEquals(3, opt.get().getId());
        assertEquals("Name 3", opt.get().getName());
        assertEquals(300, opt.get().getSeq());
    }

    @Test
    void execute() throws SQLException {
        var done = new AtomicBoolean(false);
        new JDBCHelper(ds).execute("SELECT * FROM names WHERE id = 4", (rs) -> {
            assertEquals(4, rs.getLong("id"));
            assertEquals("Name 4", rs.getString("name"));
            assertEquals(400, rs.getInt("seq"));
            done.set(true);
        });
        Assertions.assertTrue(done.get());
    }

    @Test
    void update() throws SQLException {
        var done = new AtomicBoolean(false);
        new JDBCHelper(ds).update("UPDATE names SET seq = 1000 WHERE id = 5");
        new JDBCHelper(ds).queryObject("SELECT * FROM names WHERE id = 5",
                (rs, idx) -> Name.builder().id(rs.getLong("id")).name(rs.getString("name")).seq(rs.getInt("seq")).build())
                .ifPresent(n -> {
                    assertEquals(5, n.getId());
                    assertEquals("Name 5", n.getName());
                    assertEquals(1000, n.getSeq());
                    done.set(true);
                });
        Assertions.assertTrue(done.get());
    }
}
