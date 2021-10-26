package com.notface.sql;

import java.sql.Connection;

public interface DDL {
    Boolean create(Connection conn,String sql);

    Boolean drop(Connection conn,String sql);

    Boolean alterTable(Connection conn, int sqlParameter, String table, String column);
}
