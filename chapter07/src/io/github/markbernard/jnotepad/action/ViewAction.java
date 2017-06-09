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
public class ViewAction extends AbstractAction {
    private static final long serialVersionUID = -1796930521992979702L;

    /**
     * 
     */
    public ViewAction() {
        putValue(NAME, "View");
        putValue(MNEMONIC_KEY, KeyEvent.VK_V);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * @author Mark Bernard
     */
    public static class StatusAction extends AbstractAction {
        private static final long serialVersionUID = 5337443857787040687L;
        
        private JNotepad jNotepad;

        /**
         * @param jNotepad
         */
        public StatusAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(NAME, "Status Bar");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(Action.SMALL_ICON, 
                    new IconGenerator("/res/icons/StatusBarSmall.png").loadImage());
            putValue(Action.LARGE_ICON_KEY, 
                    new IconGenerator("/res/icons/StatusBar.png").loadImage());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepad.status();
                }
            }, "Status Bar").start();
        }
    }
}
