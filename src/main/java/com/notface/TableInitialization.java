package com.notface;

import com.notface.sql.DMLTool;
import com.notface.util.JdbcUtil;
import com.notface.form.MainForm;
import com.notface.form.ProgressBarThread;
import com.notface.util.info.ErrorInfo;
import com.notface.util.jtree.ConnectionTree;
import com.notface.util.PropertiesUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



/*
 * 初始化列表
 * 后续更新列表都在此类中
 * */
public class TableInitialization {
    public static String statementSQL = "";
    public static int numberOfRowsInTheCurrentTable;
    public static List<Object> primaryKeyCollection = new ArrayList<>();
    public static String[] numericField = null;
    public static int pageSize = 1000;

    private static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Class<?> fieldType = null;
            try {
                fieldType = getFieldType(resultSetMetaData.getColumnType(columnIndex + 1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                ErrorInfo.messagePopup(null,"错误:" + throwables.getMessage(),"警告",0);
            }
            return fieldType;
        }
    };
    private static JTable table = new JTable(model);
    private static boolean initializeVariables = true;
    private static TableModelListener tableModelListener;

    private static Connection conn = null;
    private static Statement statement = null;
    private static ResultSet rs = null;
    private static ResultSetMetaData resultSetMetaData = null;
    private static Integer numberOfRowsProcessed = 0;
    private static final String[] field = {"表", "视图", "函数", "查询"};

