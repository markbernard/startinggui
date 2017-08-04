/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Mark Bernard
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.markbernard.jnotepad;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Mark Bernard
 */
public class TabComponent extends JPanel implements MouseListener {
    private static final long serialVersionUID = 2208710958031050223L;
    private JNotepad jNotepad;
    private JLabel labelName;

    /**
     * @param jNotepad 
     * @param componentName 
     */
    public TabComponent(JNotepad jNotepad, String componentName) {
        this.jNotepad = jNotepad;
        setLayout(new BorderLayout(5, 0));
        SimpleIcon simpleIcon = new SimpleIcon(new IconGenerator("/res/icons/DeleteSmall.png").loadImage());
        add(simpleIcon, BorderLayout.WEST);
        simpleIcon.addMouseListener(this);
        labelName = new JLabel(componentName);
        add(labelName);
    }

    public void setName(String name) {
        labelName.setText(name);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        jNotepad.closeTab(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    class SimpleIcon extends JComponent {
        private static final long serialVersionUID = 4289034267693173133L;

        private Icon icon;
        
        public SimpleIcon(Icon icon) {
            this.icon = icon;
            setToolTipText("Click to close");
        }
        
        public void paintComponent(Graphics g) {
            icon.paintIcon(this, g, 0, 0);
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(icon.getIconWidth(), icon.getIconHeight());
        }
    }
}
