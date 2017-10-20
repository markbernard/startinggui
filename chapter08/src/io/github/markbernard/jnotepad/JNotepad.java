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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.github.markbernard.jnotepad.action.EditAction;
import io.github.markbernard.jnotepad.action.FileAction;
import io.github.markbernard.jnotepad.action.FormatAction;
import io.github.markbernard.jnotepad.action.HelpAction;
import io.github.markbernard.jnotepad.action.ViewAction;
import io.github.markbernard.jnotepad.dialog.AboutDialog;
import io.github.markbernard.jnotepad.dialog.FontDialog;
import io.github.markbernard.jnotepad.dialog.SearchDialog;

/**
 * Main application class.
 * 
 * @author Mark Bernard
 */
public class JNotepad extends JPanel implements WindowListener, KeyListener {
    private static final long serialVersionUID = -2119311360500754201L;
    private static final String APPLICATION_TITLE = "JNotepad";

    private JFrame parentFrame;
    private JTabbedPane documentTabs;
    private TextDocument currentDocument;
    private JPanel statusBarPanel;
    private JLabel positionLabel;
    private JLabel capsLockLabel;
    private JLabel insertLabel;
    private JLabel readOnlyLabel;
    private JCheckBoxMenuItem formatWordWrap;
    private JToolBar toolbar;
    private JMenu fileRecentDocumentsMenu;

    private int newDocumentCounter;
    private PrintRequestAttributeSet printRequestAttributeSet;
    private String findTerm;
    private String replaceTerm;
    private boolean findDownDirection;
    private boolean matchCase;
    private boolean insertMode = true;
    private boolean readOnly = false;

