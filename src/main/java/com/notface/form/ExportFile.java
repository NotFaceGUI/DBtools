package com.notface.form;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;


public class ExportFile extends JFileChooser {
    public static final ExportFile EXPORT_FILE = new ExportFile();
    private static final File PATH = new File("./");// 表示当前目录

    public ExportFile() {
        this.setFileFilter(new FileNameExtensionFilter("*.sql","sql"));
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String exportFile(String defaultFileName, JFrame frame) {
        this.setCurrentDirectory(PATH);
        //设置默认文件名
        this.setSelectedFile(new File(defaultFileName));
        // 设置文件过滤
        int result = this.showSaveDialog(frame);

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
