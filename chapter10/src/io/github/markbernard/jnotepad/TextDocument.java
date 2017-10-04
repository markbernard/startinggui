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
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
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
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

import io.github.markbernard.jnotepad.dialog.EncodingDialog;
import io.github.markbernard.jnotepad.dialog.GoToDialog;
import io.github.markbernard.jnotepad.parser.JavaDocumentParser;

/**
 * @author Mark Bernard
 */
public class TextDocument extends JPanel implements DocumentListener, KeyListener {
    private static final long serialVersionUID = -6937922244390572212L;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm aa yyyy-MM-dd");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String[] NEW_LINE_CHAR = {"\n", "\r\n"};
    private static final String[] REGEX_NEW_LINE_CHAR = {"\\n", "\\r\\n"};
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
    private JTextPane textPane;
    private JScrollPane textScroll;
    private LineNumberComponent lineNumberComponent;
    private AbstractDocument document;
    private int newLineTypeUsed = 0;

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
        document = (AbstractDocument)textPane.getDocument();
        document.addDocumentListener(this);
        document.setDocumentFilter(new InsertDocumentFilter(this));
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
        textPane = new JTextPane() {
            private static final long serialVersionUID = 5313646324700091181L;

            @Override
            public boolean getScrollableTracksViewportWidth() {
                //word wrap solution from https://tips4java.wordpress.com/2009/01/25/no-wrap-text-pane/
                return (!ApplicationPreferences.isWordWrap() || getUI().getPreferredSize(this).width <= getParent().getSize().width);
            }
        };
        textScroll = new JScrollPane(textPane);
        add(textScroll, BorderLayout.CENTER);
        lineNumberComponent = new LineNumberComponent(this, textScroll.getVerticalScrollBar(), textScroll);
        textScroll.setRowHeaderView(lineNumberComponent);
        textPane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateStatusBar(e.getDot());
            }
        });
        updateStatusBar(textPane.getCaretPosition());
        textPane.addKeyListener(jNotepad);
        textPane.addKeyListener(this);
        undoManager = new UndoManager();
        textPane.getDocument().addUndoableEditListener(undoManager);
        textPane.setFont(ApplicationPreferences.getCurrentFont());
        removeKeyStrokes(textPane);
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
    public JTextPane getTextArea() {
        return textPane;
    }
    
    /**
     * Cut the selected text and place it in the system clipboard
     */
    public void cut() {
        int start = textPane.getSelectionStart();
        textPane.cut();
        textPane.setSelectionStart(start);
        textPane.setSelectionEnd(start);
    }

    /**
     * Copy the selected text and place it in the system clipboard
     */
    public void copy() {
        textPane.copy();
    }

    /**
     * @param flavor
     * @param clipboard
     */
    public void performPaste(DataFlavor flavor, Clipboard clipboard) {
        try {
            String data = (String)clipboard.getData(flavor);
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            int length = end - start;
            Document doc = textPane.getDocument();
            try {
                if (length > 0) {
                    doc.remove(start, length);
                }
                doc.insertString(start, data, null);
                int location = start + data.length();
                textPane.setSelectionStart(location);
                textPane.setSelectionEnd(location);
            } catch (BadLocationException e) {
                //looks like there is nothing to remove
                //if a mistake occurs we can still try standard paste
                textPane.paste();
            }
        } catch (UnsupportedFlavorException e) {
            // generally this should not happen since we checked before hand if the flavor passed in was available.
            //if a mistake occurs we can still try standard paste
            textPane.paste();
        } catch (IOException e) {
            //if a mistake occurs we can still try standard paste
            textPane.paste();
        }
    }

    /**
     * Delete the selected text.
     */
    public void delete() {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            textPane.replaceSelection("");
            textPane.setSelectionEnd(start);
            textPane.setSelectionStart(start);
        }
    }

    /**
     * @param findTerm
     * @param matchCase
     * @param findDownDirection
     */
    public void findNext(String findTerm, boolean matchCase, boolean findDownDirection) {
        try {
            String localFindTerm = findTerm;
            if (!matchCase) {
                localFindTerm = localFindTerm.toLowerCase();
            }
            if (findDownDirection) {
                int findStart = textPane.getSelectionEnd();
                String text = document.getText(0, document.getLength());
                if (!matchCase) {
                    text = text.toLowerCase();
                }
                int index = text.indexOf(localFindTerm, findStart);
                if (index > -1) {
                    textPane.setSelectionStart(index);
                    textPane.setSelectionEnd(index + findTerm.length());
                }
            } else {
                int findStart = textPane.getSelectionStart();
                String text = document.getText(0, document.getLength()).substring(0, findStart);
                if (!matchCase) {
                    text = text.toLowerCase();
                }
                int index = text.lastIndexOf(localFindTerm);
                if (index > -1) {
                    textPane.setSelectionStart(index);
                    textPane.setSelectionEnd(index + findTerm.length());
                }
            }
        } catch (BadLocationException e) {
            //should not happen as length is retreived from the document.
            e.printStackTrace();
        }
    }

    /**
     * @param findTerm
     * @param replaceTerm
     * @param matchCase
     */
    public void performReplace(String findTerm, String replaceTerm, boolean matchCase) {
        if (findTerm != null && replaceTerm != null && !findTerm.isEmpty() && 
                textPane.getSelectionStart() != textPane.getSelectionEnd()) {
            String selectedText = textPane.getSelectedText();
            if ((matchCase && findTerm.equals(selectedText)) || 
                    (!matchCase && findTerm.equalsIgnoreCase(selectedText))) {
                textPane.replaceSelection(replaceTerm);
                textPane.setSelectionStart(textPane.getSelectionEnd());
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
            textPane.setCaretPosition(0);
            findNext(findTerm, matchCase, true);
            while (findTerm.equals(textPane.getSelectedText())) {
                textPane.replaceSelection(replaceTerm);
                textPane.setSelectionStart(textPane.getSelectionEnd());
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
        try {
            GoToDialog goToDialog = new GoToDialog(parentFrame, jNotepad);
            if (goToDialog.showDialog()) {
                int lineNumber = goToDialog.getLineNumber();
                
                if (lineNumber == 1) {
                    textPane.setCaretPosition(0);
                } else if (lineNumber >= 0) {
                    String[] lines = document.getText(0, document.getLength()).split(REGEX_NEW_LINE_CHAR[newLineTypeUsed]);
                    int pos = 0;
                    if (lineNumber <= lines.length) {
                        for (int i=1;i<lineNumber;i++) {
                            pos += lines[i - 1].length();
                            pos++;
                        }
                        textPane.setCaretPosition(pos);
                    }
                }
            }
            goToDialog.dispose();
        } catch (BadLocationException e) {
            //should not happen as length is retreived from the document.
            e.printStackTrace();
        }
    }

    /**
     * Selects all text in the JTextArea.
     */
    public void selectAll() {
        textPane.setSelectionStart(0);        
        textPane.setSelectionEnd(textPane.getText().length());
    }

    /**
     * Insert the time and date into the text a the current cursor location.
     */
    public void timeDate() {
        String timeDateString = DATE_FORMAT.format(new Date());
        int start = textPane.getSelectionStart();
        textPane.replaceSelection(timeDateString);
        textPane.setCaretPosition(start + timeDateString.length());
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
        textPane.revalidate();
    }

    /**
     * @param selectedFont
     */
    public void setSelectedFont(Font selectedFont) {
        textPane.setFont(selectedFont);
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
        textPane.requestFocusInWindow();
        updateStatusBar(textPane.getCaretPosition());
        if (!fileName.equals(newFileName)) {
            ApplicationPreferences.setCurrentFileName(fileName);
            ApplicationPreferences.setCurrentFilePath(filePath);
        }
    }
    
    private void updateStatusBar(int position) {
        try {
            //solution for row and column from http://java-sl.com/tip_row_column.html
            Point caratPosition = calculatePosition(position);
            jNotepad.updateStatusBar(String.format("Ln %d, Col %d", 
                    caratPosition.x, caratPosition.y));
        } catch (Exception e) {
            //not critical if the position in the
            //status bar does not get updated.
            e.printStackTrace();
        }
    }
    
    /**
     * @param position
     * @return Point representing the text row and column for the provided position.
     * @throws BadLocationException
     */
    public Point calculatePosition(int position) throws BadLocationException {
        String[] lines = document.getText(0, document.getLength()).split(REGEX_NEW_LINE_CHAR[newLineTypeUsed]);
        int line = 0;
        int column = 1;
        int pos = 0;
        while (pos < position && line < lines.length) {
            int size = lines[line].length() + NEW_LINE_CHAR[newLineTypeUsed].length();
            if (pos + size > position) {
                column = (position - pos) + 1;
            }
            pos += size;
            line++;
        }
        if (column == 1) {
            line++;
        }
        
        return new Point(line, column);
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
            try {
                int trimLength = length;
                if (position + trimLength > textPane.getText().length()) {
                    trimLength = textPane.getText().length() - position;
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
            String line = null;
            BufferedReader reader = new BufferedReader(in);
            while ((line = reader.readLine()) != null) {
                content.append(line + NEW_LINE_CHAR[newLineTypeUsed]);
            }
            in.close();
//            try {
                SimpleAttributeSet[] attributeSet = new SimpleAttributeSet[2];
                attributeSet[0] = new SimpleAttributeSet();
                attributeSet[1] = new SimpleAttributeSet();
                Font currentFont = ApplicationPreferences.getCurrentFont();
                StyleConstants.setFontFamily(attributeSet[0], currentFont.getFamily());
                StyleConstants.setFontSize(attributeSet[0], currentFont.getSize());
                StyleConstants.setBold(attributeSet[0], currentFont.isBold());
                StyleConstants.setItalic(attributeSet[0], currentFont.isItalic());
                StyleConstants.setForeground(attributeSet[0], Color.BLACK);
                StyleConstants.setFontFamily(attributeSet[1], currentFont.getFamily());
                StyleConstants.setFontSize(attributeSet[1], currentFont.getSize());
                StyleConstants.setBold(attributeSet[1], currentFont.isBold());
                StyleConstants.setItalic(attributeSet[1], currentFont.isItalic());
                StyleConstants.setForeground(attributeSet[1], Color.BLUE);
                int i = 0;
                reader = new BufferedReader(new StringReader(content.toString()));
                line = null;
                int pos = 0;
                
                document = new DefaultStyledDocument();
                textPane.setDocument(document);
                document.addDocumentListener(this);
                document.setDocumentFilter(new InsertDocumentFilter(this));
                JavaDocumentParser javaDocumentParser = new JavaDocumentParser();
                javaDocumentParser.parseStream(document, reader);
//                while ((line = reader.readLine()) != null) {
//                    document.insertString(pos, line + NEW_LINE_CHAR[newLineTypeUsed], attributeSet[i]);
//                    pos += (line + NEW_LINE_CHAR[newLineTypeUsed]).length();
//                    i++;
//                    if (i >= 2) {
//                        i = 0;
//                    }
//                }
//            } catch (BadLocationException e1) {
//                e1.printStackTrace();
//            }
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

    private void removeKeyStrokes(JTextPane textArea) {
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
                    out.write(document.getText(0, document.getLength()));
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(parentComponent, "Unable to create the file: " + path + "\n" + e.getMessage(), "Error saving file", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(parentComponent, "Unable to save the file: " + path, "Error saving file", JOptionPane.ERROR_MESSAGE);
                } catch (BadLocationException e) {
                    //should not happen as length is retreived from the document.
                    e.printStackTrace();
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
            int unitIncrement = textPane.getFontMetrics(textPane.getFont()).getHeight();
            int maximum = bar.getMaximum();
            int value = bar.getValue();
            value += unitIncrement;
            if (value > maximum) {
                value = maximum;
            }
            bar.setValue(value);
        } else if (e.getKeyCode() == KeyEvent.VK_UP && Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_SCROLL_LOCK)) {
            JScrollBar bar = textScroll.getVerticalScrollBar();
            int unitIncrement = textPane.getFontMetrics(textPane.getFont()).getHeight();
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
