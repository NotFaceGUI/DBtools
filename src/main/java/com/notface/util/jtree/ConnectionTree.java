package com.notface.util.jtree;

import com.notface.util.PropertiesUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;

public class ConnectionTree extends JTree {
    private static DefaultTreeModel model;
    private static DefaultMutableTreeNode node;
    private static DefaultMutableTreeNode temp;
    private static final String path = "./connection";

    public ConnectionTree() {
        node = traverseTheTree(path);

        model = new DefaultTreeModel(node);
        this.setModel(model);
        // 不显示根节点
        this.setRootVisible(false);
    }

    private DefaultMutableTreeNode traverseTheTree(String path) {
        // 创建父节点
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(new File(path).getName());
        File file = new File(path);

        if (file.exists()){
            // 如果file是个文件，则返回的是null，
            // 如果file是空目录，返回的是空数组，
            // 如果file不是空目录，则返回的是该目录下的文件和目录
            File[] files = file.listFiles();
            // 判断查询出来是否为空目录
            if (files.length == 0){
                // 添加到节点并返回
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());;
                return childNode;
            }else {
                for (File file1 : files) {
                    if (file1.isDirectory()){
                        parentNode.add(traverseTheTree(file1.getAbsolutePath()));
                    }else {
                        // 创建临时节点
                        temp = new DefaultMutableTreeNode(file1.getName());
                        parentNode.add(temp);
                    }
                }
            }
        }else {
            return null;
        }
        return parentNode;
    }

    public DefaultTreeModel updateTree(){
        node = traverseTheTree(path);
        model = new DefaultTreeModel(node);
        return model;
    }

    public static void deleteTreeNode(String connectionName){
        File file = new File(path + "/" + PropertiesUtil.getProperties().getProperty("jdbc.user"));
        deleteRecursively(file);
    }

    private static void deleteRecursively(File file){
        if (file.exists()){
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                // 代表为空目录
                if (files.length == 0){
                    file.delete();
                }else {
                    for (int i = 0; i < files.length; i++) {
                        deleteRecursively(files[i]);
                    }
                }
            }else {
                file.delete();
            }
        }
        // 删除外部空目录
        boolean delete = file.delete();
    }

}
