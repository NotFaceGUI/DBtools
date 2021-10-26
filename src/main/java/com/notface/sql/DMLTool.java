package com.notface.sql;

import com.notface.SilentMethod;
import com.notface.util.info.ErrorInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库增删改
 * */
public class DMLTool implements DML {

    /**
     * 插入数据，只能插入在最后一条
     *
     * @param sql 传入的sql语句
     * @return 返回布尔类型，来判断更新是否成功
     * */
    @Override
    public boolean insert(Connection conn,String sql) {
        return Boolean.TRUE;
    }

    /**
     * 删除记录，并刷新table更新数据
     * @param sql 传入的sql语句
     * @return 返回布尔类型，来判断更新是否成功
     * */
    @Override
    public boolean delete(Connection conn, String sql) {
        try {
            SilentMethod.SILENT_METHOD.setTextBox(sql);
            Statement statement = conn.createStatement();
            int i = statement.executeUpdate(sql);
            if (i > 0){
                System.out.println("数据删除成功");
            }else {
                System.out.println("数据删除失败");
            }
            statement.close();
        } catch (SQLException throwables) {
            ErrorInfo.messagePopup(null,"错误：" + throwables.getMessage(),"错误",0);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 更新数据
     * @param sql 传入的sql语句
     * @return 返回布尔类型，来判断更新是否成功
     * */
    @Override
    public boolean update(Connection conn,String sql) {
        try {
            SilentMethod.SILENT_METHOD.setTextBox(sql);
            Statement statement = conn.createStatement();
            int i = statement.executeUpdate(sql);
            if (i > 0){
                System.out.println("数据更新成功");
            }else {
                System.out.println("数据更新失败");
            }
            statement.close();
        } catch (SQLException throwables) {
            ErrorInfo.messagePopup(null,"错误：" + throwables.getMessage(),"错误",0);
            // throwables.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
