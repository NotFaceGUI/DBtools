package com.notface.form;

import com.notface.TableInitialization;
import com.notface.sql.CreateTable;
import com.notface.sql.DDLTool;
import com.notface.sql.JdbcUtil;
import com.notface.sql.SQLParameter;

import javax.swing.*;
import java.sql.SQLException;

/*
* 右键菜单
*
* */
public class PopUpMenu extends JPopupMenu implements AbstractPopUpMenu {
    private Integer row;
    private Integer column;
    public PopUpMenu(JFrame jFrame) {
        JMenuItem setToBlankString = createMenuItem("设置为空白字符串");
        JMenuItem setToNULL = createMenuItem("设置为NULL");
        JMenuItem delete_record = createMenuItem("删除 记录");
        JMenuItem delete_field = createMenuItem("删除 字段");
        // JMenuItem add_field = createMenuItem("添加 字段");
        JMenuItem designTable = createMenuItem("设计表");
        JMenuItem dumpSQL = createMenu("转储 SQL 文件");
        JMenuItem copy = createMenuItem("复制");
        JMenuItem insert_statement = createMenuItem("Insert 语句");
        JMenuItem update_statement = createMenuItem("Update 语句");
        JMenuItem stick = createMenuItem("粘贴");
        JMenuItem cut = createMenuItem("剪切");
        JMenuItem display = createMenuItem("显示");
        JMenuItem refresh = createMenuItem("刷新");

        setToBlankString.addActionListener(e -> {
            JTable jTable = getSelected();
            jTable.setValueAt("",row,column);
        });

        setToNULL.addActionListener(e -> {
            JTable jTable = getSelected();
            jTable.setValueAt(null,row,column);
        });

        delete_record.addActionListener(e -> MainForm.deleteSelectedRow());
        delete_field.addActionListener(e -> {
            try {
                Boolean aBoolean = new DDLTool().alterTable(JdbcUtil.getConnection(), SQLParameter.TABLE_DROP, (String) MainForm.dataSheetSelection.getSelectedItem(), TableInitialization.numericField[MainForm.table.getSelectedColumn()]);
                if (aBoolean){
                    System.out.println("删除成功");
                }else {
                    System.out.println("删除失败");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        designTable.addActionListener(e -> new CreateTable(jFrame));
        /*add_field.addActionListener(e -> {
            try {
                Boolean aBoolean = new DDLTool().alterTable(JdbcUtil.getConnection(), SQLParameter.TABLE_ADD, (String) MainForm.dataSheetSelection.getSelectedItem(), TableInitialization.numericField[MainForm.table.getSelectedColumn()]);
                if (aBoolean){
                    System.out.println("添加成功");
                }else {
                    System.out.println("添加失败");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });*/


        refresh.addActionListener(e -> MainForm.refreshTable());

        this.add(setToBlankString);
        this.add(setToNULL);
        this.addSeparator();
        this.add(delete_record);
        this.add(delete_field);
        // this.add(add_field);
        this.add(designTable);
        this.addSeparator();
        {
            JMenuItem structureAndData = createMenuItem("结构和数据");
            JMenuItem structure = createMenuItem("仅结构");
            dumpSQL.add(structureAndData);
            dumpSQL.add(structure);
            this.add(dumpSQL);
        }
        this.addSeparator();

        this.add(copy);
        {
            JMenuItem copyAs = createMenu("复制为");
            copyAs.add(insert_statement);
            copyAs.add(update_statement);
            this.add(copyAs);
        }
        this.add(stick);
        this.add(cut);
        this.addSeparator();
        this.add(display);
        this.add(refresh);
        this.pack();


    }

    private JTable getSelected(){
        JTable jTable = MainForm.returnTable();
        row = jTable.getSelectedRow();
        column = jTable.getSelectedColumn();
        return jTable;
    }


    @Override
    public Object getTheSpecificInformationOfTheCurrentMenu() {
        return null;
    }
}
