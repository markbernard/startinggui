/**
 * 
 */
package io.github.markbernard.jnotepadfx.action;

import io.github.markbernard.jnotepadfx.JNotepadFX;
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.undo();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.cut();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.copy();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.paste();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.delete();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.find();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class FindNextAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;

        /**
         * @param jNotepadFX
         */
        public FindNextAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
        }

        @Override
        public void handle(ActionEvent arg0) {
            jNotepadFX.findNext();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.replace();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.goTo();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.selectAll();
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
        public void handle(ActionEvent arg0) {
            jNotepadFX.timeDate();
        }
    }

}