    /**
     * Set up the application before showing the window.
     * 
     * @param parentFrame The main application window.
     */
    public JNotepad(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        ApplicationPreferences.loadPrefs(parentFrame);
        parentFrame.addWindowListener(this);
        setLayout(new BorderLayout());
        documentTabs = new JTabbedPane(JTabbedPane.TOP);
        add(documentTabs, BorderLayout.CENTER);
        documentTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (documentTabs.getTabCount() > 0) {
                    currentDocument = (TextDocument)documentTabs.getSelectedComponent();
                    setTitle();
                    currentDocument.shown();
                }
            }
        });
        createMenus();
        createStatusBar();
        createToolbar();
        newDocumentCounter = 0;
        newDocument();

        parentFrame.add(this, BorderLayout.CENTER);
        parentFrame.setVisible(true);
        currentDocument.shown();
        parentFrame.setIconImages(IconGenerator.loadImages("/res/icons/JNotepadIconSmall.png", "/res/icons/JNotepadIcon.png"));
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
        fileRecentDocumentsMenu = new JMenu(new FileAction.FileRecentDocumentsAction());
        fileMenu.add(fileRecentDocumentsMenu);
        updateRecentDocumentsMenu();
        fileMenu.addSeparator();
        JMenuItem fileExitItem = new JMenuItem(new FileAction.ExitAction(this));
        fileMenu.add(fileExitItem);
        
        JMenu editMenu = new JMenu(new EditAction());
        bar.add(editMenu);
        JMenuItem editUndoItem = new JMenuItem(new EditAction.UndoAction(this));
        editMenu.add(editUndoItem);
        editMenu.addSeparator();
        JMenuItem editCutItem = new JMenuItem(new EditAction.CutAction(this));
        editMenu.add(editCutItem);
        JMenuItem editCopyItem = new JMenuItem(new EditAction.CopyAction(this));
        editMenu.add(editCopyItem);
        JMenuItem editPasteItem = new JMenuItem(new EditAction.PasteAction(this));
        editMenu.add(editPasteItem);
        JMenuItem editDeleteItem = new JMenuItem(new EditAction.DeleteAction(this));
        editMenu.add(editDeleteItem);
        editMenu.addSeparator();
        JMenuItem editFindItem = new JMenuItem(new EditAction.FindAction(this));
        editMenu.add(editFindItem);
        JMenuItem editFindNextItem = new JMenuItem(new EditAction.FindNextAction(this, null));
        editMenu.add(editFindNextItem);
        JMenuItem editReplaceItem = new JMenuItem(new EditAction.ReplaceAction(this));
        editMenu.add(editReplaceItem);
        JMenuItem editGoToItem = new JMenuItem(new EditAction.GoToAction(this));
        editMenu.add(editGoToItem);
        editMenu.addSeparator();
        JMenuItem editSelectAllItem = new JMenuItem(new EditAction.SelectAllAction(this));
        editMenu.add(editSelectAllItem);
        JMenuItem editTimeDateItem = new JMenuItem(new EditAction.TimeDateAction(this));
        editMenu.add(editTimeDateItem);
        
        JMenu formatMenu = new JMenu(new FormatAction());
        bar.add(formatMenu);
        formatWordWrap = new JCheckBoxMenuItem(FormatAction.WordWrapAction.getInstance(this));
        formatMenu.add(formatWordWrap);
        formatWordWrap.setSelected(ApplicationPreferences.isWordWrap());
        JMenuItem formatFont = new JMenuItem(new FormatAction.FontAction(this));
        formatMenu.add(formatFont);
        
        JMenu viewMenu = new JMenu(new ViewAction());
        bar.add(viewMenu);
        JCheckBoxMenuItem viewStatus = new JCheckBoxMenuItem(ViewAction.StatusAction.getInstance(this));
        viewStatus.setSelected(ApplicationPreferences.isStatusBar());
        viewMenu.add(viewStatus);

        JMenu helpMenu = new JMenu(new HelpAction());
        bar.add(helpMenu);
        JMenuItem helpViewHelp = new JMenuItem(new HelpAction.ViewHelpAction(this));
        helpMenu.add(helpViewHelp);
        helpMenu.addSeparator();
        JMenuItem helpAbout = new JMenuItem(new HelpAction.AboutAction(this));
        helpMenu.add(helpAbout);
    }
    
    private void updateRecentDocumentsMenu() {
        List<String> recentDocuments = ApplicationPreferences.getRecentDocuments();
        fileRecentDocumentsMenu.removeAll();
        if (recentDocuments.size() > 0) {
            for (int i=0;i<recentDocuments.size();i++) {
                fileRecentDocumentsMenu.add(new JMenuItem(
                        new FileAction.FileRecentDocumentsAction
                        .FileRecentDocumentsOpenDocumentAction(i, 
                                recentDocuments.get(i), this)));
            }
        } else {
            JMenuItem fileRecentDocumentsNoneItem = new JMenuItem("<no recent documents>");
            fileRecentDocumentsMenu.add(fileRecentDocumentsNoneItem);
            fileRecentDocumentsNoneItem.setEnabled(false);
        }
    }
    
    private void createStatusBar() {
        statusBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel positionPanel = new JPanel(new BorderLayout());
        statusBarPanel.add(positionPanel);
        setStausBorder(positionPanel);
        positionLabel = new JLabel();
        positionPanel.add(positionLabel, BorderLayout.WEST);
        positionLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        JPanel capsLockPanel = new JPanel(new BorderLayout());
        statusBarPanel.add(capsLockPanel);
        setStausBorder(capsLockPanel);
        capsLockLabel = new JLabel("CAPS OFF");
        capsLockPanel.add(capsLockLabel, BorderLayout.WEST);
        capsLockLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        JPanel insertPanel = new JPanel(new BorderLayout());
        statusBarPanel.add(insertPanel);
        setStausBorder(insertPanel);
        insertLabel = new JLabel("INS");
        insertPanel.add(insertLabel, BorderLayout.WEST);
        insertLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        JPanel readOnlyPanel = new JPanel(new BorderLayout());
        statusBarPanel.add(readOnlyPanel);
        setStausBorder(readOnlyPanel);
        readOnlyLabel = new JLabel("Read/Write");
        readOnlyPanel.add(readOnlyLabel, BorderLayout.WEST);
        readOnlyLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        if (ApplicationPreferences.isStatusBar()) {
            add(statusBarPanel, BorderLayout.SOUTH);
        }
    }
    
    private void setStausBorder(JPanel panel) {
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED), 
                BorderFactory.createEmptyBorder(1, 3, 1, 3)));
    }
    
    /**
     * @param position
     */
    public void updateStatusBar(String position) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCursorLocation(position);
                updateCapsLock();
                updateInsertMode();
                updateReadOnly();
            }
        });
    }
    
    private void updateCursorLocation(String position) {
        positionLabel.setText(position);
    }
    
    private void updateCapsLock() {
        if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
            capsLockLabel.setForeground(Color.BLACK);
            capsLockLabel.setText("CAPS ON ");
        } else {
            capsLockLabel.setForeground(Color.LIGHT_GRAY);
            capsLockLabel.setText("CAPS OFF");
        }
    }
    
    private void updateInsertMode() {
        if (insertMode) {
            insertLabel.setForeground(Color.LIGHT_GRAY);
            insertLabel.setText("INS");
        } else {
            insertLabel.setForeground(Color.BLACK);
            insertLabel.setText("OVR");
        }
    }

    private void updateReadOnly() {
        if (readOnly) {
            readOnlyLabel.setForeground(Color.BLACK);
            readOnlyLabel.setText("Read Only ");
        } else {
            readOnlyLabel.setForeground(Color.LIGHT_GRAY);
            readOnlyLabel.setText("Read/Write");
        }
    }

    private void createToolbar() {
        toolbar = new JToolBar();
        parentFrame.add(toolbar, BorderLayout.NORTH);
        
        toolbar.add(new FileAction.NewAction(this));
        toolbar.add(new FileAction.OpenAction(this));
        toolbar.add(new FileAction.SaveAction(this));

        toolbar.addSeparator();
        toolbar.add(new FileAction.ExitAction(this));
        
        toolbar.addSeparator();
        toolbar.add(new EditAction.CutAction(this));
        toolbar.add(new EditAction.CopyAction(this));
        toolbar.add(new EditAction.PasteAction(this));

        toolbar.addSeparator();
        toolbar.add(new EditAction.FindAction(this));
        toolbar.add(new EditAction.ReplaceAction(this));

        toolbar.addSeparator();
        toolbar.add(FormatAction.WordWrapAction.getInstance(this));
        toolbar.addSeparator();
        toolbar.add(new HelpAction.ViewHelpAction(this));
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
            updateCapsLock();
        } else if (e.getKeyCode() == KeyEvent.VK_INSERT) {
            insertMode = !insertMode;
            updateInsertMode();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Exits the application with cleanup.
     */
    public void exit() {
        
        boolean saveCompleted = true;
        while (documentTabs.getTabCount() > 0) {
            TextDocument doc = (TextDocument)documentTabs.getSelectedComponent();
            saveCompleted = doc.requestClose(false);
            if (saveCompleted) {
                documentTabs.removeTabAt(documentTabs.indexOfComponent(doc));
            } else {
                break;
            }
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
        TextDocument doc = new TextDocument(this, newDocumentCounter++);
        addDocumentToTabs(doc);
    }
    
    /**
     * Show a file dialog and load the selected file.
     */
    public void load() {
        String filePath = ApplicationPreferences.getCurrentFilePath();
        JFileChooser fileChooser = new JFileChooser(filePath);
        fileChooser.setMultiSelectionEnabled(true);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File selectedFile : selectedFiles) {
                loadFile(selectedFile);
            }
        }
    }
    
    private void loadFile(File selectedFile) {
        boolean removeInitialNewDoc = false;
        if (documentTabs.getTabCount() == 1 && currentDocument.getTitle().equals("new 0") & !currentDocument.isDirty()) {
            removeInitialNewDoc = true;
        }
        TextDocument doc = new TextDocument(this, selectedFile);
        addDocumentToTabs(doc);
        ApplicationPreferences.addDocument(doc.getFullFilePath());
        updateRecentDocumentsMenu();
        if (removeInitialNewDoc) {
            documentTabs.removeTabAt(0);
        }
    }
    
    /**
     * @param filePath
     */
    public void openRecentDocument(String filePath) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean found = false;
                for (int i=0;i<documentTabs.getTabCount();i++) {
                    TextDocument doc = (TextDocument)documentTabs.getComponentAt(i);
                    if (filePath.equals(doc.getFullFilePath())) {
                        documentTabs.setSelectedComponent(doc);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    loadFile(new File(filePath));
                }
            }
        });
    }

    private void addDocumentToTabs(TextDocument doc) {
        documentTabs.addTab(null, doc);
        TabComponent tabComponent = new TabComponent(this, doc.getTitle());
        documentTabs.setTabComponentAt(documentTabs.indexOfComponent(doc), tabComponent);
        documentTabs.setSelectedComponent(doc);
    }
    
    /**
     * Save an existing document. If there is no existing document then call saveAs.
     * If a call to saveAs is made and the user cancels during the file save dialog 
     * then false will be returned.
     * 
     * @return true if the save was not interrupted, false otherwise
     */
    public boolean save() {
        boolean saved = currentDocument.save();

        if (saved) {
            ApplicationPreferences.addDocument(currentDocument.getFullFilePath());
            updateRecentDocumentsMenu();
        }
        
        return saved;
    }
    
    /**
     * Display a dialog to the user asking for a location and name of the file
     * to save. If the user cancels the dialog then return false.
     * 
     * @return false if the user cancels the file save dialog, true otherwise
     */
    public boolean saveAs() {
        boolean saved = currentDocument.saveAs();
        
        if (saved) {
            ApplicationPreferences.addDocument(currentDocument.getFullFilePath());
            updateRecentDocumentsMenu();
            setTitle();
        }
        
        return saved;
    }

    /**
     * Update the main title with the currently selected tab.
     */
    public void setTitle() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                parentFrame.setTitle(currentDocument.getTitle() + " - " + APPLICATION_TITLE);
                int index = documentTabs.indexOfComponent(currentDocument);
                TabComponent tabComponent = (TabComponent) documentTabs.getTabComponentAt(index);
                tabComponent.setName(currentDocument.getTitle());
            }
        });
    }

    /**
     * Show the user the page setup dialog and store the users settings.
     */
    public void pageSetup() {
        if (printRequestAttributeSet == null) {
            printRequestAttributeSet = new HashPrintRequestAttributeSet();
        }
        PrinterJob.getPrinterJob().pageDialog(printRequestAttributeSet);
    }

    /**
     * Perform the printing request.
     */
    public void doPrint() {
        try {
            if (printRequestAttributeSet == null) {
                printRequestAttributeSet = new HashPrintRequestAttributeSet();
            }
            currentDocument.getTextArea().print(null, 
                    new MessageFormat("page {0}"), 
                    true, 
                    PrinterJob.getPrinterJob().getPrintService(), 
                    printRequestAttributeSet, 
                    true);
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Undo the last text input
     */
    public void undo() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.undo();
            }
        });
    }
    
    /**
     * Cut the selected text and place it in the system clipboard
     */
    public void cut() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.cut();
            }
        });
    }
    
    /**
     * Copy the selected text and place it in the system clipboard
     */
    public void copy() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.copy();
            }
        });
    }
    
    /**
     * Take contents of the system clipboard, if it is text, and place it at the cursor.
     */
    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
        for (DataFlavor flavor : flavors) {
            if (flavor.isFlavorTextType() && flavor.isMimeTypeSerializedObject()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        currentDocument.performPaste(flavor, clipboard);
                    }
                });
            }
        }
    }
    
    /**
     * Delete the selected text.
     */
    public void delete() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.delete();
            }
        });
    }
    
    /**
     * Display a dialog for the user to search the text for something
     */
    public void find() {
        JNotepad self = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SearchDialog dialog = new SearchDialog(parentFrame, self, false);
                dialog.setVisible(true);
            }
        });
    }
    
    /**
     * Due to nesting issues this method does not use SwingUtilities.invokeLater.
     * Calling methods should wrap any calls to this method in SwingUtilities.invokeLater to make sure it runs on the EDT
     */
    public void findNext() {
        if (findTerm != null && !findTerm.isEmpty()) {
            currentDocument.findNext(findTerm, matchCase, findDownDirection);
        } else {
            find();
        }
    }

    /**
     * 
     */
    public void replace() {
        JNotepad self = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SearchDialog dialog = new SearchDialog(parentFrame, self, true);
                dialog.setVisible(true);
            }
        });
    }
    
    /**
     * 
     */
    public void performReplace() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.performReplace(findTerm, replaceTerm, matchCase);
            }
        });
    }
    
    /**
     * 
     */
    public void replaceAll() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.replaceAll(findTerm, replaceTerm, matchCase);
            }
        });
    }
    
    /**
     * Place the cursor on the beginning of the line number select by the user.
     */
    public void goTo() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.goTo(parentFrame);
            }
        });
    }

    /**
     * Selects all text in the JTextArea.
     */
    public void selectAll() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.selectAll();
            }
        });
    }
    
    /**
     * Insert the time and date into the text a the current cursor location.
     */
    public void timeDate() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.timeDate();
            }
        });
    }

    /**
     * Show customizable settings dialog.
     */
    public void settings() {
        
    }
    /**
     * @param findTerm the findTerm to set
     */
    public void setFindTerm(String findTerm) {
        this.findTerm = findTerm;
    }

    /**
     * @param replaceTerm the replaceTerm to set
     */
    public void setReplaceTerm(String replaceTerm) {
        this.replaceTerm = replaceTerm;
    }

    /**
     * @param matchCase the matchCase to set
     */
    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    /**
     * @return the insertMode
     */
    public boolean isInsertMode() {
        return insertMode;
    }

    /**
     * @param insertMode the insertMode to set
     */
    public void setInsertMode(boolean insertMode) {
        this.insertMode = insertMode;
    }

    /**
     * @param findDownDirection the findDownDirection to set
     */
    public void setFindDownDirection(boolean findDownDirection) {
        this.findDownDirection = findDownDirection;
    }

    /**
     * Toggle word wrapping
     */
    public void wordWrap() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentDocument.wordWrap(formatWordWrap, toolbar);
            }
        });
    }

    /**
     * Set the font to be used for editing and printing.
     */
    public void font() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FontDialog fontDialog = new FontDialog(parentFrame, 
                        ApplicationPreferences.getCurrentFont());
                if (fontDialog.showFontDialog()) {
                    Font selectedFont = fontDialog.getSelectedFont();
                    ApplicationPreferences.setCurrentFont(selectedFont);
                    for (int i=0;i<documentTabs.getTabCount();i++) {
                        TextDocument doc = (TextDocument)documentTabs.getComponentAt(i);
                        doc.setSelectedFont(selectedFont);
                    }
                }
                fontDialog.dispose();
            }
        });
    }

    /**
     * Toggle the status bar on or off
     */
    public void status() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (ApplicationPreferences.isStatusBar()) {
                    add(statusBarPanel, BorderLayout.SOUTH);
                } else {
                    remove(statusBarPanel);
                }
                validate();
                repaint();
            }
        });
    }
    
    /**
     * Show extensive help
     */
    public void help() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.ca/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show simple about dialog.
     */
    public void about() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AboutDialog aboutDialog = new AboutDialog(parentFrame);
                aboutDialog.setVisible(true);
            }
        });
    }
    
    /**
     * @param tabComponent
     */
    public void closeTab(TabComponent tabComponent) {
        int index = documentTabs.indexOfTabComponent(tabComponent);
        if (index > -1) {
            TextDocument doc = (TextDocument) documentTabs.getComponentAt(index);
            if (doc.requestClose(documentTabs.getTabCount() == 1)) {
                if (documentTabs.getTabCount() == 1) {
                    newDocument();
                }
                documentTabs.remove(doc);
            }
        }
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
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                new JNotepad(frame);
            }
        });
    }
}
