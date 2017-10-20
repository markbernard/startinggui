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
    private SimpleIcon simpleIcon;

    /**
     * @param jNotepad 
     * @param componentName 
     */
    public TabComponent(JNotepad jNotepad, String componentName) {
        this.jNotepad = jNotepad;
        setLayout(new BorderLayout(5, 0));
        simpleIcon = new SimpleIcon(IconGenerator.loadIcon("/res/icons/TabClose.png"), 
                IconGenerator.loadIcon("/res/icons/TabCloseHover.png"), IconGenerator.loadIcon("/res/icons/TabClosePressed.png"));
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
    public void mouseEntered(MouseEvent e) {
        simpleIcon.setHover(true);
        simpleIcon.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        simpleIcon.setHover(false);
        simpleIcon.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        simpleIcon.setPressed(true);
        simpleIcon.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        simpleIcon.setPressed(false);
        simpleIcon.repaint();
    }

    class SimpleIcon extends JComponent {
        private static final long serialVersionUID = 4289034267693173133L;

        private Icon icon;
        private Icon hoverIcon;
        private Icon pressedIcon;
        
        private boolean hover;
        private boolean pressed;
        
        public SimpleIcon(Icon icon, Icon hoverIcon, Icon pressedIcon) {
            this.icon = icon;
            this.hoverIcon = hoverIcon;
            this.pressedIcon = pressedIcon;
            setToolTipText("Click to close");
        }
        
        public void paintComponent(Graphics g) {
            if (pressed) {
                pressedIcon.paintIcon(this, g, 0, 0);
            } else if (hover) {
                hoverIcon.paintIcon(this, g, 0, 0);
            } else {
                icon.paintIcon(this, g, 0, 0);
            }
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(icon.getIconWidth(), icon.getIconHeight());
        }

        /**
         * @param hover the hover to set
         */
        public void setHover(boolean hover) {
            this.hover = hover;
        }

        /**
         * @param pressed the pressed to set
         */
        public void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

    }
}
