/**
 * 
 */
package io.github.markbernard.jnotepadfx.action;

import io.github.markbernard.jnotepadfx.JNotepadFX;
import io.github.markbernard.jnotepadfx.dialog.SearchDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Mark Bernard
 *
 */
public class EditAction {
    /**
     * @author Mark Bernard
     */
    public static class UndoAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public UndoAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.undo();
                }
            }, "Undo").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class CutAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public CutAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.cut();
                }
            }, "Cut").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class CopyAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public CopyAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.copy();
                }
            }, "Copy").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class PasteAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public PasteAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.paste();
                }
            }, "Paste").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class DeleteAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public DeleteAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.delete();
                }
            }, "Delete").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class FindAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public FindAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.find();
                }
            }, "Find").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class FindNextAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        private SearchDialog searchDialog;

        /**
         * @param jNotepadFX
         * @param searchDialog 
         */
        public FindNextAction(JNotepadFX jNotepadFX, SearchDialog searchDialog) {
            this.jNotepadFX = jNotepadFX;
            this.searchDialog = searchDialog;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (searchDialog != null) {
                        jNotepadFX.setFindDownDirection(searchDialog.isFindDirectionDown());
                        jNotepadFX.setMatchCase(searchDialog.isMatchCase());
                        jNotepadFX.setFindTerm(searchDialog.getFindTerm());
                    }
                    jNotepadFX.findNext();
                }
            }, "Find Next").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class ReplaceAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public ReplaceAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.replace();
                }
            }, "Replace").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class ReplaceAllAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        private SearchDialog searchDialog;

        /**
         * @param jNotepadFX
         * @param searchDialog 
         */
        public ReplaceAllAction(JNotepadFX jNotepadFX, SearchDialog searchDialog) {
            this.jNotepadFX = jNotepadFX;
            this.searchDialog = searchDialog;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (searchDialog != null) {
                        jNotepadFX.setFindDownDirection(true);
                        jNotepadFX.setMatchCase(searchDialog.isMatchCase());
                        jNotepadFX.setFindTerm(searchDialog.getFindTerm());
                        jNotepadFX.setReplaceTerm(searchDialog.getReplaceTerm());
                    }
                    jNotepadFX.replaceAll();
                }
            }, "Replace All").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class GoToAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public GoToAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.goTo();
                }
            }, "Go To").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class SelectAllAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public SelectAllAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.selectAll();
                }
            }, "Select All").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class TimeDateAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public TimeDateAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.timeDate();
                }
            }, "Time/Date").start();
        }
    }

}
