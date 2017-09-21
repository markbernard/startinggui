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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.undo.UndoManager;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

import io.github.markbernard.jnotepad.dialog.EncodingDialog;
import io.github.markbernard.jnotepad.dialog.GoToDialog;

/**
 * @author Mark Bernard
 */
public class TextDocument extends JPanel implements DocumentListener, KeyListener {
    private static final long serialVersionUID = -6937922244390572212L;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm aa yyyy-MM-dd");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static Set<String> supportedEncodings = new HashSet<>();
    private static Map<String ,Set<String>> encodingAliasMap = new HashMap<>();
    
    static {
        supportedEncodings.add(StandardCharsets.ISO_8859_1.name());
        supportedEncodings.addAll(StandardCharsets.ISO_8859_1.aliases());
        supportedEncodings.add(StandardCharsets.US_ASCII.name());
        supportedEncodings.addAll(StandardCharsets.US_ASCII.aliases());
        supportedEncodings.add(StandardCharsets.UTF_16.name());
        supportedEncodings.addAll(StandardCharsets.UTF_16.aliases());
        supportedEncodings.add(StandardCharsets.UTF_16BE.name());
        supportedEncodings.addAll(StandardCharsets.UTF_16BE.aliases());
        supportedEncodings.add(StandardCharsets.UTF_16LE.name());
        supportedEncodings.addAll(StandardCharsets.UTF_16LE.aliases());
        supportedEncodings.add(StandardCharsets.UTF_8.name());
        supportedEncodings.addAll(StandardCharsets.UTF_8.aliases());

        encodingAliasMap.put(StandardCharsets.ISO_8859_1.name(), StandardCharsets.ISO_8859_1.aliases());
        encodingAliasMap.put(StandardCharsets.US_ASCII.name(), StandardCharsets.US_ASCII.aliases());
        encodingAliasMap.put(StandardCharsets.UTF_16.name(), StandardCharsets.UTF_16.aliases());
        encodingAliasMap.put(StandardCharsets.UTF_16BE.name(), StandardCharsets.UTF_16BE.aliases());
        encodingAliasMap.put(StandardCharsets.UTF_16LE.name(), StandardCharsets.UTF_16LE.aliases());
        encodingAliasMap.put(StandardCharsets.UTF_8.name(), StandardCharsets.UTF_8.aliases());
    }
    
    private JNotepad jNotepad;
    private JTextArea textArea;
    private JScrollPane textScroll;
    private LineNumberComponent lineNumberComponent;

    private String newFileName = "";
    private String fileName;
    private String filePath = "";
    private boolean dirty;
    private boolean readOnly;
    private UndoManager undoManager;
    private String encoding;

    /**
     * @param jNotepad 
     * @param documentNumber Number to place beside new in the new file name.
     */
    public TextDocument(JNotepad jNotepad, int documentNumber) {
        this.jNotepad = jNotepad;
        newFileName = "new " + documentNumber;
        fileName = newFileName;
        encoding = "UTF-8";
        createGui();
    }

    /**
     * @param jNotepad 
     * @param file Full path to the file
     */
    public TextDocument(JNotepad jNotepad, File file) {
        this.jNotepad = jNotepad;
        parseFileName(file.getAbsolutePath().replace("\\", "/"));
        createGui();
        loadFile(file);
    }
    
