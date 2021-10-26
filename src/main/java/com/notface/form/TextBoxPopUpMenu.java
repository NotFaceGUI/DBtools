package com.notface.form;


import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/*
 * 所有文本框的弹出事件
 * */
public class TextBoxPopUpMenu extends JPopupMenu implements AbstractPopUpMenu{
    public TextBoxPopUpMenu() {

        JMenuItem cut = createMenuItem("剪切");
        JMenuItem copy = createMenuItem("复制");
        JMenuItem stick = createMenuItem("粘贴");
        JMenuItem delete = createMenuItem("删除");
        JMenuItem SelectAll = createMenuItem("全选");
        cut.setEnabled(false);
        stick.setEnabled(false);
        delete.setEnabled(false);

        copy.addActionListener(e->{//复制事件
            String text = MainForm.sqlText.getSelectedText();
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        });

        SelectAll.addActionListener(e->{//全选事件
            MainForm.sqlText.requestFocus();
            MainForm.sqlText.selectAll();
        });

        this.add(cut);
        this.add(copy);
        this.add(stick);
        this.add(delete);

        this.addSeparator();//分割线
        this.add(SelectAll);


        if(MainForm.sqlText.getSelectedText()==null){//判断文字是否有选中
            copy.setEnabled(false);
        }else{
            copy.setEnabled(true);
        }
    }

    @Override
    public Object getTheSpecificInformationOfTheCurrentMenu() {
        return null;
    }
}
