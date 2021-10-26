package com.notface.util;

import com.notface.util.info.ErrorInfo;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/*
* 读取properties文件下的配置
* */
public class PropertiesUtil {
    // 获取app信息的配置文件
    private static final ResourceBundle application = ResourceBundle.getBundle("config.application");

    public static final String VERSION = application.getString("app.version");

    private static final String PATH = "./config/jdbc.properties";
    private static final File FILE = new File(PATH);

    public static Properties getProperties(){
        Properties pro = new Properties();
        try {
            pro.load(new FileReader(PATH));
        } catch (IOException e) {
            e.printStackTrace();
            ErrorInfo.messagePopup(new JFrame(),"找不到配置文件","错误",0);
        }
        return pro;
    }

    // 修改Properties文件的方法
    public static void update(Properties p,String key, String value) {
        p.setProperty(key, value);
        FileWriter oFile = null;
        try {
            oFile = new FileWriter("./config/jdbc.properties");
            //将Properties中的属性列表（键和元素对）写入输出流
            p.store(oFile, "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert oFile != null;
                oFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