    private void createGui() {
        dirty = false;
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textScroll = new JScrollPane(textArea);
        add(textScroll, BorderLayout.CENTER);
        lineNumberComponent = new LineNumberComponent(textArea, textScroll.getVerticalScrollBar(), textScroll);
        textScroll.setRowHeaderView(lineNumberComponent);
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateStatusBar(e.getDot());
            }
        });
        updateStatusBar(textArea.getCaretPosition());
        textArea.setLineWrap(ApplicationPreferences.isWordWrap());
        textArea.setWrapStyleWord(true);
        textArea.addKeyListener(jNotepad);
        textArea.addKeyListener(this);
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        textArea.setFont(ApplicationPreferences.getCurrentFont());
        PlainDocument document = (PlainDocument)textArea.getDocument();
        document.addDocumentListener(this);
        document.setDocumentFilter(new InsertDocumentFilter(this));
        removeKeyStrokes(textArea);
    }
    
    /**
     * @return The title for this document.
     */
    public String getTitle() {
        return (dirty ? "*" : "") + fileName;
    }

    /**
     * @return The text.
     */
    public JTextArea getTextArea() {
        return textArea;
    }
    
    /**
     * Cut the selected text and place it in the system clipboard
     */
    public void cut() {
        int start = textArea.getSelectionStart();
        textArea.cut();
        textArea.setSelectionStart(start);
        textArea.setSelectionEnd(start);
    }

    /**
     * Copy the selected text and place it in the system clipboard
     */
    public void copy() {
        textArea.copy();
    }

    /**
     * @param flavor
     * @param clipboard
     */
    public void performPaste(DataFlavor flavor, Clipboard clipboard) {
        try {
            String data = (String)clipboard.getData(flavor);
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            int length = end - start;
            Document doc = textArea.getDocument();
            try {
                if (length > 0) {
                    doc.remove(start, length);
                }
                doc.insertString(start, data, null);
                int location = start + data.length();
                textArea.setSelectionStart(location);
                textArea.setSelectionEnd(location);
            } catch (BadLocationException e) {
                //looks like there is nothing to remove
                //if a mistake occurs we can still try standard paste
                textArea.paste();
            }
        } catch (UnsupportedFlavorException e) {
            // generally this should not happen since we checked before hand if the flavor passed in was available.
            //if a mistake occurs we can still try standard paste
            textArea.paste();
        } catch (IOException e) {
            //if a mistake occurs we can still try standard paste
            textArea.paste();
        }
    }

    /**
     * Delete the selected text.
     */
    public void delete() {
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        if (start != end) {
            textArea.replaceRange("", start, end);
            textArea.setSelectionEnd(start);
            textArea.setSelectionStart(start);
        }
    }

    /**
     * @param findTerm
     * @param matchCase
     * @param findDownDirection
     */
    public void findNext(String findTerm, boolean matchCase, boolean findDownDirection) {
        String localFindTerm = findTerm;
        if (!matchCase) {
            localFindTerm = localFindTerm.toLowerCase();
        }
        if (findDownDirection) {
            int findStart = textArea.getSelectionEnd();
            String text = textArea.getText();
            if (!matchCase) {
                text = text.toLowerCase();
            }
            int index = text.indexOf(localFindTerm, findStart);
            if (index > -1) {
                textArea.setSelectionStart(index);
                textArea.setSelectionEnd(index + findTerm.length());
            }
        } else {
            int findStart = textArea.getSelectionStart();
            String text = textArea.getText().substring(0, findStart);
            if (!matchCase) {
                text = text.toLowerCase();
            }
            int index = text.lastIndexOf(localFindTerm);
            if (index > -1) {
                textArea.setSelectionStart(index);
                textArea.setSelectionEnd(index + findTerm.length());
            }
        }
    }

    /**
     * @param findTerm
     * @param replaceTerm
     * @param matchCase
     */
    public void performReplace(String findTerm, String replaceTerm, boolean matchCase) {
        if (findTerm != null && replaceTerm != null && !findTerm.isEmpty() && 
                textArea.getSelectionStart() != textArea.getSelectionEnd()) {
            String selectedText = textArea.getSelectedText();
            if ((matchCase && findTerm.equals(selectedText)) || 
                    (!matchCase && findTerm.equalsIgnoreCase(selectedText))) {
                textArea.replaceSelection(replaceTerm);
                textArea.setSelectionStart(textArea.getSelectionEnd());
                jNotepad.findNext();
            }
        }
    }

    /**
     * @param findTerm
     * @param replaceTerm
     * @param matchCase
     */
    public void replaceAll(String findTerm, String replaceTerm, boolean matchCase) {
        if (findTerm != null && replaceTerm != null && !findTerm.isEmpty()) {
            textArea.setCaretPosition(0);
            findNext(findTerm, matchCase, true);
            while (findTerm.equals(textArea.getSelectedText())) {
                textArea.replaceSelection(replaceTerm);
                textArea.setSelectionStart(textArea.getSelectionEnd());
                findNext(findTerm, matchCase, true);
            }
        }
    }

    /**
     * Place the cursor on the beginning of the line number select by the user.
     * 
     * @param parentFrame 
     */
    public void goTo(JFrame parentFrame) {
        GoToDialog goToDialog = new GoToDialog(parentFrame, jNotepad);
        if (goToDialog.showDialog()) {
            int lineNumber = goToDialog.getLineNumber() - 1;
            
            if (lineNumber >= 0 && lineNumber <= textArea.getLineCount()) {
                try {
                    textArea.setCaretPosition(textArea.getLineStartOffset(lineNumber));
                } catch (BadLocationException e) {
                    // should not occur since we already 
                    // checked if the lineNumber is in range.
                    e.printStackTrace();
                }
            }
        }
        goToDialog.dispose();
    }

    /**
     * Selects all text in the JTextArea.
     */
    public void selectAll() {
        textArea.setSelectionStart(0);        
        textArea.setSelectionEnd(textArea.getText().length());
    }

    /**
     * Insert the time and date into the text a the current cursor location.
     */
    public void timeDate() {
        String timeDateString = DATE_FORMAT.format(new Date());
        int start = textArea.getSelectionStart();
        textArea.replaceSelection(timeDateString);
        textArea.setCaretPosition(start + timeDateString.length());
    }

    /**
     * Toggle word wrapping
     * 
     * @param formatWordWrap 
     * @param toolbar 
     */
    public void wordWrap(JCheckBoxMenuItem formatWordWrap, JToolBar toolbar) {
        formatWordWrap.setSelected(ApplicationPreferences.isWordWrap());
        formatWordWrap.repaint();
        toolbar.repaint();
        textArea.setLineWrap(ApplicationPreferences.isWordWrap());
    }

    /**
     * @param selectedFont
     */
    public void setSelectedFont(Font selectedFont) {
        textArea.setFont(selectedFont);
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return The full path to the file.
     */
    public String getFullFilePath() {
        return filePath.replace("/", FILE_SEPARATOR) + FILE_SEPARATOR + fileName;
    }

    /**
     * @return the dirty
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * @param dirty the dirty to set
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Operations to update the GUI when a document tab is selected.
     */
    public void shown() {
        textArea.requestFocusInWindow();
        updateStatusBar(textArea.getCaretPosition());
        if (!fileName.equals(newFileName)) {
            ApplicationPreferences.setCurrentFileName(fileName);
            ApplicationPreferences.setCurrentFilePath(filePath);
        }
    }
    
    private void updateStatusBar(int position) {
        try {
            int line = textArea.getLineOfOffset(position);
            int column = position - textArea.getLineStartOffset(line);
            jNotepad.updateStatusBar(String.format("Ln %d, Col %d", 
                    (line + 1), (column + 1)));
        } catch (Exception e) {
            //not critical if the position in the
            //status bar does not get updated.
            e.printStackTrace();
        }
    }

    /**
     * If overwrite mode is active then characters are removed when new characters are typed.
     * 
     * @param position
     * @param length
     */
    public void processInsert(int position, int length) {
        if (!jNotepad.isInsertMode()) {
            jNotepad.setInsertMode(true);
            Document document = textArea.getDocument();
            try {
                int trimLength = length;
                if (position + trimLength > textArea.getText().length()) {
                    trimLength = textArea.getText().length() - position;
                }
                if (trimLength > 0) {
                    document.remove(position, trimLength);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            jNotepad.setInsertMode(false);
        }
    }

    private void loadFile(File path) {
        Reader in = null;
        InputStream inStream = null;
        
        try {
            inStream = new BufferedInputStream(new FileInputStream(path));
            CharsetMatch match = new CharsetDetector().setText(inStream).detect();
            encoding = match.getName();
            if (encoding.equals("ISO-8859-1")) {
                encoding = "UTF-8";
            }
            if (supportedEncodings.contains(encoding)) {
                if (!encodingAliasMap.containsKey(encoding)) {
                    for (String encodingName : encodingAliasMap.keySet()) {
                        Set<String> alias = encodingAliasMap.get(encodingName);
                        if (alias.contains(encoding)) {
                            encoding = encodingName;
                            break;
                        }
                    }
                }
            }
            in = match.getReader();
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[32768];
            int read = -1;
            while ((read = in.read(buffer)) > -1) {
                content.append(buffer, 0, read);
            }
            in.close();
            textArea.setText(content.toString());
            undoManager.discardAllEdits();
            dirty = false;
            readOnly = !path.canWrite();
            if (!readOnly) {
                RandomAccessFile rout = null;
                FileLock lock = null;
                try {
                    rout = new RandomAccessFile(path, "rw");
                    FileChannel channel = rout.getChannel();
                    lock = channel.tryLock();
                } catch (Exception e) {
                    readOnly = true;
                } finally {
                    if (lock != null) {
                        lock.release();
                    }
                    ResourceCleanup.close(rout);
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to find the file: " + path, "Error loading file", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to load the file: " + path, "Error loading file", JOptionPane.ERROR_MESSAGE);
        } finally {
            ResourceCleanup.close(in);
            ResourceCleanup.close(inStream);
        }
    }

    private void removeKeyStrokes(JTextArea textArea) {
        InputMap targetMap = null;
        InputMap parentMap = textArea.getInputMap();
        
        while (parentMap != null) {
            targetMap = parentMap;
            parentMap = targetMap.getParent();
        }
        
        targetMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        targetMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        targetMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        targetMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
    }
    
    /**
     * Save an existing document. If there is no existing document then call saveAs.
     * If a call to saveAs is made and the user cancels during the file save dialog 
     * then false will be returned.
     * 
     * @return true if the save was not interrupted, false otherwise
     */
    public boolean save() {
        boolean result = false;
        
        if (checkEncoding()) {
            if (fileName.equals(newFileName)) {
                result = saveAs();
            } else {
                saveFile();
                dirty = false;
                
                result = true;
            }
        }
        
        return result;
    }

    /**
     * Display a dialog to the user asking for a location and name of the file
     * to save. If the user cancels the dialog then return false.
     * 
     * @return false if the user cancels the file save dialog, true otherwise
     */
    public boolean saveAs() {
        boolean result = true;
        
        if (checkEncoding()) {
            String filePath = ApplicationPreferences.getCurrentFilePath();
            JFileChooser fileChooser = new JFileChooser(filePath);
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                parseFileName(selectedFile.getAbsolutePath().replace("\\", "/"));
                saveFile();
                dirty = false;
            } else {
                result = false;
            }
        }
        
        return result;
    }
    
    private boolean checkEncoding() {
        boolean encodingOk = encodingAliasMap.containsKey(encoding);

        if (!encodingOk) {
            if (JOptionPane.showConfirmDialog(this, "<html><p>The current encoding (<b>" + encoding + "</b>) is not supported for saving.</p><p>To continue saving you must select a new encoding.</p><p>Click <b>OK</b> to change encodings.</p><p>Click <b>Cancel</b> to not save the current changes.</p><html>", "Invalid Encoding", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                EncodingDialog encodingDialog = new EncodingDialog(jNotepad.getParentFrame(), encoding);
                if (encodingDialog.showDialog()) {
                    encodingOk = true;
                    encoding = encodingDialog.getEncoding();
                }
            }
        }
        
        return encodingOk;
    }

    private void saveFile() {
        final JComponent parentComponent = this;
        final String path = filePath + FILE_SEPARATOR + fileName;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Writer out = null;
                
                try {
                    out = new OutputStreamWriter(new FileOutputStream(path), encoding);
                    out.write(textArea.getText());
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(parentComponent, "Unable to create the file: " + path + "\n" + e.getMessage(), "Error saving file", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(parentComponent, "Unable to save the file: " + path, "Error saving file", JOptionPane.ERROR_MESSAGE);
                } finally {
                    ResourceCleanup.close(out);
                }
            }
        });
    }

    /**
     * Check if this tab can be closed. If it is the last tab
     * a check is made to see if it is a new tab with no changes.
     * If it is false will be returned.
     * 
     * @param isLast true if this is the last open tab
     * @return True if the user is ok to close the document
     */
    public boolean requestClose(boolean isLast) {
        DirtyStatus status = checkDirty();
        
        boolean saveCompleted = true;
        if (status.equals(DirtyStatus.SAVE_FILE)) {
            saveCompleted = save();
        } else if (status.equals(DirtyStatus.CANCEL_ACTION) || (isLast && status.equals(DirtyStatus.DONT_SAVE_FILE) && fileName.equals(newFileName) && !dirty)) {
            saveCompleted = false;
        }
        
        return saveCompleted;
    }

    private DirtyStatus checkDirty() {
        DirtyStatus result = DirtyStatus.DONT_SAVE_FILE;
        
        if (dirty) {
            String filePath = (fileName.equals(newFileName) ? fileName : 
                ApplicationPreferences.getCurrentFilePath() + FILE_SEPARATOR + fileName);
            int choice = JOptionPane.showOptionDialog(this, 
                    "Do you want to save changes to " + filePath + "?", 
                    "JNotepad", 
                    JOptionPane.YES_NO_CANCEL_OPTION, 
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[] {"Save", "Don't Save", "Cancel"}, 
                    "Save");
            if (choice == JOptionPane.YES_OPTION) {
                result = DirtyStatus.SAVE_FILE;
            } else if (choice == JOptionPane.NO_OPTION) {
                result = DirtyStatus.DONT_SAVE_FILE;
            } else if (choice == JOptionPane.CANCEL_OPTION) {
                result = DirtyStatus.CANCEL_ACTION;
            }
        }
        
        return result;
    }

    private void parseFileName(String path) {
        int index = path.lastIndexOf("/");
        if (index > -1) {
            filePath = path.substring(0, index);
            fileName = path.substring(index + 1);
        } else {
            filePath = "";
            fileName = path;
        }
    }

    /**
     * Undo the last text input
     */
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN && Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_SCROLL_LOCK)) {
            JScrollBar bar = textScroll.getVerticalScrollBar();
            int unitIncrement = textArea.getFontMetrics(textArea.getFont()).getHeight();
            int maximum = bar.getMaximum();
            int value = bar.getValue();
            value += unitIncrement;
            if (value > maximum) {
                value = maximum;
            }
            bar.setValue(value);
        } else if (e.getKeyCode() == KeyEvent.VK_UP && Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_SCROLL_LOCK)) {
            JScrollBar bar = textScroll.getVerticalScrollBar();
            int unitIncrement = textArea.getFontMetrics(textArea.getFont()).getHeight();
            int minimum = bar.getMinimum();
            int value = bar.getValue();
            value -= unitIncrement;
            if (value < minimum) {
                value = minimum;
            }
            bar.setValue(value);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentUpdated();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentUpdated();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentUpdated();
    }
    
    private void documentUpdated() {
        if (!dirty) {
            dirty = true;
            jNotepad.setTitle();
        }
        lineNumberComponent.revalidate();
        repaint();
    }

    enum DirtyStatus {
        SAVE_FILE, DONT_SAVE_FILE, CANCEL_ACTION;
    }
}
