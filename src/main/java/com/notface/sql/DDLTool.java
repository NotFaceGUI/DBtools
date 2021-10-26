package com.notface.sql;

import com.notface.SilentMethod;
import com.notface.util.info.ErrorInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DDLTool implements DDL{


    /**
     * 创建表、库等
     * */
    @Override
    public Boolean create(Connection conn, String sql) {
        return null;
    }

    @Override
    public Boolean drop(Connection conn,String sql) {
        return null;
    }

    @Override
    public Boolean alterTable(Connection conn,int sqlParameter,String table,String column) {
        StringBuilder sql = new StringBuilder("ALTER TABLE `");
        try {
            if (sqlParameter == 0){
                sql.append(table).append("` DROP COLUMN `").append(column).append("`");
            }else if (sqlParameter == 1){
                // JOptionPane.showMessageDialog(null, TypePanel.TYPE_PANEL,"设置",JOptionPane.QUESTION_MESSAGE);
                ErrorInfo.messagePopup(null,"尚未实现","提升",1);
                sql.append(table).append("` ADD `").append(column).append("` ");
            }
            SilentMethod.SILENT_METHOD.setTextBox(String.valueOf(sql));
            Statement statement = conn.createStatement();
            statement.executeUpdate(String.valueOf(sql));
            statement.close();
        } catch (SQLException throwables) {
            ErrorInfo.messagePopup(null,"错误：" + throwables.getMessage(),"错误",0);
            return Boolean.FALSE;
            //throwables.printStackTrace();
        }
        return Boolean.TRUE;
    }
}
