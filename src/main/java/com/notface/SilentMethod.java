package com.notface;

import com.notface.form.MainForm;

/**
 * 全局静态变量，如果操作很多的可以直接放入本类中直接操作
 * */
public class SilentMethod {
    public static final SilentMethod SILENT_METHOD = new SilentMethod();

    public void setTextBox(String sql){
        MainForm.sqlText.setText(sql);
        MainForm.sqlText.setCaretPosition(0);
    }
}
