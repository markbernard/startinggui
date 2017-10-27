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
import java.awt.Frame;
import java.awt.GridLayout;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author Mark Bernard
 *
 */
public class EncodingDialog extends BasicDialog {
    private static final long serialVersionUID = 8540171150229302594L;
    
    private JButton okButton;
    private boolean encodingSelected;
    private String selectedEncoding;

    /**
     * @param owner
     * @param encoding 
     */
    public EncodingDialog(Frame owner, String encoding) {
        super(owner, "Select Encoding", true);
        
        encodingSelected = false;
        
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel titlePanel = new JPanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        titlePanel.add(new JLabel("Select the encoding from one of the supported encodings."));
        
        boolean enableOkButton = false;
        JPanel encodingListPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(encodingListPanel, BorderLayout.CENTER);
        encodingListPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Encodings"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        ButtonGroup encodingButtonGroup = new ButtonGroup();
        JRadioButton utf8 = createButton(StandardCharsets.UTF_8.name(), encodingListPanel, encodingButtonGroup);
        if (!enableOkButton) {
            enableOkButton = checkEncodingName(utf8, encoding, StandardCharsets.UTF_8.name());
        }
        JRadioButton utf16 = createButton(StandardCharsets.UTF_16.name(), encodingListPanel, encodingButtonGroup);
        if (!enableOkButton) {
            enableOkButton = checkEncodingName(utf16, encoding, StandardCharsets.UTF_16.name());
        }
        JRadioButton utf16be = createButton(StandardCharsets.UTF_16BE.name(), encodingListPanel, encodingButtonGroup);
        if (!enableOkButton) {
            enableOkButton = checkEncodingName(utf16be, encoding, StandardCharsets.UTF_16BE.name());
        }
        JRadioButton utf16le = createButton(StandardCharsets.UTF_16LE.name(), encodingListPanel, encodingButtonGroup);
        if (!enableOkButton) {
            enableOkButton = checkEncodingName(utf16le, encoding, StandardCharsets.UTF_16LE.name());
        }
        JRadioButton usAscii = createButton(StandardCharsets.US_ASCII.name(), encodingListPanel, encodingButtonGroup);
        if (!enableOkButton) {
            enableOkButton = checkEncodingName(usAscii, encoding, StandardCharsets.US_ASCII.name());
        }
        JRadioButton iso88591 = createButton(StandardCharsets.ISO_8859_1.name(), encodingListPanel, encodingButtonGroup);
        if (!enableOkButton) {
            enableOkButton = checkEncodingName(iso88591, encoding, StandardCharsets.ISO_8859_1.name());
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        okButton = new JButton("Ok");
        buttonPanel.add(okButton);
        getRootPane().setDefaultButton(okButton);
        addEscapeToActionMap(okButton);
        okButton.setEnabled(enableOkButton);
        okButton.addActionListener((event) -> {
            encodingSelected = true;
            setVisible(false);
        });
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        addEscapeToActionMap(cancelButton);
        cancelButton.addActionListener((event) -> {
            encodingSelected = false;
            setVisible(false);
        });
        
        centerDialog();
        pack();
    }
    
    private JRadioButton createButton(String name, JPanel encodingListPanel, ButtonGroup encodingButtonGroup) {
        JRadioButton radioButton = new JRadioButton(name);
        encodingButtonGroup.add(radioButton);
        encodingListPanel.add(radioButton);
        addEscapeToActionMap(radioButton);
        radioButton.addActionListener((event) -> {
            okButton.setEnabled(true);
            selectedEncoding = name;
        });
        
        return radioButton;
    }
    
    private boolean checkEncodingName(JRadioButton radioButton, String encoding, String name) {
        boolean result = false;
        
        if (encoding.equals(name)) {
            radioButton.setSelected(true);
            selectedEncoding = encoding;
            result = true;
        }
        
        return result;
    }
    
    /**
     * @return true if an encoding was selected.
     */
    public boolean showDialog() {
        setVisible(true);
        dispose();
        
        return encodingSelected;
    }
    /**
     * @return the encoding
     */
    public String getEncoding() {
        return selectedEncoding;
    }

    @Override
    protected void userExit() {
        encodingSelected = false;
    }
}
