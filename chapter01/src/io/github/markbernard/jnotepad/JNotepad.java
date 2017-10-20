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

import io.github.markbernard.jnotepad.ApplicationPreferences;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application class.
 * 
 * @author mark
 */
public class JNotepad implements WindowListener {
    private JFrame parentFrame;

    /**
     * Set up the application before showing the window.
     * 
     * @param parentFrame The main application window.
     */
    public JNotepad(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        ApplicationPreferences.loadPrefs(parentFrame);
        parentFrame.addWindowListener(this);
        try {
            List<Image> icons = new ArrayList<>();
            icons.add(ImageIO.read(getClass().getResourceAsStream("/res/icons/JNotepadIconSmall.png")));
            icons.add(ImageIO.read(getClass().getResourceAsStream("/res/icons/JNotepadIcon.png")));
            parentFrame.setIconImages(icons);
        } catch (IOException e) {
            //as long as the image is part of the project this exception should not occur
            e.printStackTrace();
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        exit();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}

    private void exit() {
        ApplicationPreferences.savePrefs(parentFrame);
        System.exit(0);
    }

    /**
     * Main application starting point.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Make the application look native.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // System look and feel is always present.
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("JNotepad");
                new JNotepad(frame);
                frame.setLayout(new BorderLayout());
                frame.setVisible(true);
            }
        });
    }
}
