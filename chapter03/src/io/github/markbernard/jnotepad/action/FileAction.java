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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import io.github.markbernard.jnotepad.JNotepad;

/**
 * Takes care of all actions for the File menu. Menu actions will be internal classes of this class.
 * 
 * @author Mark Bernard
 */
public class FileAction extends AbstractAction {
    private static final long serialVersionUID = -7881302815437732429L;

    /**
     * Set up values appropriate to the File menu.
     */
    public FileAction() {
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
        putValue(NAME, "File");
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
    }

    /**
     * Actions for the File > New menu item.
     * 
     * @author Mark Bernard
     */
    public static class NewAction extends AbstractAction {
        private static final long serialVersionUID = 8935736532927739892L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the New menu item.
         * 
         * @param jNotepad Access to the application to perform actions. 
         */
        public NewAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(ACCELERATOR_KEY, 
                    KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
            putValue(NAME, "New");
            putValue(SHORT_DESCRIPTION, "Create a new blank document.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.newDocument();
            }, "New Document").start();
        }
    }

    /**
     * Actions for the File > Open menu item.
     * 
     * @author Mark Bernard
     */
    public static class OpenAction extends AbstractAction {
        private static final long serialVersionUID = 3954227951651230619L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the Open menu item.
         * 
         * @param jNotepad Access to the application to perform actions. 
         */
        public OpenAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
            putValue(NAME, "Open...");
            putValue(SHORT_DESCRIPTION, "Load a document from disk.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.load();
            }, "Open").start();
        }
    }

    /**
     * Actions for the File > Save menu item.
     * 
     * @author Mark Bernard
     */
    public static class SaveAction extends AbstractAction {
        private static final long serialVersionUID = 980383255637754695L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the Save menu item.
         * 
         * @param jNotepad 
         */
        public SaveAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
            putValue(NAME, "Save");
            putValue(SHORT_DESCRIPTION, "Save a document to disk.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.save();
            }, "Save").start();
        }
    }

    /**
     * Actions for the File > Save As menu item.
     * 
     * @author Mark Bernard
     */
    public static class SaveAsAction extends AbstractAction {
        private static final long serialVersionUID = 8295016518814119408L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the Save As menu item.
         * 
         * @param jNotepad 
         */
        public SaveAsAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 5);
            putValue(NAME, "Save As...");
            putValue(SHORT_DESCRIPTION, "Save the document with a new name.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.saveAs();
            }, "Save As Thread").start();
        }
    }

    /**
     * Actions for the File > Page Setup menu item.
     * 
     * @author Mark Bernard
     */
    public static class PageSetupAction extends AbstractAction {
        private static final long serialVersionUID = -6577539419280079060L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the Page Setup menu item.
         * 
         * @param jNotepad 
         */
        public PageSetupAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_U);
            putValue(NAME, "Page Setup...");
            putValue(SHORT_DESCRIPTION, "Edit printer page settings.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.pageSetup();
            }, "Page Setup").start();
        }
    }

    /**
     * Actions for the File > Print menu item.
     * 
     * @author Mark Bernard
     */
    public static class PrintAction extends AbstractAction {
        private static final long serialVersionUID = -6449830362022451492L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the Print menu item.
         * 
         * @param jNotepad 
         */
        public PrintAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
            putValue(NAME, "Print...");
            putValue(SHORT_DESCRIPTION, "Open the print dialog.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.doPrint();
            }, "Print").start();
        }
    }

    /**
     * Actions for the File > Exit menu item.
     * 
     * @author Mark Bernard
     */
    public static class ExitAction extends AbstractAction {
        private static final long serialVersionUID = -3795345737189627905L;
        private JNotepad jNotepad;

        /**
         * Set up values appropriate to the Exit menu item.
         * 
         * @param jNotepad Access to the application to perform actions. 
         */
        public ExitAction(JNotepad jNotepad) {
            this.jNotepad = jNotepad;
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
            putValue(NAME, "Exit");
            putValue(SHORT_DESCRIPTION, "Exit JNotepad.");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                jNotepad.exit();
            }, "Exit").start();
        }
    }
}
