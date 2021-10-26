package com.notface.form;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

public interface AbstractPopUpMenu{
    default JMenuItem createMenuItem(String urlIcon, String menuName){
        JMenuItem item = new JMenuItem(menuName);
        try {
            item.setIcon(new ImageIcon(new URL(urlIcon)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return item;
    }

    default JMenuItem createMenuItem(String menuName) {
        return new JMenuItem(menuName);
    }

    default JMenuItem createMenu(String menuName) {
        return new JMenu(menuName);
    }

    Object getTheSpecificInformationOfTheCurrentMenu();
}
