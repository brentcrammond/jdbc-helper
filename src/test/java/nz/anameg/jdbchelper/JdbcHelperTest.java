/*
 * Copyright 2020 Anameg Consulting Ltd
 */
package nz.anameg.jdbchelper;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the JdbcHelper class, uses a temp H2 database.
 */
class JdbcHelperTest {
    JdbcDataSource ds;

    @BeforeEach
    void setUp() {
        ds = new JdbcDataSource();
        ds.setUrl("jdbc:h2:./test_db");
        new JdbcHelper(ds).update("CREATE TABLE IF NOT EXISTS names (id BIGINT NOT NULL PRIMARY KEY, name VARCHAR(20), seq INT)");
        new JdbcHelper(ds).update("DELETE FROM names");
        new JdbcHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (1, 'Name 1', 100)");
        new JdbcHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (2, 'Name 2', 200)");
        new JdbcHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (3, 'Name 3', 300)");
        new JdbcHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (4, 'Name 4', 400)");
        new JdbcHelper(ds).update("INSERT INTO names (id, name, seq) VALUES (5, 'Name 5', 500)");
    }

    @Test
    void query() {
        var lst = new JdbcHelper(ds).query("SELECT * FROM names ORDER by id",
                (rs, idx) -> Name.builder().id(rs.getLong("id")).name(rs.getString("name")).seq(rs.getInt("seq")).build());
        for (int i = 0; i < 5; i++) {
            var j = i + 1;
            assertEquals(j, lst.get(i).getId());
            assertEquals("Name " + j, lst.get(i).getName());
            assertEquals(j * 100, lst.get(i).getSeq());
        }
    }

    @Test
    void queryObject() {
        var opt = new JdbcHelper(ds).queryObject("SELECT * FROM names WHERE id = 3",
                (rs, idx) -> Name.builder().id(rs.getLong("id")).name(rs.getString("name")).seq(rs.getInt("seq")).build());
        assertTrue(opt.isPresent());
        assertEquals(3, opt.get().getId());
        assertEquals("Name 3", opt.get().getName());
        assertEquals(300, opt.get().getSeq());
    }

    @Test
    void execute() {
        var done = new AtomicBoolean(false);
        new JdbcHelper(ds).execute("SELECT * FROM names WHERE id = 4", (rs) -> {
            assertEquals(4, rs.getLong("id"));
            assertEquals("Name 4", rs.getString("name"));
            assertEquals(400, rs.getInt("seq"));
            done.set(true);
        });
        Assertions.assertTrue(done.get());
    }

    @Test
    void update() {
        var done = new AtomicBoolean(false);
        new JdbcHelper(ds).update("UPDATE names SET seq = 1000 WHERE id = 5");
        new JdbcHelper(ds).queryObject("SELECT * FROM names WHERE id = 5",
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
