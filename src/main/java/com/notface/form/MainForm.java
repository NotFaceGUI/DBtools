package com.notface.form;


import com.formdev.flatlaf.FlatLaf;
import com.notface.util.JdbcUtil;
import com.notface.util.PropertiesUtil;
import com.notface.TableInitialization;
import com.notface.util.icondata.IconData;
import com.notface.util.info.ErrorInfo;
import com.notface.util.jtree.ConnectionTree;
import com.notface.sql.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class MainForm extends JFrame {
    // 这些需要其他类操控的控件设置为静态
    // 设置进度条
    public static JProgressBar progressBar = new JProgressBar();
    // 目录
    public static ConnectionTree connectionTree = new ConnectionTree();
    // 右键文本框弹出菜单文本框 无焦点 无边框
    public static RightClickTheTextBox sqlText = new RightClickTheTextBox("SELECT * FROM USER");
    public static JComboBox<String> databaseSelection = new JComboBox<>();
    public static JComboBox<String> dataSheetSelection = new JComboBox<>();
    public static JTextField textField = new JTextField("1");
    public static Boolean initializeFlag = false;

    private static Boolean flag = false;
    private static int row;
    private static int column;

    public static JTable table = TableInitialization.init();

    // 设置窗体标题
    public MainForm(String title) {
        super(title);
        init();
    }

    // 初始化窗口
    private void init() {
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menu2 = new JMenu();
        centerPanel = new JPanel();
        button1 = new JButton();
        tip = new JLabel();
        toolBar = new JToolBar();
        toolButtonCopy = new JButton();
        toolButtonPaste = new JButton();
        toolButtonShear = new JButton();
        bottomPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        leftPanel = new JPanel();
        internalPanel1 = new JPanel();
        internalPanel2 = new JPanel();
        rightPanel = new JScrollPane();
        itemCopy = new JMenuItem("复制 ");
        itemPaste = new JMenuItem("粘贴");
        itemShear = new JMenuItem("剪切");
        reload = new JButton();
        newDatabase = new JMenu("新建");
        newConnection = new JMenuItem("新建连接");
        openFile = new JMenuItem("打开");
        exportSqlFile = new JMenuItem("导出 SQL");
        exportExcelFile = new JMenuItem("另存为……");
        dataSheet = new JMenuItem("表");
        view = new JMenuItem("视图");
        function = new JMenuItem("函数");
        user = new JMenuItem("用户");
        inquiry = new JMenuItem("查询");
        menu3 = new JMenu();
        about = new JMenuItem("关于");
        internalScrollPanel = new JScrollPane();


        internalInternalPanel = new JPanel();
        internalInternalPanel2 = new JPanel();
        addition = new JButton();
        delete = new JButton();
        modify = new JButton();
        check = new JButton();
        refresh = new JButton();


        refreshButton = new JButton();
        addRow = new JButton();
        deleteRow = new JButton();
        refreshTable = new JButton();

        previewPanel = new JPanel();
        previewPanel2 = new JPanel();

        previousPage = new JButton();
        nextPage = new JButton();

        leftArrow = new JButton();
        rightArrow = new JButton();

        popUpMenu = new PopUpMenu(this);
        setup = new JButton();
        bottomPanel2 = new JPanel();

        prompt = new JLabel("条记录（每页）");
        text = new JTextField();
        checkbox = new JCheckBox("限制记录", Boolean.TRUE);

        importSqlFile = new JMenuItem("导入 SQL");
        //=========this============
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 关闭界面
        setSize(960, 540);
        setLocationRelativeTo(null);// 窗口居中
        Container contentPane = getContentPane(); // 获取容器
        contentPane.setLayout(new BorderLayout()); // 设置布局


        //=========menubar1===========
        {

            //=========menu1===========
            {
                menu1.setText("文件(F)");
                menu1.setMnemonic(KeyEvent.VK_F);
                {
                    openFile.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
                    openFile.addActionListener(e -> {
                        String path = OpenFile.OPEN_FILE.openFile(this);
                        // 开启工作线程，将表格数据添加到JTable
                        SwingWorker swingWorker = new SwingWorker() {
                            @Override
                            protected Object doInBackground() throws Exception {
                                if (path != null) {
                                    Object[][] data = ExcelRead.getData(new File(path));
                                    Object[] headerData = new Object[data[0].length];
                                    Object[][] dataBody = new Object[data.length - 1][data[0].length];
                                    System.arraycopy(data[0], 0, headerData, 0, data[0].length);
                                    for (int i = 1; i < data.length; i++) {
                                        System.arraycopy(data[i], 0, dataBody[i - 1], 0, data[i].length);
                                    }

                                    // 输出表头
                                    System.out.println(Arrays.toString(headerData));

                                    // 输出数据体
                                    System.out.println("==========输出数据体==========");
                                    for (Object[] objects : dataBody) {
                                        System.out.println(Arrays.toString(objects));
                                    }
                                    // 将数据转到设计表
                                    CreateTable createTable = new CreateTable(MainForm.this);
                                }
                                return null;
                            }
                        };
                        swingWorker.execute();
                        // System.out.println(path);
                    });
                    exportSqlFile.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.CTRL_MASK));
                    exportSqlFile.addActionListener(e -> {
                        String nameDatabase = databaseSelection.getSelectedItem() + ".sql";
                        String path = ExportFile.EXPORT_FILE.exportFile(nameDatabase, MainForm.this);
                        System.out.println(path);
                        if (path != null) {
                            PackUP.packUP((String) databaseSelection.getSelectedItem(), path);
                        }
                    });

                    exportExcelFile.setAccelerator(KeyStroke.getKeyStroke('E', InputEvent.CTRL_MASK));
                }

                {
                    importSqlFile.addActionListener(e -> {
                        SwingWorker swingWorker = new SwingWorker() {
                            @Override
                            protected Object doInBackground() throws Exception {
                                String path = OpenFile.OPEN_FILE.openFile(MainForm.this);
                                if (path != null) {
                                    if (PackUP.importSql((String) databaseSelection.getSelectedItem(), path)) {
                                        JOptionPane.showMessageDialog(MainForm.this, "导入成功！", "提示", 1);
                                    } else {
                                        ErrorInfo.messagePopup(MainForm.this, "导入失败", "错误", 0);
                                    }
                                }
                                return null;
                            }
                        };
                        swingWorker.execute();

                    });
                }

                {
                    newDatabase.add(dataSheet);
                    {
                        dataSheet.addActionListener(e -> {
                            new CreateTable(this);
                        });
                    }
                    newDatabase.add(view);
                    newDatabase.add(function);
                    newDatabase.add(user);
                    newDatabase.addSeparator();
                    newDatabase.add(inquiry);
                    {
                        inquiry.addActionListener(e -> {
                            new Selection(MainForm.this, "查询", Boolean.FALSE);
                        });
                    }
                }
                menu1.add(newDatabase);
                menu1.add(newConnection);
                {
                    newConnection.addActionListener(e -> createConnectionWindow());
                }
                menu1.addSeparator();
                menu1.add(openFile);
                menu1.add(exportSqlFile);
                menu1.add(exportExcelFile);
                menu1.add(importSqlFile);

            }

            //=========menu2===========
            {
                menu2.setText("编辑(E)");
                menu2.setMnemonic(KeyEvent.VK_E);
                {
                    itemCopy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
                    itemPaste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
                    itemShear.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));

                    itemShear.addActionListener(e -> toolButtonActionListener(toolButtonShear));
                    itemCopy.addActionListener(e -> toolButtonActionListener(toolButtonCopy));
                    itemPaste.addActionListener(e -> toolButtonActionListener(toolButtonPaste));
                }
                menu2.add(itemCopy);
                menu2.add(itemPaste);
                menu2.add(itemShear);
                menu2.addSeparator();
            }


            //========menu3========
            {
                menu3.setText("帮助(H)");
                menu3.setMnemonic(KeyEvent.VK_H);
                {
                    about.addActionListener(e -> new About(this));
                    menu3.add(about);

                }
            }

            menuBar1.add(menu1);
            menuBar1.add(menu2);
            menuBar1.add(menu3);
            // 设置菜单栏所在位置
            UIManager.put("TitlePane.menuBarEmbedded", Boolean.FALSE);
            FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
        }
        setJMenuBar(menuBar1);
        //=========中间面板==========
        {
            //设置该面板与边框之间的距离
            centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            //设置该面板的布局为东西南北中
            centerPanel.setLayout(new BorderLayout());
        }
        //==========左侧面板=========
        {
            leftPanel.setLayout(new BorderLayout());
            //======tabbedPane=========
            {
                // 设置排序方向
                tabbedPane.setTabPlacement(SwingConstants.LEFT);
                {
                    {
                        internalPanel1.setLayout(new BorderLayout());
                        //==========按钮1============
                        {
                            button1.setText("弹窗");
                            button1.addActionListener(e -> button1ActionListener());
                            button1.setIcon(new ImageIcon("res/copy.png"));
                        }
                        //==========滚动条============
                        {

                            internalScrollPanel.setViewportView(connectionTree);
                            internalScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 0));
                        }

                        internalPanel1.add(internalScrollPanel, BorderLayout.CENTER);
                    }

                    {
                        {
                            internalPanel2.setLayout(new FlowLayout());
                            internalInternalPanel.setLayout(new BoxLayout(internalInternalPanel, BoxLayout.Y_AXIS));
                            internalInternalPanel2.setLayout(new BoxLayout(internalInternalPanel2, BoxLayout.Y_AXIS));

                            internalPanel2.setBorder(new EmptyBorder(10, 5, 5, 0));

                            //==========reload==========
                            {
                                reload.setText("重载");
                                reload.setToolTipText("将修改未提交的表格内容，重新加载为当前数据表中的内容");
                                reload.addActionListener(e -> reloadClickOn());
                            }

                            {
                                addition.setText("增加");
                            }

                            {
                                refresh.setText("刷新");


                                refresh.addActionListener(e -> refreshTheContentOfTheControl());


                            }

                            {
                                delete.setText("删除");
                                delete.addActionListener(e -> deleteSelectedRow());
                            }

                            {
                                modify.setText("修改");
                            }

                            {
                                check.setText("查询");

                            }

                        }
                        internalPanel2.add(internalInternalPanel);
                        internalPanel2.add(internalInternalPanel2);

                        internalInternalPanel.add(reload);
                        internalInternalPanel2.add(refresh);
                        internalInternalPanel.add(addition);
                        internalInternalPanel2.add(delete);
                        internalInternalPanel.add(modify);
                        internalInternalPanel2.add(check);

                        for (int i = 0; i < 13; i++) {
                            internalInternalPanel.add(new JButton("占位"));
                        }

                        for (int i = 0; i < 13; i++) {
                            internalInternalPanel2.add(new JButton("占位"));
                        }
                    }
                    // 添加面板
                    tabbedPane.addTab("目录", internalPanel1);
                    tabbedPane.addTab("工具", internalPanel2);
                }
            }
            leftPanel.add(tabbedPane, BorderLayout.WEST);
        }
        //======右侧面板========
        {
            //==================
            {
                // 这里写table的方法
                // 单元格焦点
                table.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        MainForm.this.enableToolbar();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        MainForm.this.disableToolbar();
                    }
                });

                table.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
                            MainForm.this.toolButtonActionListener(toolButtonCopy); // 同时按下ctrl+c
                        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
                            MainForm.this.toolButtonActionListener(toolButtonPaste); // 同时按下ctrl+v
                        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X) {
                            MainForm.this.toolButtonActionListener(toolButtonShear);
                        }

                    }
                });

                MouseInputListener mouseInputListener = new MouseInputListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        processEvent(e);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        processEvent(e);
                        // 判断是否是鼠标右键 等同于 e.isMetaDown(); ||e.isPopupTrigger()
                        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                            popUpMenu.show(table, e.getX(), e.getY());
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        processEvent(e);

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        processEvent(e);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        processEvent(e);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        processEvent(e);
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        processEvent(e);
                    }

                    private void processEvent(MouseEvent e) {
                        /*
                         * MouseEvent.BUTTON1_MASK 鼠标左键的数字
                         * getComponent() 返回触发控件这里是JTable对象
                         * getID() 返回当前调用该事件的ID 如 mousePressed 就会返回 501
                         * getWhen()
                         * getX() getY() 事件发生时的坐标
                         * e.getClickCount() 返回当前事件的鼠标点击次数
                         * */
                        // 基本功能在当调用该事件时 使用 MouseEvent.BUTTON1_MASK 这个值 在指定位置触发
                        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                            MouseEvent ne = new MouseEvent(
                                    e.getComponent(), e.getID(),
                                    e.getWhen(), MouseEvent.BUTTON1_MASK,
                                    e.getX(), e.getY(), e.getClickCount(), false);
                            table.dispatchEvent(ne);
                        }
                    }
                };

                table.addMouseListener(mouseInputListener);
                // table.addMouseMotionListener(mouseInputListener);

            }
            rightPanel.setPreferredSize(new Dimension(760, 400));
            rightPanel.setViewportView(table);
        }


        //=========底部面板==========
        {
            //设置底部面板的布局方式为流式布局
            bottomPanel.setLayout(new GridLayout(1, 2));
            bottomPanel.setBackground(Color.white);
            //=========底部tip==========
            {
                tip.setText("当前连接：" + PropertiesUtil.getProperties().getProperty("jdbc.user"));
                tip.setBackground(new Color(0xFFFFFF));
                tip.setOpaque(true);
            }
            //=========底部进度条==========
            {
                progressBar.setVisible(false);
                progressBar.setValue(0);
            }
            bottomPanel.add(tip);
            bottomPanel.add(progressBar);
        }


        //=========顶部工具栏==========
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x10A0A0A0, true)));
        {
            // 剪切
            toolButtonShear.setIcon(IconData.shearIcon);
            toolButtonShear.setToolTipText("剪切");
            toolBar.add(toolButtonShear);

            // 复制按钮
            toolButtonCopy.setIcon(IconData.copyIcon);
            toolButtonCopy.setToolTipText("复制");
            toolBar.add(toolButtonCopy);

            // 粘贴按钮
            toolButtonPaste.setIcon(IconData.pasteIcon);
            toolButtonPaste.setToolTipText("粘贴");
            toolBar.add(toolButtonPaste);

            // 设置分割
            toolBar.addSeparator();

            // 刷新按钮
            refreshButton.setIcon(IconData.refreshIcon);
            refreshButton.setToolTipText("刷新    F5");
            toolBar.add(refreshButton);

            // 加号按钮
            addRow.setIcon(IconData.addIcon);
            addRow.setToolTipText("添加");
            toolBar.add(addRow);

            // 减号按钮
            deleteRow.setIcon(IconData.reduceIcon);
            deleteRow.setToolTipText("删除");
            toolBar.add(deleteRow);

            // 为工具栏添加事件
            toolButtonCopy.addActionListener(e -> toolButtonActionListener(e.getSource()));

            toolButtonShear.addActionListener(e -> toolButtonActionListener(e.getSource()));

            toolButtonPaste.addActionListener(e -> toolButtonActionListener(e.getSource()));

            refreshButton.registerKeyboardAction(e -> refreshTheContentOfTheControl(), KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            refreshButton.addActionListener(e -> refreshTheContentOfTheControl());

            addRow.addActionListener(e -> newRow());

            deleteRow.addActionListener(e -> deleteSelectedRow());
        }

        contentPane.add(centerPanel, BorderLayout.CENTER); //往容器中添加面板，添加到中间
        contentPane.add(bottomPanel, BorderLayout.SOUTH); // 把面板放入容器底部
        contentPane.add(toolBar, BorderLayout.PAGE_START);// 设置工具栏存放的目标
        contentPane.add(leftPanel, BorderLayout.WEST);

        // 预留位置
        JLabel jLabel = new JLabel("数据库: ", SwingConstants.CENTER);
        JLabel jLabel3 = new JLabel("数据表: ", SwingConstants.CENTER);
        {
            previewPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            previewPanel.add(new JLabel(IconData.libraryIcon));
            previewPanel.add(jLabel);
            previewPanel.add(databaseSelection);
            {
                // 初始化表格数据
                this.dropDownList(PropertiesUtil.getProperties().getProperty("jdbc.database"));
            }
            previewPanel.add(new JLabel(IconData.tableIcon));
            previewPanel.add(jLabel3);
            previewPanel.add(dataSheetSelection);

            {
                refreshTable.setIcon(IconData.refreshIcon);
                // 刷新表并且,保留选择
                refreshTable.setToolTipText("刷新");
                refreshTable.setBorderPainted(false);
                refreshTable.setOpaque(true);
                refreshTable.putClientProperty("JButton.buttonType", "roundRect");
                refreshTable.setBackground(new Color(0xF5F5F5));
                refreshTable.addActionListener(e -> addTableData(Objects.requireNonNull(dataSheetSelection.getSelectedItem()).toString()));
            }
            previewPanel.add(refreshTable);
        }

        /*jLabel.setOpaque(true);
        jLabel.setBackground(new Color(0xFFB9B9));*/

        // JLabel jLabel2 = new JLabel("对进行数据换页操作",SwingConstants.CENTER);
        bottomPanel2.setLayout(new GridLayout(1, 2));
        {
            previewPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
            textField.setHorizontalAlignment(SwingConstants.CENTER);
            textField.setColumns(3);

            textField.addActionListener(this::textBox);

            previousPage.setIcon(IconData.upIcon);
            previousPage.setBorderPainted(false);
            previousPage.setOpaque(true);
            previousPage.putClientProperty("JButton.buttonType", "roundRect");
            previousPage.setBackground(new Color(0xF5F5F5));
            previousPage.setToolTipText("上一页");

            previousPage.addActionListener(e -> previousPageReduce());


            nextPage.setIcon(IconData.downIcon);
            nextPage.setBorderPainted(false);
            nextPage.setOpaque(true);
            nextPage.putClientProperty("JButton.buttonType", "roundRect");
            nextPage.setBackground(new Color(0xF5F5F5));
            nextPage.setToolTipText("下一页");

            nextPage.addActionListener(e -> nextPageAdd());

            leftArrow.setIcon(IconData.leftIcon);
            leftArrow.setBorderPainted(false);
            leftArrow.setOpaque(true);
            leftArrow.putClientProperty("JButton.buttonType", "roundRect");
            leftArrow.setBackground(new Color(0xF5F5F5));
            leftArrow.setToolTipText("第一页");

            leftArrow.addActionListener(e -> {
                SwingWorker swingWorker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        textField.setText("1");
                        TableInitialization.reloadTable();
                        return null;
                    }
                };
                swingWorker.execute();
            });


            rightArrow.setIcon(IconData.rightIcon);
            rightArrow.setBorderPainted(false);
            rightArrow.setOpaque(true);
            rightArrow.putClientProperty("JButton.buttonType", "roundRect");
            rightArrow.setBackground(new Color(0xF5F5F5));
            rightArrow.setToolTipText("最后一页");

            rightArrow.addActionListener(e -> {
                SwingWorker swingWorker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        textField.setText(String.valueOf(TableInitialization.numberOfRowsInTheCurrentTable));
                        TableInitialization.reloadTable();
                        return null;
                    }
                };
                swingWorker.execute();
            });


            {
                checkbox.setVisible(Boolean.FALSE);
                checkbox.addActionListener(e -> {
                    if (checkbox.isSelected()) {
                        TableInitialization.pageSize = Integer.parseInt(text.getText());
                    } else {
                        TableInitialization.pageSize = -1;
                    }
                    refreshTable();
                });
                text.setVisible(Boolean.FALSE);
                text.setColumns(4);
                text.setText(String.valueOf(TableInitialization.pageSize));
                text.addActionListener(e -> {
                    TableInitialization.pageSize = Integer.parseInt(text.getText());
                    refreshTable();
                });
                prompt.setVisible(Boolean.FALSE);
            }

            setup.setIcon(IconData.installIcon);
            setup.setBorderPainted(false);
            setup.setOpaque(true);
            setup.putClientProperty("JButton.buttonType", "roundRect");
            setup.setBackground(new Color(0xF5F5F5));
            setup.setToolTipText("设置");
            AtomicReference<Boolean> flag = new AtomicReference<>(Boolean.TRUE);
            setup.addActionListener(e -> {
                if (flag.get()) {
                    flag.set(Boolean.FALSE);
                    checkbox.setVisible(Boolean.TRUE);
                    text.setVisible(Boolean.TRUE);
                    prompt.setVisible(Boolean.TRUE);
                    leftArrow.setVisible(Boolean.FALSE);
                    previousPage.setVisible(Boolean.FALSE);
                    textField.setVisible(Boolean.FALSE);
                    nextPage.setVisible(Boolean.FALSE);
                    rightArrow.setVisible(Boolean.FALSE);
                } else {
                    flag.set(Boolean.TRUE);
                    checkbox.setVisible(Boolean.FALSE);
                    text.setVisible(Boolean.FALSE);
                    prompt.setVisible(Boolean.FALSE);
                    leftArrow.setVisible(Boolean.TRUE);
                    previousPage.setVisible(Boolean.TRUE);
                    textField.setVisible(Boolean.TRUE);
                    nextPage.setVisible(Boolean.TRUE);
                    rightArrow.setVisible(Boolean.TRUE);
                }
            });


            previewPanel2.add(checkbox);
            previewPanel2.add(text);
            previewPanel2.add(prompt);
            previewPanel2.add(leftArrow);
            previewPanel2.add(previousPage);
            previewPanel2.add(textField);
            previewPanel2.add(nextPage);
            previewPanel2.add(rightArrow);
            previewPanel2.add(setup);
            // previewPanel2.setBackground(new Color(0x74EE71));
            previewPanel2.setPreferredSize(new Dimension(previewPanel.getWidth(), 28));
        }
        sqlText.setEnabled(true);
        sqlText.setEditable(false);
        sqlText.setCaretPosition(0);
        bottomPanel2.add(sqlText);
        bottomPanel2.add(previewPanel2);

        // 添加预留标签
        centerPanel.add(previewPanel, BorderLayout.NORTH);
        centerPanel.add(bottomPanel2, BorderLayout.SOUTH);
        // 中间面板添加滚动条面板
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        databaseSelection.addItemListener(this::switchDatabase);
        dataSheetSelection.addItemListener(this::switchDataSheet);

        // 初始化工具状态
        this.disableToolbar();
    }

    // 启用工具栏按钮(部分)
    public void enableToolbar() {
        flag = true;
        toolButtonCopy.setEnabled(true);
        toolButtonShear.setEnabled(true);
        toolButtonPaste.setEnabled(true);

        addRow.setEnabled(true);
        deleteRow.setEnabled(true);
    }

    // 禁用工具栏按钮(部分)
    public void disableToolbar() {
        flag = false;
        toolButtonCopy.setEnabled(false);
        toolButtonShear.setEnabled(false);
        toolButtonPaste.setEnabled(false);

        addRow.setEnabled(false);
        deleteRow.setEnabled(false);
    }

    // 将内容复制到剪贴板
    public void copyToClipboard(Object valueAt) {
        // 获取系统剪切版对象
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = new StringSelection((String) valueAt);
        clipboard.setContents(trans, null);
    }

    public void newRow() {
        DefaultTableModel defaultTableModel = TableInitialization.returnModel();
        defaultTableModel.addRow(new Object[]{});
    }

    public static void deleteSelectedRow() {
        // 获取当前选中行
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();

        String sql = "";
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            conn = JdbcUtil.getConnection();
            resultSet = TableInitialization.whetherThePrimaryKeyExistsInTheQueryTable(conn);
            resultSet.last();
            // 判断当前表是否有主键 DELETE FROM `user_info`.`practice` WHERE `id` = 1
            if (resultSet.getRow() == 0) {
                TableInitialization.addTemporaryPrimaryKey(conn);
                sql = "DELETE FROM `" + MainForm.dataSheetSelection.getSelectedItem() + "` WHERE `temporaryPrimaryKey` = " + (row + 1);
                System.out.println(sql);
                new DMLTool().delete(conn, sql);
                TableInitialization.deleteTheTemporaryPrimaryKey(conn);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.removeRow(row);
                // refreshTable();
            } else {
                String primaryKey = resultSet.getString(1);
                sql = "DELETE FROM `" + MainForm.dataSheetSelection.getSelectedItem() + "` WHERE `" + primaryKey + "` = " + TableInitialization.primaryKeyCollection.get(row);
                System.out.println(sql);
                new DMLTool().delete(conn, sql);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.removeRow(row);
                // refreshTable();
            }
        } catch (SQLException throwables) {
            ErrorInfo.messagePopup(null, "错误：" + throwables.getMessage(), "错误", 0);
            throwables.printStackTrace();
        } finally {
            JdbcUtil.close(resultSet, conn);
        }
    }

    public void switchDatabase(ItemEvent e) {
        textField.setText("1");
        if (e.getStateChange() == ItemEvent.SELECTED) {
            System.out.println(e.getItem());
            PropertiesUtil.update(PropertiesUtil.getProperties(), "jdbc.database", e.getItem().toString());
            addTableData();
            if (initializeFlag) {
                refreshTable();
            }
        }
    }

    private void switchDataSheet(ItemEvent e) {
        textField.setText("1");
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (initializeFlag) {
                refreshTable();
            }
        }
    }

    // 创建新连接对话框
    private void createConnectionWindow() {
        // 设置未模态对话框
        new ConnectionDialog(this, "新建连接", true);
    }

    // 按钮点击事件
    private void button1ActionListener() {
        ErrorInfo.messagePopup(this, "当前版本：" + PropertiesUtil.VERSION, "警告", 0);
    }

    // 刷新控件内容
    private void refreshTheContentOfTheControl() {
        String property = PropertiesUtil.getProperties().getProperty("jdbc.user");
        String item = Objects.requireNonNull(databaseSelection.getSelectedItem()).toString();

        tip.setText("当前连接：" + property);


        try {
            TableInitialization.createConnectionObject(property, JdbcUtil.getConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        DefaultTreeModel defaultTreeModel = new ConnectionTree().updateTree();
        connectionTree.setModel(defaultTreeModel);
        this.dropDownList(item);
        System.out.println("刷新");
    }

    // 下拉列表添加数据方法
    public void dropDownList(String item) {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Connection conn = JdbcUtil.getConnection();
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
                ArrayList<String> list = new ArrayList<String>();
                ResultSet resultSet = TableInitialization.traverseConnectionData(conn);
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));
                }

                for (String s : list) {
                    model.addElement(s);
                }
                databaseSelection.setModel(model);
                databaseSelection.setSelectedItem(item);
                resultSet.close();
                conn.close();
                return null;
            }
        };
        swingWorker.execute();

    }

    public static void addTableData() {
        JdbcUtil.readConfiguration();
        Connection conn = null;
        try {
            conn = JdbcUtil.getConnection();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
            List<String> dataSheet = TableInitialization.getDataSheet(conn);
            for (String s : dataSheet) {
                model.addElement(s);
            }
            dataSheetSelection.setModel(model);
            dataSheetSelection.setSelectedItem(dataSheet.get(0));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                assert conn != null;
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }


    // 本类中调用该方法,保证刷新不会切换选择
    public void addTableData(String item) {
        JdbcUtil.readConfiguration();
        Connection conn = null;
        try {
            conn = JdbcUtil.getConnection();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            List<String> dataSheet = TableInitialization.getDataSheet(conn);
            for (String s : dataSheet) {
                model.addElement(s);
            }
            dataSheetSelection.setModel(model);
            dataSheetSelection.setSelectedItem(item);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                assert conn != null;
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        // 同时刷新表格内容
        if (initializeFlag) {
            refreshTable();
        }
    }

    private void previousPageReduce() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (MainForm.textField.getText().equals("1")) {
                    TableInitialization.reloadTable();
                } else {
                    MainForm.textField.setText(String.valueOf(Integer.parseInt(MainForm.textField.getText()) - 1));
                    TableInitialization.reloadTable();
                }
                return null;
            }
        };
        swingWorker.execute();

    }

    private void nextPageAdd() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (Integer.parseInt(MainForm.textField.getText()) >= TableInitialization.numberOfRowsInTheCurrentTable) {
                    TableInitialization.reloadTable();
                } else {
                    MainForm.textField.setText(String.valueOf(Integer.parseInt(MainForm.textField.getText()) + 1));
                    TableInitialization.reloadTable();
                }
                return null;
            }
        };
        swingWorker.execute();
    }

    private void textBox(ActionEvent e) {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (Integer.parseInt(textField.getText()) > TableInitialization.numberOfRowsInTheCurrentTable) {
                    textField.setText(String.valueOf(TableInitialization.numberOfRowsInTheCurrentTable));
                    TableInitialization.reloadTable();
                } else if (Integer.parseInt(textField.getText()) <= 0) {
                    textField.setText("1");
                    TableInitialization.reloadTable();
                } else if (textField.getText().equals("")) {
                    textField.setText("1");
                    TableInitialization.reloadTable();
                } else {
                    TableInitialization.reloadTable();
                }
                return null;
            }
        };
        swingWorker.execute();
    }

    private void reloadClickOn() {
        tableInitialization.reloadTableContent(table);
    }

    private void toolButtonActionListener(Object button) {
        // 行
        row = table.getSelectedRow();
        // 列
        column = table.getSelectedColumn();

        // 获取选中列内容
        Object valueAt = table.getValueAt(row, column);

        if (button == toolButtonCopy) {
            // 将选中单元格中的内容复制到系统剪贴板
            copyToClipboard(valueAt);
        } else if (button == toolButtonShear) {
            copyToClipboard(valueAt);
            table.setValueAt("", row, column);

        } else if (button == toolButtonPaste) {
            String ret = "";
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipTf = systemClipboard.getContents(null);
            if (clipTf != null) {
                // 判断剪贴板内容是否为文本
                if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                }
                table.setValueAt(ret, row, column);
            }
        }
    }

    public static void refreshTable() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                TableInitialization.reloadTable();
                return null;
            }
        };
        swingWorker.execute();
    }

    public static JTable returnTable() {
        return table;
    }


    //=====菜单=======
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenu menu2;
    private JMenu menu3;
    private JMenuItem itemCopy;
    private JMenuItem itemPaste;
    private JMenuItem itemShear;
    private JMenu newDatabase;
    private JMenuItem newConnection;
    private JMenuItem dataSheet;
    private JMenuItem view;
    private JMenuItem function;
    private JMenuItem user;
    private JMenuItem inquiry;
    private JMenuItem openFile;
    private JMenuItem exportSqlFile;
    private JMenuItem exportExcelFile;
    private JMenuItem importSqlFile;
    private JMenuItem about;

    //=====面板=======
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JPanel leftPanel;
    private JPanel internalPanel1;
    private JPanel internalPanel2;
    private JScrollPane rightPanel;
    private JScrollPane internalScrollPanel;

    private JPanel internalInternalPanel;
    private JPanel internalInternalPanel2;

    private JPanel previewPanel;
    private JPanel previewPanel2;
    private JPanel bottomPanel2;
    //=====按钮=======
    private JButton button1;
    private JButton reload;
    private JButton addition;
    private JButton delete;
    private JButton modify;
    private JButton check;
    private JButton refresh;
    private JButton refreshTable;
    //=====标签=======
    private JLabel tip;
    private JLabel prompt;
    //=====工具=======
    private JToolBar toolBar;
    private JButton toolButtonCopy;
    private JButton toolButtonPaste;
    private JButton toolButtonShear;
    private JButton refreshButton;
    private JButton addRow;
    private JButton deleteRow;
    private JButton previousPage;
    private JButton nextPage;
    private JButton leftArrow;
    private JButton rightArrow;
    private JButton setup;
    private PopUpMenu popUpMenu;
    //===============
    private JTabbedPane tabbedPane;
    private JTextField text;
    private JCheckBox checkbox;
    //===============
    private TableInitialization tableInitialization = new TableInitialization();
}
