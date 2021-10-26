package com.notface.form;

import com.notface.util.icondata.IconData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CreateTable extends JFrame {
    public static String[] typeOfData = {"TINYINT","SMALLINT","MEDIUMINT","INT","BIGINT","FLOAT","DOUBLE","DECIMAL","DATE","TIME","YEAR",
            "DATETIME","TIMESTAMP","CHAR","VARCHAR","TINYBLOB","TINYTEXT","BLOB","TEXT","MEDIUMBLOB","MEDIUMTEXT",
            "LONGBLOB","LONGTEXT"};
    private static final String[] HEADER = {"名","类型","长度","小数点","不是null","虚拟","键","注释"};

    public DefaultTableModel model = new DefaultTableModel(){
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 4 || columnIndex == 5){
                return Boolean.class;
            }else if (columnIndex == 1){
                return JComboBox.class;
            }else if (columnIndex == 2){
                return Integer.class;
            }
            return String.class;
        }
    };
    public JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    public JTabbedPane tabbedPane = new JTabbedPane();
    public JScrollPane topsPanel = new JScrollPane();
    public JPanel bottomPanel = new JPanel();
    public JPanel topPanel = new JPanel();
    public JToolBar toolbar = new JToolBar();
    public JButton save = new JButton();
    public JButton add = new JButton();
    public JButton insert = new JButton();
    public JButton delete = new JButton();
    public JButton primaryKey = new JButton();
    public JTable table = new JTable();

    public CreateTable(JFrame jFrame){
        //==========this
        super("设计表");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setVisible(true);

        //==========contentPane
        {
            //==========toolbar
            {
                toolbar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0xB5A8A8A8, true)));
                toolbar.setBackground(Color.WHITE);
                {
                    save.setText("保存");
                    save.setIcon(IconData.saveIcon);

                    add.setText("添加字段");
                    add.setIcon(IconData.add_toIcon);

                    insert.setText("插入字段");
                    insert.setIcon(IconData.insertIcon);

                    delete.setText("删除字段");
                    delete.setIcon(IconData.deleteIcon);

                    primaryKey.setText("主键");
                    primaryKey.setIcon(IconData.primary_keyIcon);
                }
                toolbar.add(save);
                toolbar.addSeparator();
                toolbar.add(add);
                toolbar.add(insert);
                toolbar.add(delete);
                toolbar.addSeparator();
                toolbar.add(primaryKey);

                //==========splitPane
                {
                    {
                        topsPanel.setPreferredSize(new Dimension(800,450));
                        topsPanel.setBorder(new EmptyBorder(2,5,2,5));
                        topsPanel.setBackground(new Color(0x129BA7));
                        {
                            Object[][] data = {
                                    {"", "","","",Boolean.FALSE,Boolean.FALSE,"",""},
                            };
                            model.setDataVector(data,HEADER);
                            table.setModel(model);
                            table.getColumnModel().getColumn(1) .setCellEditor(new DefaultCellEditor(returnTypeCheckbox()));

                            // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            table.setRowSelectionAllowed(true);
                            table.setColumnSelectionAllowed(true);
                            table.setShowHorizontalLines(Boolean.TRUE);
                            table.setShowVerticalLines(Boolean.TRUE);

                        }
                        topsPanel.setViewportView(table);

                        bottomPanel.setBackground(new Color(0xF85656));
                        topPanel.add(tabbedPane);
                        tabbedPane.addTab("字段",topsPanel);
                        tabbedPane.addTab("索引",new JPanel());
                        tabbedPane.addTab("外键",new JPanel());
                        tabbedPane.addTab("触发器",new JPanel());
                        tabbedPane.addTab("选项",new JPanel());
                    }

                    splitPane.setTopComponent(topPanel);
                    splitPane.setBottomComponent(bottomPanel);
                }
            }
        }
        contentPane.add(splitPane,BorderLayout.CENTER);
        contentPane.add(toolbar,BorderLayout.NORTH);
        this.pack();
        this.setLocationRelativeTo(jFrame);

        add.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(new Object[]{});
        });

        delete.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(table.getSelectedRow());
        });
    }

    public static JComboBox<String> returnTypeCheckbox(){
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for (String typeOfDatum : typeOfData) {
            comboBoxModel.addElement(typeOfDatum);
        }
        JComboBox<String> stringJComboBox = new JComboBox<>(comboBoxModel);
        stringJComboBox.setEditable(Boolean.TRUE);
        JTextField editorComponent = (JTextField)stringJComboBox.getEditor().getEditorComponent();
        editorComponent.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                stringJComboBox.setPopupVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                stringJComboBox.setPopupVisible(false);
            }
        });
        return stringJComboBox;
    }


}
