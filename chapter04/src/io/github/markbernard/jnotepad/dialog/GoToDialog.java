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
package io.github.markbernard.jnotepad.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import io.github.markbernard.jnotepad.JNotepad;
import io.github.markbernard.jnotepad.NumberDocumentFilter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

/**
 * @author Mark Bernard
 */
public class GoToDialog extends BasicDialog {
    private static final long serialVersionUID = -6782369683523682953L;
    
    private JTextField lineNumberText;
    private boolean performGoto = false;

    /**
     * Set up GUI
     * 
     * @param frame 
     * @param jNotepad 
     */
    public GoToDialog(JFrame frame, JNotepad jNotepad) {
        super(frame, "Go To Line", true);
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));
        
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(labelPanel, BorderLayout.NORTH);
        JLabel lineNumberLabel = new JLabel("Line number:");
        labelPanel.add(lineNumberLabel);
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        lineNumberText = new JTextField("1", 28);
        addEscapeToActionMap(lineNumberText);
        inputPanel.add(lineNumberText);
        lineNumberLabel.setLabelFor(lineNumberText);
        ((PlainDocument)lineNumberText.getDocument()).setDocumentFilter(new NumberDocumentFilter());
        lineNumberText.setSelectionStart(0);
        lineNumberText.setSelectionEnd(1);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        JButton goToButton = new JButton("Go To");
        addEscapeToActionMap(goToButton);
        buttonPanel.add(goToButton);
        getRootPane().setDefaultButton(goToButton);
        goToButton.setMnemonic(KeyEvent.VK_G);
        JButton cancelButton = new JButton("Cancel");
        addEscapeToActionMap(cancelButton);
        buttonPanel.add(cancelButton);
        
        goToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performGoto = true;
                setVisible(false);
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performGoto = false;
                setVisible(false);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                performGoto = false;
            }
        });
    }
    
    /**
     * @return true if the user clicked Go To, false if the user clicked cancel;
     */
    public boolean showDialog() {
        pack();
        centerDialog();
        setVisible(true);
        
        return performGoto;
    }
    
    /**
     * @return The line number the user selected.
     */
    public int getLineNumber() {
        return Integer.valueOf(lineNumberText.getText().isEmpty() ? 
                "1" : 
                    lineNumberText.getText());
    }
}
