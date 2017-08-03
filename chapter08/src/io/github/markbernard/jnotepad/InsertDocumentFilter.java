/**
 * 
 */
package io.github.markbernard.jnotepad;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author mxb122
 *
 */
public class InsertDocumentFilter extends DocumentFilter {

    private TextDocument textDocument;

    /**
     * @param textDocument 
     */
    public InsertDocumentFilter(TextDocument textDocument) {
        this.textDocument = textDocument;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        super.insertString(fb, offset, string, attr);
        textDocument.processInsert(offset + string.length(), string.length());
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        super.replace(fb, offset, length, text, attrs);
        textDocument.processInsert(offset + text.length(), text.length());
    }

}
