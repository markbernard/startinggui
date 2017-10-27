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
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import io.github.markbernard.jnotepad.action.FileAction;

/**
 * Main application class.
 * 
 * @author Mark Bernard
 */
public class JNotepad extends JPanel implements WindowListener, KeyListener {
    private static final long serialVersionUID = -2119311360500754201L;
    private static final String APPLICATION_TITLE = "JNotepad";
    private static final String NEW_FILE_NAME = "Untitled";

    private JFrame parentFrame;
    private JTextArea textArea;
    
    private boolean dirty;
    private String fileName;
    private PrintRequestAttributeSet printRequestAttributeSet;

    /**
     * Set up the application before showing the window.
     * 
     * @param parentFrame The main application window.
     */
    public JNotepad(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        fileName = NEW_FILE_NAME;
        ApplicationPreferences.loadPrefs(parentFrame);
        dirty = false;
        setTitle();
        parentFrame.addWindowListener(this);
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);
        createMenus();
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
    
    private void createMenus() {
        JMenuBar bar = new JMenuBar();
        parentFrame.setJMenuBar(bar);
        
        JMenu fileMenu = new JMenu(new FileAction());
        bar.add(fileMenu);
        JMenuItem fileNewItem = new JMenuItem(new FileAction.NewAction(this));
        fileMenu.add(fileNewItem);
        JMenuItem fileOpenItem = new JMenuItem(new FileAction.OpenAction(this));
        fileMenu.add(fileOpenItem);
        JMenuItem fileSaveItem = new JMenuItem(new FileAction.SaveAction(this));
        fileMenu.add(fileSaveItem);
        JMenuItem fileSaveAsItem = new JMenuItem(new FileAction.SaveAsAction(this));
        fileMenu.add(fileSaveAsItem);
        fileMenu.addSeparator();
        JMenuItem filePageSetupItem = new JMenuItem(new FileAction.PageSetupAction(this));
        fileMenu.add(filePageSetupItem);
        JMenuItem filePrintItem = new JMenuItem(new FileAction.PrintAction(this));
        fileMenu.add(filePrintItem);
        fileMenu.addSeparator();
        JMenuItem fileExitItem = new JMenuItem(new FileAction.ExitAction(this));
        fileMenu.add(fileExitItem);
    }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                exit();
            }
        }, "Exit").start();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (!dirty) {
            dirty = true;
            setTitle();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Exits the application with cleanup.
     */
    public void exit() {
        DirtyStatus status = isDirty();
        
        boolean saveCompleted = true;
        if (status.equals(DirtyStatus.SAVE_FILE)) {
            saveCompleted = save();
        } else if (status.equals(DirtyStatus.CANCEL_ACTION)) {
            saveCompleted = false;
            parentFrame.setVisible(true);
        }

        if (saveCompleted) {
            ApplicationPreferences.savePrefs(parentFrame);
            System.exit(0);
        }
    }
    
    /**
     * Create a new document while checking to see if the current document is saved.
     */
    public void newDocument() {
        DirtyStatus status = isDirty();
        
        boolean saveCompleted = true;
        if (status.equals(DirtyStatus.SAVE_FILE)) {
            saveCompleted = save();
        } else if (status.equals(DirtyStatus.CANCEL_ACTION)) {
            saveCompleted = false;
        }

        if (saveCompleted) {
            fileName = NEW_FILE_NAME;
            textArea.setText("");
            dirty = false;
            setTitle();
        }
    }
    
    /**
     * Show a file dialog and load the selected file.
     */
    public void load() {
        DirtyStatus result = isDirty();
        
        boolean saveSuccessful = true;
        if (result.equals(DirtyStatus.SAVE_FILE)) {
            saveSuccessful = save();
        } else if (result.equals(DirtyStatus.CANCEL_ACTION)) {
            saveSuccessful = false;
        }
        
        if (saveSuccessful) {
            JNotepad self = this;
            SwingUtilities.invokeLater(() -> {
                String filePath = ApplicationPreferences.getCurrentFilePath();
                JFileChooser fileChooser = new JFileChooser(filePath);
                if (fileChooser.showOpenDialog(self) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileName = selectedFile.getName();
                    ApplicationPreferences.setCurrentFilePath(selectedFile.getParentFile().getAbsolutePath().replace("\\", "/"));
                    loadFile(selectedFile);
                }
            });
        }
    }
    
    /**
     * Save an existing document. If there is no existing document then call saveAs.
     * If a call to saveAs is made and the user cancels during the file save dialog 
     * then false will be returned.
     * 
     * @return true if the save was not interrupted, false otherwise
     */
    public boolean save() {
        if (fileName.equals(NEW_FILE_NAME)) {
            return saveAs();
        } else {
            saveFile(ApplicationPreferences.getCurrentFilePath() + "/" + fileName);
            dirty = false;
            setTitle();
            
            return true;
        }
    }
    
    private void saveFile(String path) {
        JComponent parentComponent = this;
        SwingUtilities.invokeLater(() -> {
            Writer out = null;
            
            try {
                out = new OutputStreamWriter(new FileOutputStream(path), 
                        StandardCharsets.UTF_8);
                out.write(textArea.getText());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(parentComponent, 
                        "Unable to create the file: " + path + "\n" + e.getMessage(), 
                        "Error loading file", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, 
                        "Unable to save the file: " + path, 
                        "Error loading file", JOptionPane.ERROR_MESSAGE);
            } finally {
                ResourceCleanup.close(out);
            }
        });
    }
    
    /**
     * Display a dialog to the user asking for a location and name of the file
     * to save. If the user cancels the dialog then return false.
     * 
     * @return false if the user cancels the file save dialog, true otherwise
     */
    public boolean saveAs() {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        result.put("result", Boolean.TRUE);
        JNotepad self = this;
        
        try {
            SwingUtilities.invokeAndWait(() -> {
                String filePath = ApplicationPreferences.getCurrentFilePath();
                JFileChooser fileChooser = new JFileChooser(filePath);
                if (fileChooser.showSaveDialog(self) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileName = selectedFile.getName();
                    ApplicationPreferences.setCurrentFilePath(
                            selectedFile.getParentFile().getAbsolutePath()
                            .replace("\\", "/"));
                    saveFile(selectedFile.getAbsolutePath());
                    dirty = false;
                    setTitle();
                } else {
                    result.put("result", Boolean.FALSE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", Boolean.FALSE);
        }
        
        return result.get("result").booleanValue();
    }

    private DirtyStatus isDirty() {
        final Map<String, DirtyStatus> result = new HashMap<String, DirtyStatus>();
        result.put("result", DirtyStatus.DONT_SAVE_FILE);
        JNotepad self = this;
        
        if (dirty) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    int choice = JOptionPane.showConfirmDialog(self, 
                            "There are changes in the current document.\n" +
                            "Click 'Yes' to save changes.\n" +
                            "Click 'No' to discard changes.\n" +
                            "Click 'Cancel' to stop the current action.", 
                            "Save Changes?", 
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        result.put("result", DirtyStatus.SAVE_FILE);
                    } else if (choice == JOptionPane.NO_OPTION) {
                        result.put("result", DirtyStatus.DONT_SAVE_FILE);
                    } else if (choice == JOptionPane.CANCEL_OPTION) {
                        result.put("result", DirtyStatus.CANCEL_ACTION);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return result.get("result");
    }
    
    /**
     * Load the file at the specified path while checking to see if the current document is saved.
     * 
     * @param path
     */
    public void loadFile(File path) {
        InputStreamReader in = null;
        
        try {
            in = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[32768];
            int read = -1;
            while ((read = in.read(buffer)) > -1) {
                content.append(buffer, 0, read);
            }
            textArea.setText(content.toString());
            dirty = false;
            setTitle();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to find the file: " + path, "Error loading file", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to load the file: " + path, "Error loading file", JOptionPane.ERROR_MESSAGE);
        } finally {
            ResourceCleanup.close(in);
        }
    }

    private void setTitle() {
        SwingUtilities.invokeLater(() -> {
            parentFrame.setTitle((dirty ? "*" : "") + fileName + " - " + APPLICATION_TITLE);
        });
    }

    /**
     * Show the user the page setup dialog and store the users settings.
     */
    public void pageSetup() {
        if (printRequestAttributeSet == null) {
            printRequestAttributeSet = new HashPrintRequestAttributeSet();
        }
        
        SwingUtilities.invokeLater(() -> {
            PrinterJob.getPrinterJob().pageDialog(printRequestAttributeSet);
        });
    }

    /**
     * Perform the printing request.
     */
    public void doPrint() {
        if (printRequestAttributeSet == null) {
            printRequestAttributeSet = new HashPrintRequestAttributeSet();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                textArea.print(null, 
                        new MessageFormat("page {0}"), 
                        true, 
                        PrinterJob.getPrinterJob().getPrintService(), 
                        printRequestAttributeSet, 
                        true);
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        });
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

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setLayout(new BorderLayout());
            JNotepad jNotepad = new JNotepad(frame);
            frame.add(jNotepad, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
    
    enum DirtyStatus {
        SAVE_FILE, DONT_SAVE_FILE, CANCEL_ACTION;
    }
}
