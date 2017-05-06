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

import io.github.markbernard.jnotepad.JNotepad;
import io.github.markbernard.jnotepad.action.DialogAction;
import io.github.markbernard.jnotepad.action.EditAction;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Mark Bernard
 */
public class SearchDialog extends BasicDialog implements DocumentListener {
    private static final long serialVersionUID = 7765170324238442423L;

    private JTextField findField;
    private JTextField replaceField;
    private JCheckBox matchCase;
    private JRadioButton upRadio;
    private JRadioButton downRadio;
    private JButton findNextButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private boolean replace;
    
    /**
     * Set up GUI
     * 
     * @param frame 
     * @param jNotepad 
     * @param replace set to true to show replace dialog, false to only show find
     */
    public SearchDialog(JFrame frame, JNotepad jNotepad, boolean replace) {
        super(frame, (replace ? "Replace" : "Find"), false);
        this.replace = replace;
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(11, 5, 11, 5));

        JPanel inputPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        JPanel textInputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.add(textInputPanel, BorderLayout.NORTH);
        JPanel findLabelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        textInputPanel.add(findLabelPanel, BorderLayout.WEST);
        JPanel findInputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        textInputPanel.add(findInputPanel, BorderLayout.CENTER);
        findLabelPanel.add(new JLabel("Find what:"), BorderLayout.WEST);
        findField = new JTextField(21);
        addEscapeToActionMap(findField);
        findInputPanel.add(findField, BorderLayout.CENTER);
        if (replace) {
            findLabelPanel.add(new JLabel("Replace with:"), BorderLayout.WEST);
            replaceField = new JTextField(21);
            addEscapeToActionMap(replaceField);
            findInputPanel.add(replaceField, BorderLayout.CENTER);
        }

        if (!replace) {
            JPanel radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            inputPanel.add(radioButtonPanel, BorderLayout.EAST);
            upRadio = new JRadioButton("Up");
            addEscapeToActionMap(upRadio);
            radioButtonPanel.add(upRadio);
            upRadio.setMnemonic(KeyEvent.VK_U);
            downRadio = new JRadioButton("Down");
            addEscapeToActionMap(downRadio);
            radioButtonPanel.add(downRadio);
            downRadio.setMnemonic(KeyEvent.VK_D);
            downRadio.setSelected(true);
            radioButtonPanel.setBorder(BorderFactory.createTitledBorder("Direction"));
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(upRadio);
            buttonGroup.add(downRadio);
        }
        
        JPanel checkboxPanel = new JPanel(new BorderLayout());
        inputPanel.add(checkboxPanel, BorderLayout.WEST);
        matchCase = new JCheckBox(new DialogAction.MatchCaseAction());
        addEscapeToActionMap(matchCase);
        checkboxPanel.add(matchCase, BorderLayout.SOUTH);

        JPanel eastPanel = new JPanel(new BorderLayout());
        mainPanel.add(eastPanel, BorderLayout.EAST);
        eastPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 5));
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        eastPanel.add(buttonPanel, BorderLayout.NORTH);
        findNextButton = new JButton(new EditAction.FindNextAction(jNotepad, this));
        addEscapeToActionMap(findNextButton);
        buttonPanel.add(findNextButton);
        findNextButton.setEnabled(false);
        JRootPane rootPane = getRootPane();
        rootPane.setDefaultButton(findNextButton);
        if (replace) {
            replaceButton = new JButton(new DialogAction.ReplaceAction(jNotepad, this));
            addEscapeToActionMap(replaceButton);
            buttonPanel.add(replaceButton);
            replaceButton.setEnabled(false);
            replaceAllButton = new JButton(new DialogAction.ReplaceAllAction(jNotepad, this));
            addEscapeToActionMap(replaceAllButton);
            buttonPanel.add(replaceAllButton);
            replaceAllButton.setEnabled(false);
        }
        JButton cancel = new JButton("Cancel");
        addEscapeToActionMap(cancel);
        buttonPanel.add(cancel);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                dispose();
            }
            
        });

        setResizable(false);
        pack();
        findField.requestFocusInWindow();
        centerDialog();
        findField.getDocument().addDocumentListener(this);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkIfText();
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        checkIfText();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        checkIfText();
    }
    
    private void checkIfText() {
        if (findField.getText().isEmpty()) {
            findNextButton.setEnabled(false);
            if (replaceButton != null) {
                replaceButton.setEnabled(false);
            }
            if (replaceAllButton != null) {
                replaceAllButton.setEnabled(false);
            }
        } else {
            findNextButton.setEnabled(true);
            if (replaceButton != null) {
                replaceButton.setEnabled(true);
            }
            if (replaceAllButton != null) {
                replaceAllButton.setEnabled(true);
            }
        }
    }
    
    /**
     * @return the findTerm
     */
    public String getFindTerm() {
        return findField.getText();
    }
    
    /**
     * @return the replaceTerm
     */
    public String getReplaceTerm() {
        return replaceField.getText();
    }
    
    /**
     * @return the findDownDirection
     */
    public boolean isFindDirectionDown() {
        if (replace) {
            return true;
        } else {
            return downRadio.isSelected();
        }
    }
    
    /**
     * @return the matchCase
     */
    public boolean isMatchCase() {
        return matchCase.isSelected();
    }
}
