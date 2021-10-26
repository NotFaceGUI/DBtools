package com.notface.form;

import javax.swing.*;

public class TypePanel extends JPanel{
    public static final TypePanel TYPE_PANEL = new TypePanel();
    public static String[] typeOfData = {"TINYINT","SMALLINT","MEDIUMINT","INT","BIGINT","FLOAT","DOUBLE","DECIMAL","DATE","TIME","YEAR",
            "DATETIME","TIMESTAMP","CHAR","VARCHAR","TINYBLOB","TINYTEXT","BLOB","TEXT","MEDIUMBLOB","MEDIUMTEXT",
            "LONGBLOB","LONGTEXT"};
    public TypePanel() {
        JLabel columnName = new JLabel("列名:");
        JLabel type = new JLabel("类型:");
        JTextField textField = new JTextField();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for (String typeOfDatum : typeOfData) {
            comboBoxModel.addElement(typeOfDatum);
        }
        JComboBox<String> typeComboBox = new JComboBox<>(comboBoxModel);

        this.add(columnName);
        this.add(textField);
        this.add(type);
        this.add(typeComboBox);
    }

    public static JComboBox<String> returnTypeCheckbox(){
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for (String typeOfDatum : typeOfData) {
            comboBoxModel.addElement(typeOfDatum);
        }
        return new JComboBox<>(comboBoxModel);
    }
}
