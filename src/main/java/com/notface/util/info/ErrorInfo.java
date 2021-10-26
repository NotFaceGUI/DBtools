package com.notface.util.info;

import javax.swing.*;
import java.awt.*;

public class ErrorInfo {
    // message对话框
    public static void messagePopup(Component parentComponent, String msg, String title, int type){
        JOptionPane.showMessageDialog(parentComponent,msg,title,type);
    }
}
