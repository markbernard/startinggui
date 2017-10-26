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
package io.github.markbernard.jnotepadfx;

import java.util.prefs.Preferences;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

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
    private static final String FONT_FAMILY = "font.family";
    private static final String FONT_BOLD = "font.bold";
    private static final String FONT_ITALIC = "font.italic";
    private static final String FONT_SIZE = "font.size";
    
    private static String currentFileName = "Untitled";
    private static String currentFilePath;
    private static boolean wordWrap;
    private static Font currentFont;
    private static boolean bold;
    private static boolean italic;
    
    /**
     * Load state from the user preferences store and apply that state to 
     * the provided application window. Use sensible defaults as required.
     * 
     * @param frame The application window.
     */
    public static void loadPrefs(Stage frame) {
        Preferences prefs = Preferences.userNodeForPackage(ApplicationPreferences.class);
        
        frame.setX(prefs.getDouble(WINDOW_X, 0));
        frame.setY(prefs.getDouble(WINDOW_Y, 0));
        frame.setWidth(prefs.getDouble(WINDOW_WIDTH, 800));
        frame.setHeight(prefs.getDouble(WINDOW_HEIGHT, 600));
        frame.setMaximized(prefs.getBoolean(WINDOW_MAXIMIZED, false));
        currentFilePath = prefs.get(LAST_FILE_PATH, System.getProperty("user.home"));
        wordWrap = prefs.getBoolean(WORD_WRAP, false);
        bold = prefs.getBoolean(FONT_BOLD, false);
        italic = prefs.getBoolean(FONT_ITALIC, false);
        String fontFamily = "monospaced";
        if (Font.getFontNames("Courier New").size() > 0) {
            fontFamily = "Courier New";
        }
        currentFont = Font.font(
                prefs.get(FONT_FAMILY, fontFamily), 
                bold ? FontWeight.BOLD : FontWeight.NORMAL, 
                italic ? FontPosture.ITALIC : FontPosture.REGULAR, 
                prefs.getDouble(FONT_SIZE, 16));
    }

    /**
     * Read state from the provided application window and store it
     * to the user preferences store.
     * 
     * @param frame The main application window.
     */
    public static void savePrefs(Stage frame) {
        Preferences prefs = Preferences.userNodeForPackage(ApplicationPreferences.class);
        
        if (frame.isMaximized()) {
            prefs.putBoolean(WINDOW_MAXIMIZED, true);
        } else {
            prefs.putBoolean(WINDOW_MAXIMIZED, false);
            prefs.putDouble(WINDOW_X, frame.getX());
            prefs.putDouble(WINDOW_Y, frame.getY());
            prefs.putDouble(WINDOW_WIDTH, frame.getWidth());
            prefs.putDouble(WINDOW_HEIGHT, frame.getHeight());
        }
        prefs.put(LAST_FILE_PATH, currentFilePath);
        prefs.putBoolean(WORD_WRAP, wordWrap);
        prefs.put(FONT_FAMILY, currentFont.getFamily());
        prefs.putBoolean(FONT_BOLD, bold);
        prefs.putBoolean(FONT_ITALIC, italic); 
        prefs.putDouble(FONT_SIZE, currentFont.getSize());
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
     * @return the currentFont
     */
    public static Font getCurrentFont() {
        return currentFont;
    }

    /**
     * @param family 
     * @param bold 
     * @param italic 
     * @param size 
     * @return The Font based on the provided inputs.
     */
    public static Font setCurrentFont(String family, boolean bold, boolean italic, double size) {
        ApplicationPreferences.bold = bold;
        ApplicationPreferences.italic = italic;
        currentFont = Font.font(family, bold ? FontWeight.BOLD : FontWeight.NORMAL, italic ? FontPosture.ITALIC : FontPosture.REGULAR, size);

        return currentFont;
    }

    /**
     * @return the bold
     */
    public static boolean isBold() {
        return bold;
    }

    /**
     * @return the italic
     */
    public static boolean isItalic() {
        return italic;
    }
}
