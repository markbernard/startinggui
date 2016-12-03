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
    }
}