    // 初始化表格
    public static JTable init() {
        /*
         * 设置JTable的列名
         */
        // 初始化的时候需要用到
        MainForm.addTableData();

        /*TableInitialization tableInitialization = new TableInitialization();
        tableInitialization.setName("初始化线程");
        tableInitialization.start();*/
        /*Object[][] data = {{""}};

        Object[] tableHead = {""};

        model.setDataVector(data,tableHead);
        table.setModel(model);*/
        MainForm.initializeFlag = true;

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        setTableColumnCenter(table);



        try {
            Connection connection = JdbcUtil.getConnection();
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    createConnectionObject(PropertiesUtil.getProperties().getProperty("jdbc.user"), connection);
                    return null;
                }
            };
            swingWorker.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return table;

    }

    public void reloadTableContent(JTable table) {
        // 重新读取连接
        try {
            JdbcUtil.refreshConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // 初始化表内数据
        // new TableInitialization().start();
        readDatabase();

    }

    public static DefaultTableModel returnModel() {
        return model;
    }

    public synchronized static void initializeTheConnection() {
        ListSelectionModel t = table.getSelectionModel();
        if (t.isSelectionEmpty() && !initializeVariables) {
            ErrorInfo.messagePopup(new JFrame(), "表未发生任何改变", "信息", 1);
        } else {
            reloadTable();
            ErrorInfo.messagePopup(new JFrame(), "重载成功，表格以复原！", "信息", 1);
        }

    }

    public static void createConnectionObject(String connectionName, Connection conn) {
        File file = new File("./connection/" + connectionName);

        // 删除连接内的文件
        ConnectionTree.deleteTreeNode(PropertiesUtil.getProperties().getProperty("jdbc.user"));

        if (file.exists()) {
            System.out.println("连接以存在");
        } else {
            boolean mkdir = file.mkdir();
            if (mkdir) {
                System.out.println("创建成功");
            } else {
                System.out.println("创建失败");
            }
        }


        ResultSet show_databases;
        try {
            show_databases = traverseConnectionData(conn);

            show_databases.last();
            int row = show_databases.getRow();
            show_databases.beforeFirst();

            String temporaryPath = file.getPath() + "/";
            while (true) {
                if (!show_databases.next()) break;
                for (int i = 0; i < row; i++) {
                    File file1 = new File(temporaryPath + show_databases.getString("Database"));

                    if (!file1.exists()) {
                        boolean mkdir0 = file1.mkdir();

                        for (int j = 0; j < field.length; j++) {
                            File file2 = new File(file1 + "/" + field[j]);

                            if (!file2.exists()) {
                                boolean mkdir1 = file2.mkdir();
                                if (mkdir1) {
                                    System.out.println("创建成功");
                                } else {
                                    System.out.println("创建失败");
                                }
                            }

                        }

                        if (mkdir0) {
                            System.out.println("创建成功");
                        } else {
                            System.out.println("创建失败");
                        }
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                assert statement != null;
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }


    /*@Override
    public void run() {
        initializeTheConnection();
    }
*/
    // 重载按钮用 swing 的任务线程，初始化用自己定义的线程
    public static void readDatabase() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                initializeTheConnection();
                return null;
            }
        };
        swingWorker.execute();
    }

    // 获取选中数据表名
    public static List<String> getDataSheet(Connection conn) {
        Statement statement = null;
        List<String> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            ResultSet show_tables = statement.executeQuery("show tables");
            while (show_tables.next()) {
                list.add(show_tables.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                assert statement != null;

                statement.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return list;
    }

    public static ResultSet traverseConnectionData(Connection conn) throws SQLException {
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery("SHOW DATABASES");
    }

    public synchronized static void reloadTable() {
        // 先删除该表的事件，如果没有事件就不删除
        deleteEventRegistration(tableModelListener);
        MainForm.databaseSelection.setEnabled(Boolean.FALSE);
        MainForm.dataSheetSelection.setEnabled(Boolean.FALSE);
        int startPage = Integer.parseInt(MainForm.textField.getText());

        // 清空表中数据
        model.setRowCount(0);
        try {
            System.out.println(Thread.currentThread().getName());

            String sql = "SELECT * FROM ";
            conn = JdbcUtil.getConnection();
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // 获取数据表的总行数
            rs = statement.executeQuery("select count(*) as totalCount from" + "`" + MainForm.dataSheetSelection.getSelectedItem() + "`");
            rs.next();
            // 对最大页数的数学运算
            double totalCount = rs.getDouble("totalCount") / pageSize;
            numberOfRowsInTheCurrentTable = (int) Math.ceil(totalCount);
            if (numberOfRowsInTheCurrentTable == 0){
                numberOfRowsInTheCurrentTable = 1;
            }
            rs.close();
            if (statementSQL.equals("")){
                if (pageSize != -1){
                    sql = sql + "`" + MainForm.dataSheetSelection.getSelectedItem() + "` " +
                            "Limit " + (startPage - 1) * pageSize + "," + pageSize;
                }else {
                    sql = sql + "`" + MainForm.dataSheetSelection.getSelectedItem() + "` ";
                }
            }else {
                sql = statementSQL;
            }


            MainForm.sqlText.setText(sql);
            MainForm.sqlText.setCaretPosition(0);
            rs = statement.executeQuery(sql);
            resultSetMetaData = rs.getMetaData();

            // 因为初始化的东西太多了所以我需要加入延迟减小初始化线程压力
            MainForm.progressBar.setValue(0);

            // 返回字段数量
            int colCount = resultSetMetaData.getColumnCount();
            System.out.println("字段数量" + colCount);
            numericField = new String[colCount];
            for (int i = 0; i < numericField.length; i++) {
                numericField[i] = resultSetMetaData.getColumnName(i + 1);
            }

            // 将游标指向最后一条记录
            rs.last();
            // 返回游标指向的行号
            int row = rs.getRow();

            System.out.println("总行数" + row);
            Object[][] rowDate = new Object[row][colCount];
            rs.beforeFirst();

            MainForm.progressBar.setMinimum(0);
            MainForm.progressBar.setMaximum(row);
            MainForm.progressBar.setVisible(true);

            int count = 0;

            // 获取主键
            ResultSet resultSet = whetherThePrimaryKeyExistsInTheQueryTable(conn);
            resultSet.last();
            String primaryKey ="";
            if (resultSet.getRow() != 0){
                primaryKey = resultSet.getString(1);
            }
            while (rs.next()) {
                // 每处理一行将总行数赋给他
                numberOfRowsProcessed = rs.getRow();

                if (!primaryKey.equals("")){
                    primaryKeyCollection.add(rs.getString(primaryKey));
                }

                // 开启进度条任务线程
                ProgressBarThread progressBarThread =
                        new ProgressBarThread(row, numberOfRowsProcessed, MainForm.progressBar);
                progressBarThread.execute();

                for (int i = 0; i < colCount; i++) {
                    rowDate[count][i] = rs.getString(numericField[i]);
                }
                count++;
            }
            model.setDataVector(rowDate, numericField);
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JdbcUtil.close(rs,statement, conn);
            initializeVariables = false;
        }



        // 设置表内数据
        table.setModel(model);
        // 结束时将处理行数调整为0
        numberOfRowsProcessed = 0;

        // 设置延迟来减小快速操作的压力
        if (model.getColumnCount() - 1 >= 500) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MainForm.databaseSelection.setEnabled(Boolean.TRUE);
        MainForm.dataSheetSelection.setEnabled(Boolean.TRUE);

        MainForm.progressBar.setValue(0);
        MainForm.progressBar.setVisible(false);

        // 重新注册事件
        registrationIssue();

        statementSQL = "";
    }

    public static void deleteEventRegistration(TableModelListener tableModelListener){
        if (tableModelListener != null){
            table.getModel().removeTableModelListener(tableModelListener);
        }
        // 删除集合内的主键数据
        primaryKeyCollection.clear();

    }

    public static void registrationIssue(){
        TableModelListener tableModelListener = e -> {
            // 用以存放数据，防止当输入数据类型和数据库不匹配导致数据消失

            if (e.getType() == TableModelEvent.UPDATE) {
                // 判断表格是否拥有主键,获取连接
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                Object valueAt = table.getValueAt(row, column);
                // 判断该列的类型将valueAt在下方进行类型变换
                /*try {
                    getFieldType(valueAt,resultSetMetaData.getColumnType(column + 1));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }*/
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    conn = JdbcUtil.getConnection();
                    // 判断该表是否拥有主键
                    rs = whetherThePrimaryKeyExistsInTheQueryTable(conn);

                    /*String sql = "select COLUMN_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE t where t.TABLE_NAME =?";
                    ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ps.setString(1, (String) MainForm.dataSheetSelection.getSelectedItem());
                    rs = ps.executeQuery();*/

                    // 当程序没有主键的时候，会为0
                    rs.last();
                    if (rs.getRow() == 0){
                        System.out.println("没有主键");
                        // 添加一个临时主键
                        addTemporaryPrimaryKey(conn);
                        /*String sql = "ALTER TABLE `" + (String) MainForm.dataSheetSelection.getSelectedItem() + "` ADD temporaryPrimaryKey int not null auto_increment,add primary key (temporaryPrimaryKey)";
                        Statement create = conn.createStatement();
                        create.executeUpdate(sql);
                        create.close();*/

                        // 获取当前列名
                        String columnName = resultSetMetaData.getColumnName(column + 1);

                        // int columnType = resultSetMetaData.getColumnType(1);
                        // 开始更新 UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
                        StringBuilder updateStatement = new StringBuilder();
                        updateStatement.append("UPDATE ").append((String) MainForm.dataSheetSelection.getSelectedItem())
                                .append(" SET ").append(columnName).append(" = ").append("\"").append(valueAt).append("\"")
                                .append(" WHERE ").append("temporaryPrimaryKey").append(" = ").append(row + 1);

                        System.out.println(updateStatement);

                        boolean update = new DMLTool().update(conn, String.valueOf(updateStatement));
                        // 如果更新失败,则将上一轮数据替换
                        // 更新完毕后删除 临时主键
                        /*StringBuilder deleteField = new StringBuilder();
                        deleteField.append("ALTER TABLE ").append("`").append((String) MainForm.dataSheetSelection.getSelectedItem())
                                .append("`").append(" DROP COLUMN `temporaryPrimaryKey`");

                        System.out.println(deleteField);
                        Statement statement = conn.createStatement();

                        int i = statement.executeUpdate(String.valueOf(deleteField));
                        if (i > 0){
                            System.out.println("数据删除成功");
                        }else {
                            System.out.println("数据删除失败");
                        }
                        statement.close();*/
                        deleteTheTemporaryPrimaryKey(conn);
                    }

                    rs.beforeFirst();
                    while (rs.next()){
                        System.out.println("有主键");
                        String primaryKey = rs.getString(1);
                        System.out.println(primaryKey);
                        String columnName = resultSetMetaData.getColumnName(column + 1);

                        //更新数据
                        StringBuilder updateStatement = new StringBuilder();
                        updateStatement.append("UPDATE ").append((String) MainForm.dataSheetSelection.getSelectedItem())
                                .append(" SET ").append(columnName).append(" = ").append("\"").append(valueAt).append("\"")
                                .append(" WHERE ").append(primaryKey).append(" = ").append(primaryKeyCollection.get(row));

                        System.out.println(updateStatement);
                        boolean update = new DMLTool().update(conn, String.valueOf(updateStatement));

                        // 如果更新失败
                        if (!update){
                            table.setValueAt(primaryKeyCollection.get(row),row,column);
                        } else if (columnName.equals(primaryKey)){
                            primaryKeyCollection.set(row, table.getValueAt(row,column));
                        }


                    }

                } catch (SQLException throwables) {

                    throwables.printStackTrace();
                } finally {
                    JdbcUtil.close(rs, ps, conn);
                }
            }
        };
        table.getModel().addTableModelListener(tableModelListener);
        TableInitialization.tableModelListener = tableModelListener;
    }

    /**
     * 查询当前表是否存在主键
     * @param conn 当前数据库连接
     * @return 返回结果集，通过这个结果集判断(rs.getRow() != 0)当前表是否有主键
     * */
    public static ResultSet whetherThePrimaryKeyExistsInTheQueryTable(Connection conn){
        String sql = "select COLUMN_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE t where t.TABLE_NAME =?";
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, (String) MainForm.dataSheetSelection.getSelectedItem());
            resultSet = ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultSet;
    }

    /**
     * 给当前表添加一个临时主键，可以对其进行插入、删除、更新等操作
     * @param conn 当前数据库连接
     * @throws SQLException 如果发生数据库访问错误，或者在关闭的连接上调用此方法
     * */
    public static void addTemporaryPrimaryKey(Connection conn) throws SQLException{
        // 添加一个临时主键
        String sql = "ALTER TABLE `" + (String) MainForm.dataSheetSelection.getSelectedItem() + "` ADD temporaryPrimaryKey int not null auto_increment,add primary key (temporaryPrimaryKey)";
        Statement create = conn.createStatement();
        create.executeUpdate(sql);
        create.close();
    }

    /**
     * 删除当前表的临时主键
     * @param conn 当前数据库连接
     * @throws SQLException 如果发生数据库访问错误，或者在关闭的连接上调用此方法
     * */
    public static void deleteTheTemporaryPrimaryKey(Connection conn) throws SQLException{
        // 更新完毕后删除 临时主键
        StringBuilder deleteField = new StringBuilder();
        deleteField.append("ALTER TABLE ").append("`").append((String) MainForm.dataSheetSelection.getSelectedItem())
                .append("`").append(" DROP COLUMN `temporaryPrimaryKey`");

        System.out.println(deleteField);
        Statement statement = conn.createStatement();

        int i = statement.executeUpdate(String.valueOf(deleteField));
        if (i > 0){
            System.out.println("数据删除成功");
        }else {
            System.out.println("数据删除失败");
        }
        statement.close();
    }


    // 自适应大小
    public static void FitTableColumns(JTable myTable){
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();

        Enumeration columns = myTable.getColumnModel().getColumns();
        while(columns.hasMoreElements()){
            TableColumn column = (TableColumn)columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)myTable.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(myTable, column.getIdentifier()
                            , false, false, -1, col).getPreferredSize().getWidth();
            for(int row = 0; row<rowCount; row++){
                int preferredWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,
                        myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferredWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width+myTable.getIntercellSpacing().width);
        }
    }

    /**
     * 返回数据库字段类型，在jdbc中匹配的数据类型的字节码文件
     * @param type sql type
     * @return 字节码文件
     * */
    public static Class<?> getFieldType(int type){
        // 通过判断类型，判断输入类型是否符合标准，不符合标准的将其转换，不能转换的报错
        switch (type){
            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
                // System.out.println("VARBINARY");
                // System.out.println("LONGVARBINARY");
                // System.out.println("BINARY");
                return byte[].class;
            case Types.BIGINT:
                // System.out.println("BIGINT");
                return Long.class;
            case Types.TINYINT:
            case Types.BIT:
                // System.out.println("BIT");
                // System.out.println("TINYINT");
                return Byte.class;
            case Types.NULL:
                System.out.println("NULL");
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                // System.out.println("DECIMAL");
                // System.out.println("NUMERIC");
                return BigDecimal.class;
            case Types.INTEGER:
                // System.out.println("INTEGER");
                return Integer.class;
            case Types.SMALLINT:
                // System.out.println("SMALLINT");
                return Short.class;
            case Types.FLOAT:
                // System.out.println("FLOAT");
                return Double.class;
            case Types.REAL:
                // System.out.println("REAL");
                return Float.class;
            case Types.DOUBLE:
                // System.out.println("DOUBLE");
                return double.class;
            case Types.BOOLEAN:
                // System.out.println("BOOLEAN");
                return Boolean.class;
            case Types.DATALINK:
                // System.out.println("DATALINK");
                return URL.class;
            case Types.OTHER:
                // System.out.println("OTHER");
            case Types.JAVA_OBJECT:
                // System.out.println("JAVA_OBJECT");
                return Object.class;
            case Types.DISTINCT:
                // System.out.println("DISTINCT");
                break;
            case Types.STRUCT:
                // System.out.println("STRUCT");
                break;
            case Types.ARRAY:
                // System.out.println("ARRAY");
                return Array.class;
            case Types.BLOB:
                // System.out.println("BLOB");
                return Blob.class;
            case Types.CLOB:
                // System.out.println("CLOB");
                return Clob.class;
            case Types.REF:
                // System.out.println("REF");
                return Ref.class;
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE:
                // System.out.println("DATE");
                // System.out.println("TIME");
                // System.out.println("TIMESTAMP");
                // System.out.println("LONGVARCHAR");
                // System.out.println("CHAR");
                // System.out.println("VARCHAR");
            default:
                // System.out.println("默认");
                return String.class;
        }
        return null;
    }

    public static void setTableColumnCenter(JTable table){
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, r);
    }

}

