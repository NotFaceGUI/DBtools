package com.notface.sql;

import java.sql.Connection;

public interface DML {

    boolean insert(Connection conn,String sql);

    boolean delete(Connection conn,String sql);

    boolean update(Connection conn,String sql);

}
