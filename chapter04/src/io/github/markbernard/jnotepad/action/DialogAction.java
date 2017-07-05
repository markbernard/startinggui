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

import io.github.markbernard.jnotepad.JNotepad;
import io.github.markbernard.jnotepad.dialog.SearchDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Containing class for all actions for dialogs.
 * 
 * @author Mark Bernard
 */
public class DialogAction {
    /**
     * @author Mark Bernard
     */
    public static class MatchCaseAction extends AbstractAction {
        private static final long serialVersionUID = 4675522254419238324L;

        /**
         * 
         */
        public MatchCaseAction() {
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 6);
            putValue(NAME, "Match case");
        }
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
        }
        
    }

    /**
     * @author Mark Bernard
     */
    public static class ReplaceAction extends AbstractAction {
        private static final long serialVersionUID = 4675522254419238324L;
        private JNotepad jNotepad;
        private SearchDialog searchDialog;

        /**
         * @param jNotepad 
         * @param searchDialog 
         */
        public ReplaceAction(JNotepad jNotepad, SearchDialog searchDialog) {
            this.jNotepad = jNotepad;
            this.searchDialog = searchDialog;
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            putValue(NAME, "Replace");
        }
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepad.setFindTerm(searchDialog.getFindTerm());
                    jNotepad.setReplaceTerm(searchDialog.getReplaceTerm());
                    jNotepad.setMatchCase(searchDialog.isMatchCase());
                    jNotepad.performReplace();
                }
            }, "Replace (dialog)").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class ReplaceAllAction extends AbstractAction {
        private static final long serialVersionUID = 4675522254419238324L;
        private JNotepad jNotepad;
        private SearchDialog searchDialog;

        /**
         * @param jNotepad 
         * @param searchDialog 
         */
        public ReplaceAllAction(JNotepad jNotepad, SearchDialog searchDialog) {
            this.jNotepad = jNotepad;
            this.searchDialog = searchDialog;
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 8);
            putValue(NAME, "Replace All");
        }
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepad.setFindTerm(searchDialog.getFindTerm());
                    jNotepad.setReplaceTerm(searchDialog.getReplaceTerm());
                    jNotepad.setMatchCase(searchDialog.isMatchCase());
                    jNotepad.replaceAll();
                }
            }, "Replace All").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class CancelAction extends AbstractAction {
        private static final long serialVersionUID = 4675522254419238324L;
        private JDialog dialog;

        /**
         * @param dialog 
         */
        public CancelAction(JDialog dialog) {
            this.dialog = dialog;
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
            putValue(NAME, "Cancel");
        }
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(false);
                }
            });
        }
    }
}
