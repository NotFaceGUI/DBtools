package com.notface;

import com.notface.form.ConnectionDialog;
import com.notface.form.MainForm;
import com.formdev.flatlaf.FlatLightLaf;
import com.notface.util.PropertiesUtil;

import javax.swing.*;
import java.util.Properties;

/**
 * 程序入口
 */
public class App {
    public static void main(String[] args) {
        //=========设置style===========
        // FlatArcIJTheme.setup();
        System.out.println("程序开始！");
        FlatLightLaf.setup();
        UIManager.put( "TextComponent.arc",5);

        SwingUtilities.invokeLater(() -> {
            init();

            if (ConnectionDialog.flag){
                MainForm db_tools = new MainForm("DB Tools");
                db_tools.requestFocusInWindow();
                db_tools.setVisible(true);
                db_tools.pack(); //窗口自适应
            }else{
                System.exit(0);
            }
        });

    }

    private static void init(){
        Properties properties = PropertiesUtil.getProperties();
        String url = properties.getProperty("jdbc.url");
        String database = properties.getProperty("jdbc.database");
        String driver = properties.getProperty("jdbc.driver");
        String user = properties.getProperty("jdbc.user");
        String port = properties.getProperty("jdbc.port");
        String password = properties.getProperty("jdbc.password");
        String host = properties.getProperty("jdbc.host");

        if (url.equals("") || database.equals("") || driver.equals("") || user.equals("") || port.equals("") || password.equals("") || host.equals("")){
            new ConnectionDialog(new JFrame(),"新建连接",true);
        }else{
            ConnectionDialog.flag=true;
        }
    }
}
