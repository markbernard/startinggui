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

import io.github.markbernard.jnotepad.IconGenerator;
import io.github.markbernard.jnotepad.JNotepad;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * @author Mark Bernard
 */
public class HelpAction extends AbstractAction {
    private static final long serialVersionUID = -4083560520034375524L;

    /**
     * 
     */
    public HelpAction() {
        putValue(NAME, "Help");
        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
    
    /**
     * @author Mark Bernard
     */
    public static class ViewHelpAction extends AbstractAction {
        private static final long serialVersionUID = -1640517285601598321L;
        private JNotepad jNotepad;

        /**
         * @param jNotepad
         */
        public ViewHelpAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(NAME, "View Help");
            putValue(MNEMONIC_KEY, KeyEvent.VK_H);
            putValue(Action.SHORT_DESCRIPTION, "Open help pages.");
            putValue(Action.SMALL_ICON, IconGenerator.loadIcon("/res/icons/HelpSmall.png"));
            putValue(Action.LARGE_ICON_KEY, IconGenerator.loadIcon("/res/icons/Help.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> jNotepad.help(), "View Help").start();
        }
    }


    /**
     * @author Mark Bernard
     */
    public static class AboutAction extends AbstractAction {
        private static final long serialVersionUID = -1640517285601598321L;
        private JNotepad jNotepad;

        /**
         * @param jNotepad
         */
        public AboutAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(NAME, "About");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(Action.SMALL_ICON, 
                    IconGenerator.loadIcon("/res/icons/AboutSmall.png"));
            putValue(Action.LARGE_ICON_KEY, 
                    IconGenerator.loadIcon("/res/icons/About.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> jNotepad.about(), "About").start();
        }
    }
}
