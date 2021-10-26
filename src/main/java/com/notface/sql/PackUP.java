package com.notface.sql;

import com.notface.util.PropertiesUtil;
import com.notface.SilentMethod;

import java.io.IOException;
import java.util.Properties;

public class PackUP {
    public static void packUP(String dbname, String url) {
        Properties pro = PropertiesUtil.getProperties();
        String host = pro.getProperty("jdbc.host");
        String user = pro.getProperty("jdbc.user");
        String password = pro.getProperty("jdbc.password");

        try {//用cmd执行mysqldump代码
//          mysqldump –user=root –password=mypassword -h localhost databasename table_name_to_dump table_name_to_dump_2 > dump_only_two_tables_file.sql
            //用cmd来执行mysqldump语句
            StringBuilder commandStr = new StringBuilder();
            /*if (tbname == null){
                // mysqldump 数据库名 -uroot -p　>　xxx.sql　
                commandStr.append("mysqldump ").append(dbname).append(" -uroot").append(" -p ").append(password).append(" > ").append(url);
            }else {
                commandStr.append("mysqldump -u").append(user).append(" -p").append(password).append(" -h").append(host).append(" ").append(dbname).append(" ").append(tbname).append(" > ").append(url);
            }*/
            System.out.println(url);
            commandStr.append("mysqldump ").append(dbname).append(" -uroot").append(" -p").append(password).append(" > ").append(url);
            SilentMethod.SILENT_METHOD.setTextBox(String.valueOf(commandStr));
            // /c是执行命令后关闭cmd /k则是执行后保留cmd
            String[] command = {"cmd", "/c", commandStr.toString()};
            Process process = Runtime.getRuntime().exec(command);
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 导入sql */
    public static Boolean importSql(String dbname, String url) {
        Properties pro = PropertiesUtil.getProperties();
        String host = pro.getProperty("jdbc.host");
        String user = pro.getProperty("jdbc.user");
        String password = pro.getProperty("jdbc.password");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mysql").append(" -h").append(host);
        stringBuilder.append(" -u").append(user).append(" -p").append(password);
        stringBuilder.append(" ").append(dbname);
        stringBuilder.append(" < ").append(url);

        SilentMethod.SILENT_METHOD.setTextBox(String.valueOf(stringBuilder));

        try {
            Process process = Runtime.getRuntime().exec("cmd /c " + stringBuilder.toString());//必须要有“cmd /c ”
            if (process.waitFor() == 0) {// 0 表示线程正常终止。
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
