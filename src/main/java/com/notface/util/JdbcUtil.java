package com.notface.util;



import com.notface.util.info.ErrorInfo;

import javax.swing.*;
import java.sql.*;
import java.util.Properties;

public class JdbcUtil {
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    static {
        // 初始化连接
        readConfiguration();
    }

    public static void readConfiguration(){
        //读取资源文件，获取值
        try {
            //1.创建Properties集合
            Properties pro = PropertiesUtil.getProperties();
            //2.加载文件进内存
            //pro.load();
            driver = pro.getProperty("jdbc.driver");
            url = pro.getProperty("jdbc.url") + pro.getProperty("jdbc.host") +
                    ":" + pro.getProperty("jdbc.port") +
                    "/" + pro.getProperty("jdbc.database");
            user = pro.getProperty("jdbc.user");
            password = pro.getProperty("jdbc.password");

            //注册驱动
            Class.forName(driver);
        } catch (ClassNotFoundException c){
            ErrorInfo.messagePopup(new JFrame(),"驱动加载失败。","警告",0);
            c.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    public static void refreshConnection() throws SQLException {
        // 刷新连接
        readConfiguration();
    }

    public static void close(Statement ps,Connection conn){
        if(ps!=null){
            try {
                ps.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn!=null){
            try {
                conn.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs,Connection conn){
        if(rs!=null){
            try {
                rs.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn!=null){
            try {
                conn.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs, Statement ps, Connection conn){
        if(rs!=null){
            try {
                rs.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(ps!=null){
            try {
                ps.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn!=null){
            try {
                conn.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
