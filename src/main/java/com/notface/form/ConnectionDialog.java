package com.notface.form;

import com.notface.util.PropertiesUtil;
import com.notface.util.JdbcUtil;
import com.notface.util.icondata.IconData;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionDialog extends JDialog {
    public static Boolean flag = Boolean.FALSE;
    public ConnectionDialog(JFrame jframe, String title, Boolean model) {
        super(jframe, title, model);
        initializationDialog(jframe);
    }

    private void initializationDialog(JFrame jframe) {
        this.setSize(530,450);
        this.setLocationRelativeTo(jframe);// 窗口居中
        this.setLayout(null);
        JPanel jp = new JPanel();
        jp.setBounds(0, -20, 530, 500);
        jp.setLayout(null);
        JLabel jl[] = new JLabel[4];
        JTextField jt[] = new JTextField[4];
        jl[0] = new JLabel(" 连接主机名：");
        jl[1] = new JLabel(" 连接端口名：");
        jl[2] = new JLabel(" 连接用户名：");
        jl[3] = new JLabel(" 连接密码：");
        int s = 130;//控制上下距离
        for (int i = 0; i < 4; i++) {
            jl[i].setBounds(87, s, 170, 60);
            jt[i] = new JTextField();
            jt[i].setBounds(235, s + 15, 230, 30);
            s += 55;
            jp.add(jl[i]);
            jp.add(jt[i]);
        }
        jl[3].setBounds(101,s-55,170,60);
        jp.remove(jt[3]);
        JPasswordField ps = new JPasswordField();//密码框
        ps.setBounds(235, s -40, 230, 30);
        jp.add(ps);


        JButton jb2 = new JButton("重置");//重置输入框里的内容
        jb2.setBounds(210, 380, 100, 40);
        jb2.addActionListener(event -> {
            for (int i = 0; i < 4; i++)
                jt[i].setText("");
            ps.setText("");
        });


        JButton jb3 = new JButton("确定");//确定内容并且判断修改jdbc文件
        jb3.setBounds(410, 380, 100, 40);
        jb3.addActionListener(event -> {
            this.dispose();
            JdbcUtil.readConfiguration();
            flag=Boolean.TRUE;
        });

        JButton jb1=new JButton("测试连接");
        jb1.setBounds(17,380,100,40);
        jb1.addActionListener(event->{
            Connection conn =null;
            try {
                Properties properties = PropertiesUtil.getProperties();
                String url= "jdbc:mysql://"+jt[0].getText()+":"+jt[1].getText()+"/"+properties.getProperty("jdbc.database");
                String user=jt[2].getText();
                String pw=String.valueOf(ps.getPassword());
                System.out.println(pw);
                conn = DriverManager.getConnection(url,user,pw);
                if(conn != null) {
                    JOptionPane.showMessageDialog(null,"数据库连接成功","提示",1);
                    jb3.setEnabled(true);

                    PropertiesUtil.update(properties, "jdbc.host", jt[0].getText());
                    PropertiesUtil.update(properties, "jdbc.port", jt[1].getText());
                    PropertiesUtil.update(properties, "jdbc.user", user);
                    PropertiesUtil.update(properties, "jdbc.password", pw);
                }
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null, "数据库连接失败:" + throwables.getMessage(), "警告", 0);
                for (int i = 0; i < 4; i++) {
                    jt[i].setText("");
                }
                ps.setText("");
                jb3.setEnabled(false);
            }finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        ActionListener[] actionListeners = jb1.getActionListeners();
        jb1.registerKeyboardAction(actionListeners[0], KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        jb3.setEnabled(false);

        jp.add(jb1);
        jp.add(jb2);
        jp.add(jb3);

        JLabel label1 = new JLabel();//顶部图片
        label1.setIcon(IconData.connectIcon);
        label1.setBounds(0,30,530,450);

        JLabel label2=new JLabel();//文本旁图片
        label2.setIcon(IconData.hostIcon);
        label2.setBounds(37,-65,530,450);

        JLabel label3=new JLabel();
        label3.setIcon(IconData.portIcon);
        label3.setBounds(42,-15,530,450);

        JLabel label4=new JLabel();
        label4.setIcon(IconData.userIcon);
        label4.setBounds(40,42,530,450);

        JLabel label5=new JLabel();
        label5.setIcon(IconData.psIcon);
        label5.setBounds(33,92,530,450);

        JLabel label[]=new JLabel[4];
        int sl=134;
        for (int i = 0; i < 4; i++) {
            label[i]=new JLabel();
            label[i].setIcon(IconData.poIcon);
            label[i].setBounds(192,sl+10,51,31);
            sl+=55;
            jp.add(label[i]);
        }


        jp.add(label1);
        jp.add(label2);
        jp.add(label3);
        jp.add(label4);
        jp.add(label5);

        this.add(jp);
        this.setResizable(false);

        this.setVisible(true);
    }
}
