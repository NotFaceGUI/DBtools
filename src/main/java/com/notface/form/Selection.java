package com.notface.form;

import com.notface.TableInitialization;
import com.notface.util.JdbcUtil;
import com.notface.util.icondata.IconData;
import com.notface.util.info.ErrorInfo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Selection extends JDialog {
    int wordnum=0;
    public Selection(JFrame jframe, String title, Boolean model) {
        super(jframe,title, model);
        initializationSelection(jframe);
    }

    private void initializationSelection(JFrame jframe){


        this.setSize(900,600);
        this.setLocationRelativeTo(jframe);// 窗口居中
        this.setLayout(null);

        JPanel jptop=new JPanel(new FlowLayout(FlowLayout.LEFT));//顶部panel
        jptop.setBounds(0,0,900,40);
        jptop.setBackground(new Color(226,230,231));
        jptop.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0xB5A8A8A8, true)));

        JLabel p1=new JLabel();
        p1.setIcon(IconData.libraryIcon);
        p1.setSize(16,16);

        JLabel label1=new JLabel("数据库：");
        label1.setSize(70,10);
        JLabel label2=new JLabel("数据表：");
        label2.setSize(70,10);

        JLabel p2=new JLabel();
        p2.setIcon(IconData.tableIcon);
        p2.setSize(16,16);

        JComboBox<String> dbcb=new JComboBox<>();//数据库数据表combobox
        dbcb.setSize(300,10);
        dbcb.setModel(MainForm.databaseSelection.getModel());

        JComboBox<String> tbcb=new JComboBox<>();
        tbcb.setSize(300,10);
        tbcb.setModel(MainForm.dataSheetSelection.getModel());

        JButton jb=new JButton();
        jb.setText("执行");
        jb.setIcon(IconData.excuteIcon);
        jb.setToolTipText("运行");
        jb.setBorderPainted(false);
        jb.setOpaque(true);
        jb.putClientProperty("JButton.buttonType", "roundRect");
        jb.setBackground(new Color(0xF5F5F5));

        JPanel jmain=new JPanel(new FlowLayout());//中间的panel
        jmain.setBounds(0,43,890,470);
        jmain.setBackground(new Color(243,243,243));

        JTextArea jtf=new JTextArea();
        jtf.setPreferredSize(new Dimension(890,461));
        jtf.setLineWrap(true);
        jtf.setWrapStyleWord(true);
        jtf.setRows(100);
        jtf.setFont(new Font("微软雅黑",0,18));
        JScrollPane tsc = new JScrollPane();
        tsc.setViewportView(jtf);
        tsc.setPreferredSize(new Dimension(890,460));
        tsc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jtf.setAutoscrolls(true);

        JPanel jbottom=new JPanel(null);//底部的panel
        jbottom.setBounds(0,510,890,40);
        jbottom.setBackground(new Color(233,233,233));
        jbottom.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(0xB5A8A8A8, true)));

        JLabel word=new JLabel();
        word.setText("共编辑： "+wordnum+" 个字，"+jtf.getLineCount()+"行");
        word.setBounds(670,12,190,23);
        JLabel p3=new JLabel();
        p3.setIcon(IconData.wordIcon);
        p3.setBounds(650,14,17,17);


        jtf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                wordnum++;
                word.setText("共编辑： "+wordnum+" 个字，"+jtf.getLineCount()+"行");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(wordnum>0){
                    wordnum--;
                    word.setText("共编辑： "+wordnum+" 个字，"+jtf.getLineCount()+"行");
                }else{
                    wordnum=0;
                    word.setText("共编辑： "+wordnum+" 个字，"+jtf.getLineCount()+"行");
                }

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        jb.addActionListener(e->{//执行按钮的事件
            String sql;
            if(jtf.getSelectedText()!=null){
                sql=jtf.getSelectedText();
            }else{
                sql=jtf.getText();
            }
            try {
                Connection conn = JdbcUtil.getConnection();
                Statement statement = conn.createStatement();
                System.out.println(sql);
                boolean execute = statement.execute(sql);
                if (execute){
                    TableInitialization.statementSQL = sql;
                    MainForm.refreshTable();
                    JOptionPane.showMessageDialog(this,"执行成功！","提示", JOptionPane.INFORMATION_MESSAGE);
                }else {
                    JOptionPane.showMessageDialog(this,"执行失败！","提示", JOptionPane.ERROR_MESSAGE);
                }
                statement.close();
                conn.close();
            } catch (SQLException throwables) {
                ErrorInfo.messagePopup(this, throwables.getMessage(),"错误",0);
                // throwables.printStackTrace();
            }
        });



        jmain.add(tsc);
        jptop.add(p1);
        jptop.add(label1);
        jptop.add(dbcb);
        jptop.add(p2);
        jptop.add(label2);
        jptop.add(tbcb);
        jptop.add(jb);
        jbottom.add(word);
        jbottom.add(p3);
        this.add(jptop);
        this.add(jmain);
        this.add(jbottom);
        this.setResizable(false);
        this.setVisible(true);
    }
}
