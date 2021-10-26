package com.notface.form;


import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;


public class OpenFile extends JFileChooser {
    public static final OpenFile OPEN_FILE = new OpenFile();
    private static final File PATH = new File("./");// 表示当前目录

    public OpenFile() {
        //加入想要过滤的文件类型(这里添加的是表格文件.xlsx)
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xls,*.xlsx", "xls","xlsx");
        this.setFileFilter(filter);
        this.setFileFilter(new FileNameExtensionFilter("*.sql","sql"));
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String openFile(JFrame frame) {
        this.setCurrentDirectory(PATH);
        //添加完毕后进行筛选的操作
        int result = this.showDialog(frame, "选择");
        switch (result){
            case JFileChooser.APPROVE_OPTION:
                // 判断点击的是那个按钮
                return this.getSelectedFile().getPath();
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                return null;
        }
    }
}




