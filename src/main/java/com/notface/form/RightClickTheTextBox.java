package com.notface.form;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RightClickTheTextBox extends JTextField {
    public RightClickTheTextBox(String text) {
        this.setText(text);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ErrorInfo.messagePopup(null,"1","1",1);

                if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {  // e.isMetaDown()
                    new TextBoxPopUpMenu().show(RightClickTheTextBox.this,e.getX(),e.getY());
                }
            }
        });
    }

    @Override
    public void setBorder(Border border) {
        // No!
    }
}
