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
package io.github.markbernard.jnotepad.action;

import io.github.markbernard.jnotepad.ApplicationPreferences;
import io.github.markbernard.jnotepad.IconGenerator;
import io.github.markbernard.jnotepad.JNotepad;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Takes care of all actions for the Format menu. Menu actions will be internal classes of this class. 
 * 
 * @author Mark Bernard
 */
public class FormatAction extends AbstractAction {
    private static final long serialVersionUID = -2739705341207106213L;

    /**
     * Set up values for the Format menu
     */
    public FormatAction() {
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
        putValue(Action.NAME, "Format");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * @author Mark Bernard
     */
    public static class WordWrapAction extends AbstractAction {
        private static final long serialVersionUID = -7155740870533895459L;
        private static WordWrapAction instance;
        
        private JNotepad jNotepad;

        /**
         * @param jNotepad
         * @return Single instance
         */
        public static WordWrapAction getInstance(JNotepad jNotepad) {
            if (instance == null) {
                instance = new WordWrapAction(jNotepad);
            }
            
            return instance;
        }
        
        /**
         * Set up values for the Word Wrap menu
         * 
         * @param jNotepad 
         */
        private WordWrapAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
            putValue(Action.NAME, "Word Wrap");
            putValue(Action.SHORT_DESCRIPTION, "Toggle soft word wrap on or off.");
            putValue(Action.SMALL_ICON, IconGenerator.loadIcon("/res/icons/WordWrapSmall" +
                    (ApplicationPreferences.isWordWrap() ? "On" : "Off") + ".png"));
            putValue(Action.LARGE_ICON_KEY, IconGenerator.loadIcon("/res/icons/WordWrap" +
                    (ApplicationPreferences.isWordWrap() ? "On" : "Off") + ".png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ApplicationPreferences.setWordWrap(!ApplicationPreferences.isWordWrap());
                    putValue(Action.SMALL_ICON, IconGenerator.loadIcon("/res/icons/WordWrapSmall" +
                            (ApplicationPreferences.isWordWrap() ? "On" : "Off") + ".png"));
                    putValue(Action.LARGE_ICON_KEY, IconGenerator.loadIcon("/res/icons/WordWrap" +
                            (ApplicationPreferences.isWordWrap() ? "On" : "Off") + ".png"));
                    jNotepad.wordWrap();
                }
            }, "Word Wrap").start();
        }
        
    }

    /**
     * @author Mark Bernard
     */
    public static class FontAction extends AbstractAction {
        private static final long serialVersionUID = -7155740870533895459L;
        private JNotepad jNotepad;

        /**
         * Set up values for the Word Wrap menu
         * 
         * @param jNotepad 
         */
        public FontAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(Action.NAME, "Font...");
            putValue(Action.SMALL_ICON, 
                    IconGenerator.loadIcon("/res/icons/FontSmall.png"));
            putValue(Action.LARGE_ICON_KEY, 
                    IconGenerator.loadIcon("/res/icons/Font.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepad.font();
                }
            }, "Font").start();
        }
    }
    
    /**
     * @author Mark Bernard
     */
    public static class EncodingAction extends AbstractAction {
        private static final long serialVersionUID = -3348895239090369348L;
        private JNotepad jNotepad;

        /**
         * Show dialog to switch encodings.
         * 
         * @param jNotepad
         */
        public EncodingAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
            putValue(Action.NAME, "Change Encoding...");
            putValue(Action.SHORT_DESCRIPTION, "Select a different encoding to save your document under.");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepad.encoding();
                }
            }, "Encoding").start();
        }
    }
}
