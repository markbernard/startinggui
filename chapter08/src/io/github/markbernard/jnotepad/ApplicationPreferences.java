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

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

/**
 * Class to save and load application state between executions.
 * 
 * @author Mark Bernard
 */
public class ApplicationPreferences {
    private static final String WINDOW_X = "window.x";
    private static final String WINDOW_Y = "window.y";
    private static final String WINDOW_WIDTH = "window.width";
    private static final String WINDOW_HEIGHT = "window.height";
    private static final String WINDOW_MAXIMIZED = "window.maximized";
    private static final String LAST_FILE_PATH = "last.file.path";
    private static final String WORD_WRAP = "word.wrap";
    private static final String STATUS_BAR = "status.bar";
    private static final String FONT_FAMILY = "font.family";
    private static final String FONT_STYLE = "font.style";
    private static final String FONT_SIZE = "font.size";
    private static final String RECENT_DOCUMENTS = "recent.documents";
    
    private static String currentFileName = "Untitled";
    private static String currentFilePath;
    private static boolean wordWrap;
    private static boolean statusBar;
    private static Font currentFont;
    private static List<String> recentDocuments = new ArrayList<>();
    
    /**
     * Load state from the user preferences store and apply that state to 
     * the provided application window. Use sensible defaults as required.
     * 
     * @param frame The application window.
     */
    public static void loadPrefs(JFrame frame) {
        Preferences prefs = Preferences.userNodeForPackage(ApplicationPreferences.class);
        
        frame.setBounds(prefs.getInt(WINDOW_X, 0),
                prefs.getInt(WINDOW_Y, 0),
                prefs.getInt(WINDOW_WIDTH, 800),
                prefs.getInt(WINDOW_HEIGHT, 600));
        if (prefs.getBoolean(WINDOW_MAXIMIZED, false)) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        currentFilePath = prefs.get(LAST_FILE_PATH, System.getProperty("user.home"));
        wordWrap = prefs.getBoolean(WORD_WRAP, false);
        statusBar = prefs.getBoolean(STATUS_BAR, true);
        currentFont = new Font(prefs.get(FONT_FAMILY, "monospaced"), prefs.getInt(FONT_STYLE, Font.PLAIN), prefs.getInt(FONT_SIZE, 16));
        for (int i=0;i<9;i++) {
            String document = prefs.get(i + RECENT_DOCUMENTS, null);
            if (document != null) {
                recentDocuments.add(document);
            } else {
                break;
            }
        }
    }

    /**
     * Read state from the provided application window and store it
     * to the user preferences store.
     * 
     * @param frame The main application window.
     */
    public static void savePrefs(JFrame frame) {
        Preferences prefs = Preferences.userNodeForPackage(ApplicationPreferences.class);
        
        if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            prefs.putBoolean(WINDOW_MAXIMIZED, true);
        } else {
            prefs.putBoolean(WINDOW_MAXIMIZED, false);
            prefs.putInt(WINDOW_X, frame.getX());
            prefs.putInt(WINDOW_Y, frame.getY());
            prefs.putInt(WINDOW_WIDTH, frame.getWidth());
            prefs.putInt(WINDOW_HEIGHT, frame.getHeight());
        }
        prefs.put(LAST_FILE_PATH, currentFilePath);
        prefs.putBoolean(WORD_WRAP, wordWrap);
        prefs.putBoolean(STATUS_BAR, statusBar);
        prefs.put(FONT_FAMILY, currentFont.getFamily());
        prefs.putInt(FONT_STYLE, currentFont.getStyle());
        prefs.putInt(FONT_SIZE, currentFont.getSize());
        for (int i=0;i<recentDocuments.size();i++) {
            prefs.put(i + RECENT_DOCUMENTS, recentDocuments.get(i));
        }
    }

    /**
     * @return the currentFileName
     */
    public static String getCurrentFileName() {
        return currentFileName;
    }

    /**
     * @param currentFileName the currentFileName to set
     */
    public static void setCurrentFileName(String currentFileName) {
        ApplicationPreferences.currentFileName = currentFileName;
    }

    /**
     * @return the currentFilePath
     */
    public static String getCurrentFilePath() {
        return currentFilePath;
    }

    /**
     * @param currentFilePath the currentFilePath to set
     */
    public static void setCurrentFilePath(String currentFilePath) {
        ApplicationPreferences.currentFilePath = currentFilePath;
    }

    /**
     * @return the wordWrap
     */
    public static boolean isWordWrap() {
        return wordWrap;
    }

    /**
     * @param wordWrap the wordWrap to set
     */
    public static void setWordWrap(boolean wordWrap) {
        ApplicationPreferences.wordWrap = wordWrap;
    }

    /**
     * @return the statusBar
     */
    public static boolean isStatusBar() {
        return statusBar;
    }

    /**
     * @param statusBar the statusBar to set
     */
    public static void setStatusBar(boolean statusBar) {
        ApplicationPreferences.statusBar = statusBar;
    }

    /**
     * @return the currentFont
     */
    public static Font getCurrentFont() {
        return currentFont;
    }

    /**
     * @param currentFont the currentFont to set
     */
    public static void setCurrentFont(Font currentFont) {
        ApplicationPreferences.currentFont = currentFont;
    }
    
    /**
     * @param document
     */
    public static void addDocument(String document) {
        recentDocuments.remove(document);
        recentDocuments.add(0, document);
        while (recentDocuments.size() > 9) {
            recentDocuments.remove(9);
        }
    }

    /**
     * @return the recentDocuments
     */
    public static List<String> getRecentDocuments() {
        return new ArrayList<>(recentDocuments);
    }
}
