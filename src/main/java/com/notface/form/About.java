package com.notface.form;

import com.notface.PropertiesUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


/*
* 关于界面
* */
public class About extends JDialog {
    public About(JFrame jFrame) {
        super(jFrame);
        this.setSize(256, 256);
        // 设置弹窗居中于主窗口
        this.setLocationRelativeTo(jFrame);
        this.setUndecorated(Boolean.TRUE);
        this.setOpacity(0.95F);

        // AWTUtilities.setWindowShape(this, new RoundRectangle2D.Double(0.0D, 0.0D, this.getWidth(), this.getHeight(), 30.0D, 30.0D));

        // 设置边框显示
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel();
        contentPane.add(jPanel, BorderLayout.CENTER);
        // contentPane.setBackground(new Color(0x59ADA3));


        StringBuilder str = new StringBuilder("<HTML><h3 style=\"text-align: center\">DB Tools</h3><p><b>当前内部版本：</b><br>");
        str.append(PropertiesUtil.VERSION)
                .append("</p><p><b>null:</b><br>null</p><p><b>null:</b><br><a href=\"\">null</a><br><a href=\"\">null</a><br><a href=\"\">null</a></p>")
                        .append("<br><br><br><p>CopyRight@copy2021</p></HTML>");
        JLabel jLabel = new JLabel(String.valueOf(str), SwingUtilities.CENTER);
        jPanel.add(jLabel);


        this.setVisible(true);

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                About.this.dispose();
            }
        });
    }
}
