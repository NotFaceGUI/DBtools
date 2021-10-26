package com.notface.form;

import javax.swing.*;
import java.awt.*;

public class CustomPopup extends JDialog {
    public CustomPopup() {
        super((Dialog) null,"设置",true);
        this.setLocationRelativeTo(null);
        this.add(TypePanel.TYPE_PANEL);
        this.pack();
        this.setVisible(true);
    }
}
