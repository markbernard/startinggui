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
package io.github.markbernard.jnotepadfx.action;

import io.github.markbernard.jnotepadfx.JNotepadFX;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Mark Bernard
 */
public class FileAction {
    /**
     * @author Mark Bernard
     */
    public static class NewAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public NewAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.newDocument();
                }
            }, "New").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class OpenAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public OpenAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.load();
                }
            }, "Load").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class SaveAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public SaveAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.save();
                }
            }, "Save").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class SaveAsAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public SaveAsAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.saveAs();
                }
            }, "Save As").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class PageSetupAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public PageSetupAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.pageSetup();
                }
            }, "Page Setup").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class PrintAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public PrintAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.doPrint();
                }
            }, "Print").start();
        }
    }

    /**
     * @author Mark Bernard
     */
    public static class ExitAction implements EventHandler<ActionEvent> {
        private JNotepadFX jNotepadFX;
        
        /**
         * @param jNotepadFX
         */
        public ExitAction(JNotepadFX jNotepadFX) {
            this.jNotepadFX = jNotepadFX;
            
        }
        @Override
        public void handle(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jNotepadFX.exit();
                }
            }, "Exit").start();
        }
    }
}
