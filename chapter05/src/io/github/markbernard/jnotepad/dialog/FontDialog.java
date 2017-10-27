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

import io.github.markbernard.jnotepad.NumberDocumentFilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Dialog to allow the user to select the font they want.
 * 
 * @author Mark Bernard
 */
public class FontDialog extends BasicDialog implements WindowListener {
    private static final long serialVersionUID = -301017949751686897L;
    private static final String[] STYLES = {"Plain", "Bold", "Italic", "Bold Italic"};
    private static final String[] SIZES = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};
    
    private JList<String> fontFamilyList;
    private JList<String> fontStyleList;
    private JList<String> fontSizeList;
    
    private JTextField fontFamilyField;
    private JTextField fontStyleField;
    private JTextField fontSizeField;
    
    private DocumentListener fontFamilyDocumentListener;
    private DocumentListener fontStyleDocumentListener;
    private DocumentListener fontSizeDocumentListener;
    
    private FontSample fontSample;
    
    private String currentFontFamily;
    private int currentFontStyle;
    private int currentFontSize;
    private boolean cancelled;

    /**
     * @param owner
     * @param currentFont 
     */
    public FontDialog(JFrame owner, Font currentFont) {
        super(owner, "Font", true);
        cancelled = true;
        addWindowListener(this);
        setResizable(false);
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        JPanel fontPanel = new JPanel(new BorderLayout());
        mainPanel.add(fontPanel, BorderLayout.NORTH);

        JPanel fontNameStylePanel = new JPanel(new BorderLayout());
        fontPanel.add(fontNameStylePanel, BorderLayout.CENTER);

        JPanel fontNamePanel = new JPanel(new BorderLayout());
        fontNameStylePanel.add(fontNamePanel, BorderLayout.CENTER);
        fontNamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        JLabel fontLabel = new JLabel("Font:");
        fontNamePanel.add(fontLabel, BorderLayout.NORTH);
        currentFontFamily = currentFont.getFamily();
        fontFamilyField = new JTextField(currentFontFamily);
        fontNamePanel.add(fontFamilyField, BorderLayout.CENTER);
        fontLabel.setLabelFor(fontFamilyField);
        fontLabel.setDisplayedMnemonic(KeyEvent.VK_F);
        addEscapeToActionMap(fontFamilyField);
        final String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontFamilyList = new JList<String>(fontFamilyNames);
        addEscapeToActionMap(fontFamilyList);
        JScrollPane fontListPane = new JScrollPane(fontFamilyList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fontNamePanel.add(fontListPane, BorderLayout.SOUTH);
        
        JPanel fontStylePanel = new JPanel(new BorderLayout());
        fontNameStylePanel.add(fontStylePanel, BorderLayout.EAST);
        fontStylePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        JLabel fontStyleLabel = new JLabel("Font style:");
        fontStylePanel.add(fontStyleLabel, BorderLayout.NORTH);
        currentFontStyle = currentFont.getStyle() & 0x03;
        fontStyleField = new JTextField(STYLES[currentFontStyle]);
        fontStylePanel.add(fontStyleField, BorderLayout.CENTER);
        fontStyleLabel.setLabelFor(fontStyleField);
        fontStyleLabel.setDisplayedMnemonic(KeyEvent.VK_Y);
        addEscapeToActionMap(fontStyleField);
        fontStyleList = new JList<String>(STYLES);
        addEscapeToActionMap(fontStyleList);
        JScrollPane fontStylePane = new JScrollPane(fontStyleList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fontStylePanel.add(fontStylePane, BorderLayout.SOUTH);
        
        JPanel fontSizePanel = new JPanel(new BorderLayout());
        fontPanel.add(fontSizePanel, BorderLayout.EAST);
        fontSizePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        JLabel fontSizeLabel = new JLabel("Size:");
        fontSizePanel.add(fontSizeLabel, BorderLayout.NORTH);
        currentFontSize = currentFont.getSize();
        fontSizeField = new JTextField(String.valueOf(currentFontSize));
        fontSizePanel.add(fontSizeField, BorderLayout.CENTER);
        fontSizeLabel.setLabelFor(fontSizeField);
        fontSizeLabel.setDisplayedMnemonic(KeyEvent.VK_S);
        addEscapeToActionMap(fontSizeField);
        ((PlainDocument)fontSizeField.getDocument()).setDocumentFilter(new NumberDocumentFilter());
        fontSizeList = new JList<String>(SIZES);
        addEscapeToActionMap(fontSizeList);
        JScrollPane fontSizePane = new JScrollPane(fontSizeList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fontSizePanel.add(fontSizePane, BorderLayout.SOUTH);
        
        JPanel samplePanel = new JPanel(new BorderLayout());
        mainPanel.add(samplePanel, BorderLayout.CENTER);
        JPanel sampleOutputPanel = new JPanel(new BorderLayout());
        samplePanel.add(sampleOutputPanel, BorderLayout.EAST);
        sampleOutputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 20),
                BorderFactory.createTitledBorder("Sample")));
        fontSample = new FontSample(currentFont);
        sampleOutputPanel.add(fontSample);
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        JPanel buttonInputPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.add(buttonInputPanel, BorderLayout.EAST);
        buttonInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        JButton okButton = new JButton("Ok");
        buttonInputPanel.add(okButton);
        addEscapeToActionMap(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        buttonInputPanel.add(cancelButton);
        addEscapeToActionMap(cancelButton);
        
        centerDialog();
        pack();
        fontFamilyList.setSelectedValue(currentFontFamily, true);
        fontStyleList.setSelectedValue(STYLES[currentFontStyle], true);
        fontSizeList.setSelectedValue(String.valueOf(currentFontSize), true);
        
        okButton.addActionListener((event) -> {
            cancelled = false;
            setVisible(false);
        });
        
        cancelButton.addActionListener((event) -> {
            cancelled = true;
            setVisible(false);
        });
        
        addListEvents();
        addTextFieldEvents(fontFamilyNames);
    }
    
    private void addListEvents() {
        fontFamilyList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                new Thread(() -> {
                    currentFontFamily = (String)fontFamilyList.getSelectedValue();
                    Document doc = fontFamilyField.getDocument();
                    doc.removeDocumentListener(fontFamilyDocumentListener);
                    fontFamilyField.setText(currentFontFamily);
                    doc.addDocumentListener(fontFamilyDocumentListener);
                    updateFont();
                }, "Font Family Select").start();
            }
        });
        
        fontStyleList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                new Thread(() -> {
                    String styleName = (String)fontStyleList.getSelectedValue();
                    currentFontStyle = 0;
                    for (currentFontStyle = 0;currentFontStyle < STYLES.length;
                            currentFontStyle++) {
                        if (styleName.equals(STYLES[currentFontStyle])) {
                            break;
                        }
                    }
                    Document doc = fontStyleField.getDocument();
                    doc.removeDocumentListener(fontStyleDocumentListener);
                    fontStyleField.setText(STYLES[currentFontStyle]);
                    doc.addDocumentListener(fontStyleDocumentListener);
                    updateFont();
                }, "Font Style Select").start();
            }
        });

        fontSizeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                new Thread(() -> {
                    currentFontSize = Integer.parseInt((String)fontSizeList.getSelectedValue());
                    Document doc = fontSizeField.getDocument();
                    doc.removeDocumentListener(fontSizeDocumentListener);
                    fontSizeField.setText(String.valueOf(currentFontSize));
                    doc.addDocumentListener(fontSizeDocumentListener);
                    updateFont();
                }, "Font Size Select").start();
            }
        });
    }
    
    private void addTextFieldEvents(final String[] fontFamilyNames) {
        fontFamilyField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                fontFamilyField.selectAll();
            }
        });
        
        fontStyleField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                fontStyleField.selectAll();
            }
        });
        
        fontSizeField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                fontSizeField.selectAll();
            }
        });
        
        fontFamilyDocumentListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkText();
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkText();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkText();
            }
            private void checkText() {
                new Thread(() -> {
                    ListSelectionListener[] currentListeners = 
                            fontFamilyList.getListSelectionListeners();
                    for (ListSelectionListener currentListener : currentListeners) {
                        fontFamilyList.removeListSelectionListener(currentListener);
                    }
                    fontFamilyList.clearSelection();
                    String textToCheck = fontFamilyField.getText();
                    for (String fontFamilyName : fontFamilyNames) {
                        if (fontFamilyName.equalsIgnoreCase(textToCheck)) {
                            fontFamilyList.setSelectedValue(fontFamilyName, true);
                            currentFontFamily = fontFamilyName;
                            break;
                        } else if (fontFamilyName.toLowerCase()
                                .startsWith(textToCheck.toLowerCase())) {
                            fontFamilyList.setSelectedValue(fontFamilyName, true);
                            currentFontFamily = fontFamilyName;
                            break;
                        }
                    }
                    updateFont();
                    for (ListSelectionListener currentListener : currentListeners) {
                        fontFamilyList.addListSelectionListener(currentListener);
                    }
                }, "Font Family Typing").start();
            }
        };
        fontFamilyField.getDocument().addDocumentListener(fontFamilyDocumentListener);

        fontStyleDocumentListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkText();
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkText();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkText();
            }
            private void checkText() {
                new Thread(() -> {
                    ListSelectionListener[] currentListeners = 
                            fontStyleList.getListSelectionListeners();
                    for (ListSelectionListener currentListener : currentListeners) {
                        fontStyleList.removeListSelectionListener(currentListener);
                    }
                    fontStyleList.clearSelection();
                    String textToCheck = fontStyleField.getText();
                    for (int i=0;i<STYLES.length;i++) {
                        if (STYLES[i].equalsIgnoreCase(textToCheck)) {
                            fontStyleList.setSelectedValue(STYLES[i], true);
                            currentFontStyle = i;
                            break;
                        }
                    }
                    updateFont();
                    for (ListSelectionListener currentListener : currentListeners) {
                        fontStyleList.addListSelectionListener(currentListener);
                    }
                }, "Font Style Typing").start();
            }
        };
        fontStyleField.getDocument().addDocumentListener(fontStyleDocumentListener);

        fontSizeDocumentListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkText();
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkText();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkText();
            }
            private void checkText() {
                new Thread(() -> {
                    ListSelectionListener[] currentListeners = 
                            fontSizeList.getListSelectionListeners();
                    for (ListSelectionListener currentListener : currentListeners) {
                        fontSizeList.removeListSelectionListener(currentListener);
                    }
                    fontSizeList.clearSelection();
                    String textToCheck = fontSizeField.getText();
                    for (String size : SIZES) {
                        if (size.equalsIgnoreCase(textToCheck)) {
                            fontSizeList.setSelectedValue(size, true);
                            break;
                        } else if (size.startsWith(textToCheck)) {
                            fontSizeList.setSelectedValue(size, true);
                            fontSizeList.clearSelection();
                            break;
                        }
                    }
                    if (!textToCheck.isEmpty()) {
                        currentFontSize = Integer.parseInt(textToCheck);
                    }
                    updateFont();
                    for (ListSelectionListener currentListener : currentListeners) {
                        fontSizeList.addListSelectionListener(currentListener);
                    }
                }, "Font Size Typing").start();
            }
        };
        fontSizeField.getDocument().addDocumentListener(fontSizeDocumentListener);
    }
    
    private void updateFont() {
        fontSample.updateFont(new Font(currentFontFamily, currentFontStyle, currentFontSize));
    }
    
    /**
     * @return The font selected by the user
     */
    public Font getSelectedFont() {
        return new Font(currentFontFamily, currentFontStyle, currentFontSize);
    }

    /**
     * @return true if the user clicked the OK button.
     */
    public boolean showFontDialog() {
        setVisible(true);
        
        return !cancelled;
    }
    
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        cancelled = true;
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    /**
     * Display some text in the currently selected font.
     */
    class FontSample extends JComponent {
        private static final long serialVersionUID = 2585836584394117034L;
        private static final String DISPLAY = "AaBbYyZz";
        private static final int WIDTH = 200;
        private static final int HEIGHT = 60;
        private final Dimension SIZE = new Dimension(WIDTH, HEIGHT);
        
        private Font font;
        
        public FontSample(Font font) {
            this.font = font;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int x = (WIDTH - fm.stringWidth(DISPLAY)) / 2;
            int y = ((HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(DISPLAY, x, y);
        }

        @Override
        public Dimension getPreferredSize() {
            return SIZE;
        }
        
        public void updateFont(Font font) {
            this.font = font;
            repaint();
        }
    }
}
