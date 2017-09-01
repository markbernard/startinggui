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
package io.github.markbernard.jnotepad.dialog;

import io.github.markbernard.jnotepad.ResourceCleanup;
import io.github.markbernard.jnotepad.action.DialogAction;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * @author Mark Bernard
 *
 */
public abstract class BasicDialog extends JDialog implements WindowListener {
    private static final long serialVersionUID = 5010359693222917600L;

    /**
     * @param owner
     * @param title
     * @param modal
     */
    public BasicDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    protected void addEscapeToActionMap(JComponent component) {
        component.getActionMap().put("Cancel", new DialogAction.CancelAction(this));
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
    }

    protected void centerDialog() {
        Window frameOwner = getOwner();

        int width = getWidth();
        int height = getHeight();
        if (width <= 0) {
            width = 350;
        }
        if (height <= 0) {
            height = 130;
        }
        setLocation(frameOwner.getX() + (frameOwner.getWidth() - width) / 2,
                frameOwner.getY() + (frameOwner.getHeight() - height) / 2);
    }
    
    protected String loadTextFromResource(String ref) {
        String result = "";
        
        Reader in = null;
        try {
            in = new InputStreamReader(getClass().getResourceAsStream(ref), "UTF-8");
            StringBuilder content = new StringBuilder();
            char buffer[] = new char[16384];
            int read = -1;
            while ((read = in.read(buffer)) > -1) {
                content.append(buffer, 0, read);
            }
            result = content.toString();
        } catch (Exception e) {
            //should not occur since we are dealing with a local resource
            e.printStackTrace();
        } finally {
            ResourceCleanup.close(in);
        }
        
        
        return result;
    }
    
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        userExit();
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {
        dispose();
    }

    /**
     * Called when the user clicks the 'X' on the dialog window instead of one of the buttons.
     */
    protected abstract void userExit();
}
